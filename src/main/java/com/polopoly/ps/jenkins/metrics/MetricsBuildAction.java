package com.polopoly.ps.jenkins.metrics;

import hudson.model.AbstractBuild;

import java.io.PrintStream;
import java.util.List;

import org.jsoup.nodes.Document;

/**
 * Action used for Metrics report on build level.
 * 
 */
public class MetricsBuildAction extends AbstractMetricsAction {
	private final AbstractBuild<?, ?> build;
	private List<MetricsData> metricsList;

	public MetricsBuildAction(AbstractBuild<?, ?> build, Document doc,
			PrintStream logger, String wsURI, String authStr) {
		this.build = build;
		MetricsReader reader = new MetricsReader(doc, build.getNumber(), wsURI, authStr);
		metricsList = reader.getMetricsList();
		logger.println("Created Metrics results");
	}

	public AbstractBuild<?, ?> getBuild() {
		return build;
	}

	public List<MetricsData> getMetricsList(){
		return metricsList;
	}
	
	@Override
	public String getUrlName() {
	
		return super.getUrlName() + "build";
	}


}
