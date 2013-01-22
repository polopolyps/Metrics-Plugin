package com.polopoly.ps.jenkins.metrics;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Cause;
import hudson.model.Project;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;

import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import com.polopoly.ps.abench.LoadGenerator;

/**
 * Sample {@link Builder}.
 *
 * <p>
 * Generates load on the site using org.apache.http.benchmark.HttpBenchmark.
 * </p>
 * 
 */
public class LoadGeneratorBuilder extends Builder {

	private Boolean clearMetrics;
    private LoadGeneratorParam[] loadGeneratorParams = new LoadGeneratorParam[0];

    // Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
    @DataBoundConstructor
    public LoadGeneratorBuilder(LoadGeneratorParam[] loadGeneratorParams, Boolean clearMetrics) { 
    	this.loadGeneratorParams = loadGeneratorParams;
        this.clearMetrics = clearMetrics;
    }

    /**
     * We'll use this from the <tt>config.jelly</tt>.
     */
    public boolean getClearMetrics() {
    	return clearMetrics;
    }
    
    public LoadGeneratorParam[] getLoadGeneratorParams()
    {
    	return loadGeneratorParams;
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
        // This is where you 'build' the project.
    	List<Cause> buildStepCause = new ArrayList<Cause>();
        buildStepCause.add(new Cause() {
          public String getShortDescription() {
            return "Build Step started by the load generator";
          }
        });
        listener.started(buildStepCause);
        
        if(clearMetrics) {
        	MetricsPublisher metricsPublisher = (MetricsPublisher)build.getProject().getPublishersList().get(MetricsPublisher.class);
        	String metricsURI = metricsPublisher.getMetricsServletURI();

        	clearMetricsData(listener, metricsURI);
        }
        
        for(LoadGeneratorParam loadGeneratorParam : loadGeneratorParams) {
        	try {
        		LoadGenerator loadGenerator = new LoadGenerator(loadGeneratorParam.url.toString(), loadGeneratorParam.threads, loadGeneratorParam.requests);;
        		String abResult = loadGenerator.generateLoad();
        		build.addAction(new LoadGeneratorBuildAction(abResult, loadGeneratorParam.url.toString(), loadGeneratorParam.description));
        		listener.finished(Result.SUCCESS);
        	} catch (Exception e) {
        		listener.error("Failed to generate load, " + e.getMessage());
        		listener.finished(Result.FAILURE);
        	}
        }
        return true;
    }
    
	@Override
	public Action getProjectAction(AbstractProject<?, ?> project) {
		if(project instanceof Project) {
			Action action = project.getAction(LoadGeneratorProjectAction.class);
			if(action == null) {
				return new LoadGeneratorProjectAction((Project) project);
			}
			else {
				return action;
			}
		}
		return null;
	}

    private void clearMetricsData(BuildListener listener, String metricsURI) {
        HttpClient httpclient = new DefaultHttpClient();
        PrintStream logger = listener.getLogger();
        try {
            HttpGet httpget = new HttpGet(metricsURI + "?mode=clear");

            HttpResponse response = httpclient.execute(httpget);
            if(response.getStatusLine().getStatusCode() == 200) {
            	logger.println("Cleared all metrics data");
            }
            else {
            	listener.error("Failed to clear metrics data, got response code " + response.getStatusLine().getStatusCode());
            	listener.finished(Result.FAILURE);
            }

        } catch (ClientProtocolException e) {
        	listener.error("Failed to clear metrics data, " + e.getMessage());
        	listener.finished(Result.FAILURE);
		} catch (IOException e) {
			listener.error("Failed to clear metrics data, " + e.getMessage());
			listener.finished(Result.FAILURE);
		} finally {
            httpclient.getConnectionManager().shutdown();
        }

    }

	// Overridden for better type safety.
    // If your plugin doesn't really define any property on Descriptor,
    // you don't have to do this.
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }

    /**
     * Descriptor for {@link LoadGeneratorBuilder}. Used as a singleton.
     * The class is marked as public so that it can be accessed from views.
     *
     * <p>
     * See <tt>src/main/resources/hudson/plugins/hello_world/HelloWorldBuilder/*.jelly</tt>
     * for the actual HTML fragment for the configuration screen.
     */
    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        /**
         * Performs on-the-fly validation of the form field 'url'.
         *
         * @param value
         *      This parameter receives the value that the user has typed.
         * @return
         *      Indicates the outcome of the validation. This is sent to the browser.
         */
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project types 
            return true;
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return "Load generator";
        }
    }
}

