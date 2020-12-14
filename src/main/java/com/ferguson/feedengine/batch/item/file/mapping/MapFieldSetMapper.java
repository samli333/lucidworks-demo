package com.ferguson.feedengine.batch.item.file.mapping;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class MapFieldSetMapper implements FieldSetMapper<Map<String, String>> {

    @Override
    public Map<String, String> mapFieldSet(FieldSet fieldSet) throws BindException {
        Map<String, String> map = new HashMap<>();
        Properties properties = fieldSet.getProperties();
        properties.forEach((key, value) -> map.put(key.toString(), value.toString()));
        return map;
    }

}