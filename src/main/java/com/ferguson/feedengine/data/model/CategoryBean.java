package com.ferguson.feedengine.data.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

@Document(indexName = "category")
public class CategoryBean implements ESBean {

	@Id
	private String id;
	@Field(index=false)
	private String name;
	@Field(index=false)
	private List<String> categories = new ArrayList<>();
	@Field(index=false)
	private Map<String, String> otherProperties = new HashMap<>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public List<String> getCategories() {
		return categories;
	}

	public void setCategories(List<String> categories) {
		this.categories = categories;
	}

	public Map<String, String> getOtherProperties() {
		return otherProperties;
	}

	public void setOtherProperties(Map<String, String> otherProperties) {
		this.otherProperties = otherProperties;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
