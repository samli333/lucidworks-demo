package com.ferguson.feedengine.batch.step.preparation;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ferguson.feedengine.batch.step.stibofeed.convertor.CatalogDataCovertor;
import com.ferguson.feedengine.batch.utils.FeedEngineCache;
import com.ferguson.feedengine.batch.utils.XMLStreamParser;
import com.ferguson.feedengine.data.model.BestSellerBean;
import com.ferguson.feedengine.data.model.ESBean;
import com.ferguson.feedengine.data.model.SalesRankBean;
import com.ferguson.feedengine.data.model.TempBestSellerBean;

public class CacheItemProcessor implements ItemProcessor<Object, Object>, StepExecutionListener {

	public static final String CONVERTOR_SUFFIX = "Convertor";
	
	@Autowired
	private Map<String, CatalogDataCovertor> covertorMap;
	
	@Autowired
	@Qualifier("feedEngineCache")
	private FeedEngineCache cache;

	@Override
	public void beforeStep(StepExecution stepExecution) {
		
	}

	@Override
	public Object process(Object element) throws Exception {
		
		if (null == element) {
			return null;
		}
		if (element instanceof Map) {
			Map catalogData = (Map)element;
			CatalogDataCovertor convertor = null; 
			Object elementName = catalogData.get(XMLStreamParser.ELEMENT_NAME);
			if (elementName instanceof String) {
				convertor = covertorMap.get(elementName + CONVERTOR_SUFFIX);
			}
			if (null == convertor) {
				return null;
			}
			ESBean result = convertor.convert(catalogData);
			
			return result;
		}
		
		if (element instanceof TempBestSellerBean) {
			if (!cache.containsKey(FeedEngineCache.CACHE_DISTRICT_BEST_SELLER)) {
				cache.put(FeedEngineCache.CACHE_DISTRICT_BEST_SELLER, new HashMap());
			}
			TempBestSellerBean tmp = (TempBestSellerBean)element;
			Map bestSellerCache = ((Map) cache.get(FeedEngineCache.CACHE_DISTRICT_BEST_SELLER));
			BestSellerBean result = (BestSellerBean)bestSellerCache.get(FeedEngineCache.CACHE_KEY_PREFIX_BEST_SELLER + tmp.getSkuId());
			if (null == result) {
				result = new BestSellerBean(tmp.getSkuId(), new HashMap<String, String>());
			}
			result.getSkuBranchSales().put(tmp.getBranch() + "_sales", tmp.getRank());
			bestSellerCache.put(FeedEngineCache.CACHE_KEY_PREFIX_BEST_SELLER + tmp.getSkuId(), result);
			return result;
		}
		
		if (element instanceof SalesRankBean) {
			if (!cache.containsKey(FeedEngineCache.CACHE_DISTRICT_SALES_RANK)) {
				cache.put(FeedEngineCache.CACHE_DISTRICT_SALES_RANK, new HashMap());
			}
			SalesRankBean salesRank = (SalesRankBean) element;
			Map salesRankCache = ((Map) cache.get(FeedEngineCache.CACHE_DISTRICT_SALES_RANK));
			salesRankCache.put(FeedEngineCache.CACHE_KEY_PREFIX_SALES_RANK + salesRank.getSkuId(), salesRank);
			return salesRank;
		}
		return null;
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		return ExitStatus.COMPLETED;
	}
}