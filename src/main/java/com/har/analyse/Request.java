package com.har.analyse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Request {

	private static final String PREFIX = "";
	private static final String PREFIX2 = "";
	private static final String PREFIX3 = "";
	private String url;
	private int bodySize;

	public String getUrl() {
		String name = url.replace(PREFIX, "").replaceAll(PREFIX2, "").replace(PREFIX3, "");
		return name;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getBodySize() {
		return bodySize;
	}

	public void setBodySize(int bodySize) {
		this.bodySize = bodySize;
	}

}
