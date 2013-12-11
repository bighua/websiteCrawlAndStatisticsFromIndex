package com.jcm.statistics.data;

import java.util.List;

public class Data {

	private String dateTime = null;
	
	private String name = null;
	
	private Long total = null;
	
	private Long increment = null;
	
	private List<Data> subData = null;

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public Long getIncrement() {
		return increment;
	}

	public void setIncrement(Long increment) {
		this.increment = increment;
	}

	public List<Data> getSubData() {
		return subData;
	}

	public void setSubData(List<Data> subData) {
		this.subData = subData;
	}
}
