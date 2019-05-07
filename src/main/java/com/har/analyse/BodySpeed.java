package com.har.analyse;

public class BodySpeed implements Comparable<BodySpeed>{

	private Double bodySize;
	private Double time;

	public Double getBodySize() {
		if(bodySize == null) {
			bodySize = 0.00;
		}
		return bodySize;
	}

	public void setBodySize(Double bodySize) {
		this.bodySize = bodySize;
	}

	public Double getTime() {
		if(time == null) {
			time = 0.00;
		}
		return time;
	}

	public void setTime(Double time) {
		this.time = time;
	}

	@Override
	public String toString() {
		return "BodySpeed [bodySize=" + bodySize + "::: time=" + time + "]";
	}

	@Override
	public int compareTo(BodySpeed o) {
		return time.compareTo(o.getTime());
	}

}
