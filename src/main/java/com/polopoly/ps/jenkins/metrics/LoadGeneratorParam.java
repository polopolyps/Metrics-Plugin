package com.polopoly.ps.jenkins.metrics;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.FormValidation;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletException;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

    
public class LoadGeneratorParam extends AbstractDescribableImpl<LoadGeneratorParam>{
    	
    	public URL url;
    	public String description;
    	public int threads;
    	public int requests;
    	
    	@DataBoundConstructor
    	public LoadGeneratorParam(String url, String description, String threads, String requests) {
    		try {
				this.url = new URL(url);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		this.description = description;
    		try {
    			this.threads = Integer.parseInt(threads);
    		} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		try {
    			this.requests = Integer.parseInt(requests);
    		} catch (NumberFormatException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
    	
        public String getUrl() {
            return url.toString();
        }
        
        public String getDescription() {
            return description;
        }
        public String getThreads() {
            return Integer.toString(threads);
        }
        public String getRequests() {
        	return Integer.toString(requests);
        }
        
        
        @Extension
        public static class DescriptorImpl extends Descriptor<LoadGeneratorParam> {
            public String getDisplayName() { return ""; }
            
            /**
             * Performs on-the-fly validation of the form fields 'url'.
             *
             * @param value
             *      This parameter receives the value that the user has typed.
             * @return
             *      Indicates the outcome of the validation. This is sent to the browser.
             */
            public FormValidation doCheckUrl(@QueryParameter String value)
                    throws IOException, ServletException {
    			return ValidationUtil.checkUrl(value);

            }
            
            public FormValidation doCheckThreads(@QueryParameter String value)
                    throws IOException, ServletException {
                return ValidationUtil.checkIntValue(value);
            }

            public FormValidation doCheckRequests(@QueryParameter String value)
                    throws IOException, ServletException {
                return ValidationUtil.checkIntValue(value);
            }
            
        }
}