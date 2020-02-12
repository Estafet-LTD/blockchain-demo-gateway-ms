package com.estafet.blockchain.demo.blockchain.gateway.ms.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.StringTokenizer;

public class API {

	private static final Logger LOGGER = LoggerFactory.getLogger(API.class);
	
	private String version;

	public API() {
	}

	public API(String version) {
		this.version = version;		
	}

	public String getVersion() {
		StringTokenizer tokenizer = new StringTokenizer(version.replaceAll("\\-SNAPSHOT", ""), ".");
		String p1 = tokenizer.nextToken();
		String p2 = tokenizer.nextToken();
		String p3 = tokenizer.nextToken();
		LOGGER.info("version - " + version);
		LOGGER.info("p1 - " + p1);
		LOGGER.info("p2 - " + p2);
		LOGGER.info("p3 - " + p3);
		return p1 + "." + p2 + "." + Integer.toString(Integer.parseInt(p3) - 1);
	}

}
