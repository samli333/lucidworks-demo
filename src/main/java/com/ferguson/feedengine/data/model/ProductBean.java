package com.ferguson.feedengine.data.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "product")
public class ProductBean implements ESBean {

	private String id;
	private String name;
	private Map<String, String> values = new HashMap<>();
	private Map<String, List<String>> multiValue = new HashMap<>();
	private Map<String, String> otherProperties = new HashMap<>();
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Map<String, String> getValues() {
		return values;
	}
	public void setValues(Map<String, String> values) {
		this.values = values;
	}
	public Map<String, List<String>> getMultiValue() {
		return multiValue;
	}
	public void setMultiValue(Map<String, List<String>> multiValue) {
		this.multiValue = multiValue;
	}
	public Map<String, String> getOtherProperties() {
		return otherProperties;
	}
	public void setOtherProperties(Map<String, String> otherProperties) {
		this.otherProperties = otherProperties;
	}
	
	

}
