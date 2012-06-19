# Metrics plugin for Jenkins

The Metrics plugin for Jenkins adds capability for a Jenkins job to generate load on your site, presenting benchmarking figures as well as Metrics data.
The goal is to have a lightweight load test as part of you countinous integration setup.
The target installation is assumed to have Metrics data collection enabled.

## Reports

You'll get two reports from the Metrics plugin.
The first one will give the avarage time to load a page as well as the avarage requests per second your installation delivers. This is presented in a graph over time so you can see how your implementation affects the page load.

The second report will give you rendering time per output template from the Metrics data generated from the initial load. This will give you a more fine grained presentation on how the different elements evolve over time.

## Install

Currently we don't offer a build distribution of the plugin so you would have to build it yourself and install it in Jenkins.

    git clone https://github.com/polopolyps/Metrics-Plugin.git
    cd Metrics-Plugin
    mvn install

Now you should have the plugin available for you at target/metrics.hpi. To install it in Jenkins you'd upload the metrics.hpi to your Jenkins installation under. In the Jenkins GUI you'd go to Manage Jenkins -> Manage Plugins -> Advanced and there you have a section called Upload Plugin.

## Contribute

If you want to improve and/or expand the plugin, simply fork the repository, do your magic, push your changes of send us a pull request.
To run and debug it locally you'd use the following commands:

    mvn hpi:run -Djetty.port=8090
    or 
    mvnDebug hpi:run -Djetty.port=8090

The jetty.port paramater is optional but often wanted.
