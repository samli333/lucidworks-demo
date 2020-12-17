package com.ferguson.feedengine.batch.step.stibofeed;

import java.util.List;
import java.util.stream.Collectors;

import com.ferguson.feedengine.data.model.AssetBean;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.util.CollectionUtils;

import com.ferguson.feedengine.batch.utils.FeedEngineCache;
import com.ferguson.feedengine.data.model.AttributeBean;
import com.ferguson.feedengine.data.model.CategoryBean;
import com.ferguson.feedengine.data.model.ESBean;
import com.ferguson.feedengine.data.model.ProductBean;

public class CatalogDataWriter implements ItemWriter<ESBean>, StepExecutionListener {

	@Autowired
	@Qualifier("assetBeanRepository")
	private ElasticsearchRepository assetBeanRepository;

	@Autowired
    @Qualifier("attributeBeanRepository")
	private ElasticsearchRepository attributeBeanRepository;
	
	@Autowired
    @Qualifier("categoryBeanRepository")
	private ElasticsearchRepository categoryBeanRepository;
	
	
	@Autowired
    @Qualifier("productBeanRepository")
	private ElasticsearchRepository productBeanRepository;
	
	@Autowired
	@Qualifier("feedEngineCache")
	private FeedEngineCache cache;
	
	@Override
	public void beforeStep(StepExecution stepExecution) {
	}

	@Override
	public void write(List<? extends ESBean> elements) throws Exception {
		
		if (null == elements) {
			return;
		}
		List<ESBean> assetBeans = elements.stream().filter(element -> {return element instanceof AssetBean;}).collect(Collectors.toList());
		List<ESBean> attributeBeans = elements.stream().filter(element -> {return element instanceof AttributeBean;}).collect(Collectors.toList());
		List<ESBean> categoryBeans = elements.stream().filter(element -> {return element instanceof CategoryBean;}).collect(Collectors.toList());
		List<ESBean> productBeans = elements.stream().filter(element -> {return element instanceof ProductBean;}).collect(Collectors.toList());
		if (!CollectionUtils.isEmpty(assetBeans)) {
//			assetBeanRepository.saveAll(assetBeans);
		}
		if (!CollectionUtils.isEmpty(attributeBeans)) {
//			attributeBeanRepository.saveAll(attributeBeans);
		}
		if (!CollectionUtils.isEmpty(categoryBeans)) {
//			categoryBeanRepository.saveAll(categoryBeans);
		}
		if (!CollectionUtils.isEmpty(productBeans)) {
			productBeanRepository.saveAll(productBeans);
		}
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		return ExitStatus.COMPLETED;
	}
}
