package com.polopoly.ps.jenkins.metrics;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import junit.framework.Assert;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

public class MetricsReaderTest {

	@Test
	public void testReadData() throws IOException{

		MetricsReader reader;
		try {
			
			Document document = Jsoup.parse(new File("src/test/resources/com/polopoly/ps/jenkins/metrics/test1.html"), "UTF-8");
			reader = new MetricsReader(document , 1, "http://localhost:8080", "credentials");
			
			System.out.println(reader);
			Assert.assertTrue(reader.getMetricsList().size() == 19);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
}
