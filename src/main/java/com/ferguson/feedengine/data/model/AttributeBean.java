package com.ferguson.feedengine.data.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

@Document(indexName = "attribute")
public class AttributeBean implements ESBean{
	@Id
	private String id;
	@Field(index=false)
	private String Name;
	@Field(index=false)
	private List<String> attributeGroupLinks = new ArrayList<>();
	@Field(index=false)
	private List<String> userTypeLinks = new ArrayList<>();
	@Field(index=false)
	private Map<String, String> otherProperties = new HashMap<>();
	@Field(index=false)
	private ValidationBean validation;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public List<String> getAttributeGroupLinks() {
		return attributeGroupLinks;
	}

	public void setAttributeGroupLinks(List<String> attributeGroupLinks) {
		this.attributeGroupLinks = attributeGroupLinks;
	}

	public List<String> getUserTypeLinks() {
		return userTypeLinks;
	}

	public void setUserTypeLinks(List<String> userTypeLinks) {
		this.userTypeLinks = userTypeLinks;
	}

	public Map<String, String> getOtherProperties() {
		return otherProperties;
	}

	public void setOtherProperties(Map<String, String> otherProperties) {
		this.otherProperties = otherProperties;
	}

	public ValidationBean getValidation() {
		return validation;
	}

	public void setValidation(ValidationBean validation) {
		this.validation = validation;
	}

}
