package com.ctl.hyperconverse.request.model;

import java.util.List;

public class Request {
	
	private String object;
	
	private List<Entry> entry;

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public List<Entry> getEntry() {
		return entry;
	}

	public void setEntry(List<Entry> entry) {
		this.entry = entry;
	}


	
	

}
