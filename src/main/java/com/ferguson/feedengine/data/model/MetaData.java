package com.ferguson.feedengine.data.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetaData {

	private Map<String, String> values = new HashMap<>();
	private Map<String, List<String>> multiValues = new HashMap<>();

	public Map<String, String> getValues() {
		return values;
	}

	public void setValues(Map<String, String> values) {
		this.values = values;
	}

	public Map<String, List<String>> getMultiValues() {
		return multiValues;
	}

	public void setMultiValues(Map<String, List<String>> multiValues) {
		this.multiValues = multiValues;
	}
}
