package com.ferguson.feedengine.batch.step.stibofeed;

import java.util.Map;

import com.ferguson.feedengine.data.model.ESBean;

public interface CatalogDataCovertor {

	ESBean convert(Map input);
}