package com.ferguson.feedengine.data.model;

public class ValueBean {

    private String id;
    private String attributeID;
    private String unitID;
    private boolean changed;
    private String value;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAttributeID() {
        return attributeID;
    }

    public void setAttributeID(String attributeID) {
        this.attributeID = attributeID;
    }

    public String getUnitID() {
        return unitID;
    }

    public void setUnitID(String unitID) {
        this.unitID = unitID;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
