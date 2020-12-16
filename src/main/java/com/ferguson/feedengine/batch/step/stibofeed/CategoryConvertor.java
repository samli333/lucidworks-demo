package com.ferguson.feedengine.batch.step.stibofeed;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.ferguson.feedengine.data.model.CategoryBean;
import com.ferguson.feedengine.data.model.ESBean;
@Component("ClassificationConvertor")
public class CategoryConvertor implements CatalogDataCovertor {

	@Override
	public ESBean convert(Map input) {
		if(null == input) {
			return null;
		}
		CategoryBean category = new CategoryBean();
		category.setId((String)input.remove("ID"));
		category.setName((String)input.remove("Name"));
		if (null != input.get("Classifications")) {
			category.getCategories().addAll((List<String>) input.remove("Classifications"));
		}
//		category.getOtherProperties().putAll(input);
		return category;
	}

}
