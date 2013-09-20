package com.polopoly.ps.jenkins.metrics;

import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Result;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.nodes.Document;

public class LoadGeneratorResult {
	private static final Logger LOG = Logger.getLogger(LoadGeneratorResult.class.getName());
	public String metricsServletURI;
	public String wsURI;
	public String authStr;
	public String authURI;
	public String authUsername;
	public String authPassword;

	public LoadGeneratorResult() {
	}

	public void setAuthURI(String uri) {
		authURI = removeTrailingSlash(uri);
	}
	
	public void setAuthStr(String str) {
		this.authStr = str;
		if(str != null) {
			this.authUsername = authStr.split(":")[0];
			this.authPassword = authStr.split(":")[1];
		}
	}

	private String removeTrailingSlash(String uri) {
		if(uri.endsWith("/"))
			return uri.subSequence(0, uri.length() -1).toString();
		else
			return uri;
	}
	
	public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
			BuildListener listener) throws InterruptedException, IOException {

		PrintStream logger = listener.getLogger();
		InputStream is = null;
		try {
			if (!metricsServletURI.endsWith("/")) {
				metricsServletURI = metricsServletURI + "/";
			}
			
			
			URL metricsUrl = new URL(
					metricsServletURI
							+ "?name=_-_-RenderStats__--element__--ownTotal&op=ownTotal&res=hour&fmt=html&asc=false&totalop=ownTotal&col=0");
			
			Document doc = loginAndGetMetricsDocument(metricsUrl.toExternalForm());
			build.addAction(new MetricsBuildAction(build, doc, logger, wsURI, authStr));
		} catch (MalformedURLException mue) {
			build.setResult(Result.FAILURE);
			throw new IOException(
					"Failed to connect to Metrics servlet using provided address '"
							+ metricsServletURI + "'", mue);
		} catch (MetricsParseException gpe) {
			LOG.log(Level.WARNING, "Failed to parse metrics data", gpe);
			build.setResult(Result.FAILURE);
		} finally {
			if (is != null) {
				is.close();
			}
		}

		return true;
	}
	
	protected Document loginAndGetMetricsDocument(String metricsUrl) throws IOException {
		PolopolyAuthUtil authUtil = new PolopolyAuthUtil(authURI, authUsername, authPassword);
		return authUtil.getAuthenticatedURI(metricsUrl);
	}
}