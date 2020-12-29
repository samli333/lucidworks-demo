package com.ferguson.feedengine.data.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

@Document(indexName = "product")
public class ProductBean implements ESBean {

	@Id
	private String id;
	@Field(index=false)
	private String name;
	@Field(index=false)
	private Map<String, String> values = new HashMap<>();
	@Field(index=false)
	private Map<String, List<String>> multiValue = new HashMap<>();
	@Field(index=false)
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
