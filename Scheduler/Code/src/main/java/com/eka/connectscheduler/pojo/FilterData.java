package com.eka.connectscheduler.pojo;

import java.util.List;

public class FilterData {
	private List<MongoOperations> filter;

	public List<MongoOperations> getFilter() {
		return filter;
	}

	public void setFilter(List<MongoOperations> filter) {
		this.filter = filter;
	}

	@Override
	public String toString() {
		return "FilterData [filter=" + filter +"]";
	}	 
}
