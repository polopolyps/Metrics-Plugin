package com.polopoly.ps.jenkins.metrics;

import hudson.model.Action;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.model.Project;
import hudson.model.Run;
import hudson.util.ChartUtil;
import hudson.util.ChartUtil.NumberOnlyBuildLabel;
import hudson.util.DataSetBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * Action used for Metrics report on project level.
 * 
 */
public class LoadGeneratorProjectAction implements Action {

	private final Project<?, ?> project;
	
	private List<String> uniqueUrls;
	private Map<String, DataSetBuilder<String, NumberOnlyBuildLabel>> timePerRequestBuildersPerUrlMap;
	private Map<String, DataSetBuilder<String, NumberOnlyBuildLabel>> requestsPerSecondBuildersPerUrlMap;
	
	public LoadGeneratorProjectAction(Project<?, ?> project) {
		this.project = project;
		this.uniqueUrls = new ArrayList<String>();
		this.timePerRequestBuildersPerUrlMap = new HashMap<String, DataSetBuilder<String, NumberOnlyBuildLabel>>();
		this.requestsPerSecondBuildersPerUrlMap = new HashMap<String, DataSetBuilder<String, NumberOnlyBuildLabel>>();
	}

	public Project<?, ?> getProject() {
		return project;
	}

	public void init() {

		DataSetBuilder<String, NumberOnlyBuildLabel> timePerRequestBuilder = new DataSetBuilder<String, NumberOnlyBuildLabel>();
		DataSetBuilder<String, NumberOnlyBuildLabel> requestsPerSecondBuilder = new DataSetBuilder<String, NumberOnlyBuildLabel>();
		
		for (Object build : project.getBuilds()) {

			AbstractBuild abstractBuild = (AbstractBuild) build;

			if (!abstractBuild.isBuilding()
					&& abstractBuild.getResult().isBetterOrEqualTo(
							Result.SUCCESS)) {
				List<Action> buildActions = abstractBuild.getActions();
				for(Iterator<Action> buildActionIterator = buildActions.iterator(); buildActionIterator.hasNext();) {
					Action buildAction = buildActionIterator.next();
					if(buildAction instanceof LoadGeneratorBuildAction) {
						LoadGeneratorBuildAction loadBuildAction = (LoadGeneratorBuildAction)buildAction;
						String url = loadBuildAction.getUrl();
						String description = loadBuildAction.getDescription();
						timePerRequestBuilder = getTimePerRequestBuilder(url);
						requestsPerSecondBuilder = getRequestsPerSecondBuilder(url);
						
						double timePerRequest = loadBuildAction.getTimePerRequest();
						if(timePerRequest != -1) {
							timePerRequestBuilder.add(timePerRequest, description, new NumberOnlyBuildLabel(abstractBuild));
						}
						double requestPerSecond = loadBuildAction.getRequestPerSecond();
						if(requestPerSecond != -1) {
							requestsPerSecondBuilder.add(requestPerSecond, description, new NumberOnlyBuildLabel(abstractBuild));						
						}
						if(!uniqueUrls.contains(url)) {
							uniqueUrls.add(url);
						}
					}
				}
			}
		}
	}
	
	private DataSetBuilder<String, NumberOnlyBuildLabel> getRequestsPerSecondBuilder(String url)
	{
		DataSetBuilder<String, NumberOnlyBuildLabel> requestsPerSecondBuilder = requestsPerSecondBuildersPerUrlMap.get(url) ;
		if(requestsPerSecondBuildersPerUrlMap.get(url) == null) {
			requestsPerSecondBuilder = new DataSetBuilder<String, NumberOnlyBuildLabel>();
			requestsPerSecondBuildersPerUrlMap.put(url, requestsPerSecondBuilder);
		} 
		return requestsPerSecondBuilder;
	}
	
	private DataSetBuilder<String, NumberOnlyBuildLabel> getTimePerRequestBuilder(String url)
	{
		DataSetBuilder<String, NumberOnlyBuildLabel> timePerRequestBuilder = timePerRequestBuildersPerUrlMap.get(url);
		if(timePerRequestBuildersPerUrlMap.get(url) == null) {
			timePerRequestBuilder = new DataSetBuilder<String, NumberOnlyBuildLabel>();
			timePerRequestBuildersPerUrlMap.put(url, timePerRequestBuilder);
		} 
		return timePerRequestBuilder;
	}

	public void doLoadTestTimePerRequestGraph(StaplerRequest request,
			StaplerResponse response, @QueryParameter String url) throws IOException {

		if (shouldReloadGraph(request, response)) {
			// Draw the common graph:
			ChartUtil.generateGraph(request, response,
					GraphUtil.createGraph(timePerRequestBuildersPerUrlMap.get(url), "Avarage rendering time (ms)"), 1200, 600);
		}
	}
	
	public void doLoadTestRequestsPerSecondGraph(StaplerRequest request,
			StaplerResponse response, @QueryParameter String url) throws IOException {

		if (shouldReloadGraph(request, response)) {
			// Draw the common graph:
			ChartUtil.generateGraph(request, response,
					GraphUtil.createGraph(requestsPerSecondBuildersPerUrlMap.get(url), "Avarage requests per second"), 1200, 600);

		}
	}

	private boolean shouldReloadGraph(StaplerRequest request,
			StaplerResponse response) throws IOException {
		return shouldReloadGraph(request, response,
				project.getLastSuccessfulBuild());
	}

	public String getIconFileName() {
		return MetricsPlugin.ICON_FILE_NAME;
	}

	public String getDisplayName() {
		return "Page load test report";
	}

	public String getUrlName() {
		return "loadTest";
	}
	
	protected boolean shouldReloadGraph(StaplerRequest request, StaplerResponse response, Run build) throws IOException {
	    return !request.checkIfModified(build.getTimestamp(), response);
	}
	
	public List<String> getUrls() {
		return uniqueUrls;
    }
}
