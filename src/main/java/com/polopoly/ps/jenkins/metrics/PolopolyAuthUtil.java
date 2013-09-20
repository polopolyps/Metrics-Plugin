package com.polopoly.ps.jenkins.metrics;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.nodes.Document;

public class PolopolyAuthUtil {
	private String loginURI;
	private String username;
	private String password;
	
	public PolopolyAuthUtil(String loginURI, String username, String password) {
		this.loginURI = loginURI;
		this.username = username;
		this.password = password;
	}
	
	public Connection.Response loginToPolopoly() throws IOException {
		Connection.Response res = Jsoup.connect(loginURI)
			    .data("j_username", username) 
			    .data("j_password", password)
			    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
			    .method(Method.POST)
			    .execute();
		return res;
	}
	
	public Document getAuthenticatedURI(String uri) throws IOException {
		Response response = loginToPolopoly();
		String sessionKey = response.cookie("sessionKey"); // you will need to check what the right cookie name is
		String userId = response.cookie("userId");

		Document doc = Jsoup.connect(uri)
				.cookie("sessionKey", sessionKey)
				.cookie("userId", userId)
				.get();

		return doc;
	}

}