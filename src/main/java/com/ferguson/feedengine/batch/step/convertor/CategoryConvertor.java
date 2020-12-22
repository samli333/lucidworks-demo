package com.ferguson.feedengine.batch.step.convertor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ferguson.feedengine.batch.utils.FeedEngineCache;
import com.ferguson.feedengine.data.model.CategoryBean;
import com.ferguson.feedengine.data.model.ESBean;

@Component("ClassificationConvertor")
public class CategoryConvertor implements CatalogDataCovertor {
	
	@Autowired
	@Qualifier("feedEngineCache")
	private FeedEngineCache cache;

	@Override
	public ESBean convert(Map input) {
		if (null == input) {
			return null;
		}
		CategoryBean category = new CategoryBean();
		category.setId((String) input.remove("ID"));
		category.setName((String) input.remove("Name"));
		if (null != input.get("Classifications")) {
			category.getCategories().addAll((List<String>) input.remove("Classifications"));
		}
//		category.getOtherProperties().putAll(input);
		if (!cache.containsKey(FeedEngineCache.CACHE_DISTRICT_CATEGORY)) {
			cache.put(FeedEngineCache.CACHE_DISTRICT_CATEGORY, new HashMap());
		}
		((Map) cache.get(FeedEngineCache.CACHE_DISTRICT_CATEGORY))
				.put(FeedEngineCache.CACHE_KEY_PREFIX_CATEGORY + category.getId(), category);
		return category;
	}

}
