package com.polopoly.ps.jenkins.metrics;

import hudson.model.Build;
import hudson.model.AbstractBuild;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoadGeneratorBuildAction extends AbstractMetricsAction {

	private static Logger LOG = Logger.getLogger(LoadGeneratorBuildAction.class.getSimpleName());
	private String abResult;
	private String url;
	private String description;
	private double timePerRequest;
	private double requestsPerSecond;
	private Pattern tprPattern = Pattern.compile(("Time per request:\\s\\s(\\d*[\\.,]\\d*)"));
	private Pattern rpsPattern = Pattern.compile(("Requests per second:\\s\\s(\\d*[\\.,]\\d*)"));
	
	private final AbstractBuild build;
	
	
	public LoadGeneratorBuildAction(AbstractBuild build, String abResult, String url, String description) {
		this.build = build;
		this.abResult = abResult;
		this.url = url;
		this.description = description;
		parseResult();
	}

	private void parseResult() {
		try {
			Matcher matcher = tprPattern.matcher(abResult);
			matcher.find();
			timePerRequest = Double.parseDouble(matcher.group(1).replace(',', '.'));
		}
		catch(Exception e) {
			LOG.log(Level.WARNING, "Failed to parse time per request from AB result", e);
			timePerRequest = -1;
		}
		try {
			Matcher matcher = rpsPattern.matcher(abResult);
			matcher.find();
			requestsPerSecond = Double.parseDouble(matcher.group(1).replace(',', '.'));
		}
		catch(Exception e) {
			LOG.log(Level.WARNING, "Failed to parse requests per second from AB result", e);
			requestsPerSecond = -1;
		}
		 
	}

	public double getTimePerRequest() {
		return this.timePerRequest;
	}
	
	public double getRequestPerSecond() {
		return this.requestsPerSecond;
	}
	
	public String getUrl() {
		return this.url;
	}
	
	public String getDescription() {
		return this.description;
	}

	public AbstractBuild getBuild() {
		return build;
	}
	
	@Override
	public String getUrlName() {
		return null;
	}
	
	@Override
	public String getIconFileName() {
		return null;
	}
}