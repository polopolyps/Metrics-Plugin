package com.polopoly.ps.jenkins.metrics;

import hudson.model.Action;
import hudson.model.Run;

import java.io.IOException;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * Abstract class with functionality common to all Metrics actions.
 *
 */
public class AbstractMetricsAction implements Action {
   public String getIconFileName() {
      return MetricsPlugin.ICON_FILE_NAME;
   }

   public String getDisplayName() {
      return MetricsPlugin.DISPLAY_NAME;
   } 

   public String getUrlName() {
      return MetricsPlugin.URL;
   }
   
   protected boolean shouldReloadGraph(StaplerRequest request, StaplerResponse response, Run build) throws IOException {
      return !request.checkIfModified(build.getTimestamp(), response);
   }
}