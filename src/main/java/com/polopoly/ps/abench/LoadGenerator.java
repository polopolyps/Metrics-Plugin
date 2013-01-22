package com.polopoly.ps.abench;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import org.apache.http.benchmark.Config;
import org.apache.http.benchmark.HttpBenchmark;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 *
 * <p>
 * Generates load on the site using org.apache.http.benchmark.HttpBenchmark.
 * </p>
 * 
 */
public class LoadGenerator {

	private static Logger logger = Logger.getLogger(LoadGenerator.class.getSimpleName()); 
	
	final static int SOCKET_TIMEOUT = 1000;
	final static int DEFAULT_NUMBER_OF_THREADS = 1;
	final static int DEFAULT_NUMBER_OF_REQUESTS = 100;
	
	private URL url;
    private int numberOfThreads;
    private int numberOfRequests;

    // Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
    @DataBoundConstructor
    public LoadGenerator(String url, int numberOfThreads, int numberOfRequests) {
    	try {
			this.url = new URL(url);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Failed to set URL", e);
		}
    	this.numberOfThreads = numberOfThreads;
    	this.numberOfRequests = numberOfRequests;
    }

    /**
     * 
     * @return result returned by http benchmark call.
     * @throws Exception
     */
    public String generateLoad() throws Exception {
        logger.info("Generating load on " + url);
        
        Config config = new Config();
        config.setSocketTimeout(SOCKET_TIMEOUT);
        config.setRequests(numberOfRequests);
        config.setThreads(numberOfThreads);
        config.setUrl(url);
        
    	HttpBenchmark ab = new HttpBenchmark(config);
    	return ab.execute();
	}
}

