package com.ferguson.feedengine.batch.step.stibofeed.convertor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ferguson.feedengine.batch.utils.FeedEngineCache;
import com.ferguson.feedengine.data.model.AttributeBean;
import com.ferguson.feedengine.data.model.ESBean;
import com.ferguson.feedengine.data.model.ProductBean;

@Component("ProductConvertor")
public class ProductConvertor implements CatalogDataCovertor {

	@Autowired
	@Qualifier("feedEngineCache")
	private FeedEngineCache cache;
	
	
	@Override
	public ESBean convert(Map input) {
		if (null == input) {
			return null;
		}
		ProductBean product = new ProductBean();
		product.setId((String)input.remove("ID"));
		product.setName((String)input.remove("Name"));
		if (null != input.get("Values")) {
			Map values = (Map)input.remove("Values");
			List<Map<String, String>> valueList = (List<Map<String, String>>)values.get("Values");
			if (null != valueList) {
				for (Map<String, String> valuebean: valueList) {
					String attributeId = valuebean.get("AttributeID");
					if (null == attributeId) {
						continue;
					}
					String value = valuebean.get("_value_");
					AttributeBean attribute = (AttributeBean) cache.get(FeedEngineCache.CACHE_KEY_PREFIX_ATTRIBUTE + attributeId);
					String key = attributeId;
					if ( null != attribute.getName()) {
						key = attribute.getName();
					}
					product.getValues().put(key, value);
				}
			}
			List<Map> multiValueList = (List<Map>)values.get("MultiValues");
			if (null != multiValueList) {
				for (Map multiValueBean : multiValueList) {
					String attributeId = (String)multiValueBean.get("AttributeID");
					if (null == attributeId) {
						continue;
					}
					List<Map> multiValues = (List<Map>)multiValueBean.get("Values");
					if (null != multiValues) {
						AttributeBean attribute = (AttributeBean) cache.get(FeedEngineCache.CACHE_KEY_PREFIX_ATTRIBUTE + attributeId);
						String key = attributeId;
						if ( null != attribute.getName()) {
							key = attribute.getName();
						}
						List<String> value = multiValues.stream().filter(multiValue -> multiValue != null).map(multiValue -> {
							return (String)multiValue.get("_value_");
						}).collect(Collectors.toList());
						product.getMultiValue().put(key, value);
					}
				}
			}
		}
		
		product.getOtherProperties().putAll(input);
		return product;
	}

}
