package com.ferguson.feedengine.data.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document(indexName = "asset")
public class AssetBean implements ESBean {
    @Id
    private String id;
    @Field(index=false)
    private String Name;
    @Field(index=false)
    private List<ValueBean> values = new ArrayList<>();
    @Field(index=false)
    private Map<String, String> otherProperties = new HashMap<>();

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

    public List<ValueBean> getValues() {
        return values;
    }

    public void setValues(List<ValueBean> values) {
        this.values = values;
    }

    public Map<String, String> getOtherProperties() {
        return otherProperties;
    }

    public void setOtherProperties(Map<String, String> otherProperties) {
        this.otherProperties = otherProperties;
    }
}
