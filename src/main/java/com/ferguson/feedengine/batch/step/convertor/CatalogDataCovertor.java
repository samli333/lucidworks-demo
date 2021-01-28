package com.ferguson.feedengine.batch.step.convertor;

import java.util.Map;

import com.ferguson.feedengine.data.model.ESBean;

public interface CatalogDataCovertor {

	ESBean convert(Map input);
}
