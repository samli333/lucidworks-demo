package com.ferguson.feedengine.batch.step.writees;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.util.CollectionUtils;

import com.ferguson.feedengine.batch.utils.FeedEngineCache;
import com.ferguson.feedengine.data.model.AssetBean;
import com.ferguson.feedengine.data.model.AttributeBean;
import com.ferguson.feedengine.data.model.BaseBean;
import com.ferguson.feedengine.data.model.BestSellerBean;
import com.ferguson.feedengine.data.model.CategoryBean;
import com.ferguson.feedengine.data.model.ESBean;
import com.ferguson.feedengine.data.model.ProductBean;
import com.ferguson.feedengine.data.model.SalesRankBean;

public class WriteESWriter implements ItemWriter<Object>, StepExecutionListener {

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
	@Qualifier("bestSellerBeanRepository")
	private ElasticsearchRepository bestSellerRepository;

	@Autowired
	@Qualifier("salesRankBeanRepository")
	private ElasticsearchRepository salesRankRepository;

	@Autowired
	@Qualifier("feedEngineCache")
	private FeedEngineCache cache;

	@Override
	public void beforeStep(StepExecution stepExecution) {
	}

	@Override
	public void write(List<? extends Object> elements) throws Exception {
		
		if (null == elements) {
			return;
		}
		List<ESBean> assetBeans = elements.stream().filter(element -> {return element instanceof AssetBean;}).map(element -> {return (AssetBean)element;}).collect(Collectors.toList());
		List<ESBean> attributeBeans = elements.stream().filter(element -> {return element instanceof AttributeBean;}).map(element -> {return (AttributeBean)element;}).collect(Collectors.toList());
		List<ESBean> categoryBeans = elements.stream().filter(element -> {return element instanceof CategoryBean;}).map(element -> {return (CategoryBean)element;}).collect(Collectors.toList());
		List<ESBean> productBeans = elements.stream().filter(element -> {return element instanceof ProductBean;}).map(element -> {return (ProductBean)element;}).collect(Collectors.toList());
		List<BaseBean> bestSellerBeans = elements.stream().filter(element -> {return element instanceof BestSellerBean;}).map(element -> {return (BestSellerBean)element;}).collect(Collectors.toList());
		List<BaseBean> salesRankBeans = elements.stream().filter(element -> {return element instanceof SalesRankBean;}).map(element -> {return (SalesRankBean)element;}).collect(Collectors.toList());
		if (!CollectionUtils.isEmpty(assetBeans)) {
			assetBeanRepository.saveAll(assetBeans);
		}
		if (!CollectionUtils.isEmpty(attributeBeans)) {
			attributeBeanRepository.saveAll(attributeBeans);
		}
		if (!CollectionUtils.isEmpty(categoryBeans)) {
			categoryBeanRepository.saveAll(categoryBeans);
		}
		if (!CollectionUtils.isEmpty(productBeans)) {
			productBeanRepository.saveAll(productBeans);
		}
		if (!CollectionUtils.isEmpty(bestSellerBeans)) {
			bestSellerRepository.saveAll(bestSellerBeans);
		}
		if (!CollectionUtils.isEmpty(salesRankBeans)) {
			salesRankRepository.saveAll(salesRankBeans);
		}
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		return ExitStatus.COMPLETED;
	}
}
