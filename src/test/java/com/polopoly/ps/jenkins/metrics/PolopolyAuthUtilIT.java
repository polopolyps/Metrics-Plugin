package com.polopoly.ps.jenkins.metrics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.junit.Test;

public class PolopolyAuthUtilIT {
	@Test
	public void testLogin() throws IOException {
		PolopolyAuthUtil authUtil = getAuthUtilInstance();
		Response loginToDeveloperTools = authUtil.loginToPolopoly();
		String userId = loginToDeveloperTools.cookie("userId");
		assertEquals("98", userId);
		String sessionKey = loginToDeveloperTools.cookie("sessionKey");
		assertTrue(sessionKey != null);
	}
	
	@Test
	public void testLoginAndGetMetrics() throws IOException {
		PolopolyAuthUtil authUtil = getAuthUtilInstance();
		Document document = authUtil.getAuthenticatedURI("http://localhost:8080/polopolydevelopment/Metrics");
		assertTrue(document.html().contains("<title>Metrics</title>"));
	}
	
	private PolopolyAuthUtil getAuthUtilInstance() {
		PolopolyAuthUtil authUtil = new PolopolyAuthUtil("http://localhost:8080/polopolydevelopment-auth/login", "sysadmin", "sysadmin");
		return authUtil;
	}

}
