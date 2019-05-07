package com.har.analyse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Timings {

	private int blocked;
	private int dns;
	private int connect;
	private int ssl;
	private int send;
	private int wait;
	private int receive;

	public int getBlocked() {
		return blocked;
	}

	public void setBlocked(int blocked) {
		this.blocked = blocked;
	}

	public int getDns() {
		return dns;
	}

	public void setDns(int dns) {
		this.dns = dns;
	}

	public int getConnect() {
		return connect;
	}

	public void setConnect(int connect) {
		this.connect = connect;
	}

	public int getSsl() {
		return ssl;
	}

	public void setSsl(int ssl) {
		this.ssl = ssl;
	}

	public int getSend() {
		return send;
	}

	public void setSend(int send) {
		this.send = send;
	}

	public int getWait() {
		return wait;
	}

	public void setWait(int wait) {
		this.wait = wait;
	}

	public int getReceive() {
		return receive;
	}

	public void setReceive(int receive) {
		this.receive = receive;
	}

}
