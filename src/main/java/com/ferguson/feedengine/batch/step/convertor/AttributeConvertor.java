package com.ferguson.feedengine.batch.step.convertor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ferguson.feedengine.batch.utils.FeedEngineCache;
import com.ferguson.feedengine.batch.utils.XMLStreamParser;
import com.ferguson.feedengine.data.model.AttributeBean;
import com.ferguson.feedengine.data.model.ESBean;
import com.ferguson.feedengine.data.model.MetaData;
import com.ferguson.feedengine.data.model.ValidationBean;

@Component("AttributeConvertor")
public class AttributeConvertor implements CatalogDataCovertor {

	@Autowired
	@Qualifier("feedEngineCache")
	private FeedEngineCache cache;
	
	@Override
	public ESBean convert(Map input) {
		if (input == null) {
			return null;
		}
		AttributeBean attribute = new AttributeBean();
		attribute.setId((String)input.remove("ID"));
		attribute.setName((String)input.remove("Name"));
		populateList(input, attribute.getAttributeGroupLinks(), "AttributeGroupLinks", "AttributeGroupID");
		populateList(input, attribute.getUserTypeLinks(), "UserTypeLinks", "UserTypeID");
		if (null != input.get("Validation")) {
			Map validationMap = (Map)input.remove("Validation");
			ValidationBean validation = new ValidationBean();
			attribute.setValidation(validation);
			validation.setBaseType((String)validationMap.get("BaseType"));
			validation.setInputMask((String)validationMap.get("InputMask"));
			validation.setMaxLength((String)validationMap.get("MaxLength"));
			validation.setMaxValue((String)validationMap.get("MaxValue"));
			validation.setMinValue((String)validationMap.get("MinValue"));
		}
		input.remove(XMLStreamParser.ELEMENT_NAME);
		attribute.getOtherProperties().putAll(input);
		if (!cache.containsKey(FeedEngineCache.CACHE_DISTRICT_ATTRIBUTE)) {
			cache.put(FeedEngineCache.CACHE_DISTRICT_ATTRIBUTE, new HashMap());
		}
		((Map)cache.get(FeedEngineCache.CACHE_DISTRICT_ATTRIBUTE)).put(FeedEngineCache.CACHE_KEY_PREFIX_ATTRIBUTE + attribute.getId(), attribute);
		return attribute;
	}

	private void populateList(Map input, List list, String listName, String valueName) {
		if (null != input.get(listName)) {
			List<Map> links = (List<Map>)input.remove(listName);
			for(Map link: links) {
				if (null == link) {
					continue;
				}
				list.add((String)link.get(valueName));
			}
		}
	}

}
