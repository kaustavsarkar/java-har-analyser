package com.har.analyse;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Entry {

	private Date startedDateTime;
	private Request request;
	private Response response;
	private Timings timings;
	private int time;

	public Date getStartedDateTime() {
		return startedDateTime;
	}

	public void setStartedDateTime(Date startedDateTime) {
		this.startedDateTime = startedDateTime;
	}

	public Request getRequest() {
		return request;
	}

	public void setRequest(Request request) {
		this.request = request;
	}

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

	public Timings getTimings() {
		return timings;
	}

	public void setTimings(Timings timings) {
		this.timings = timings;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

}
