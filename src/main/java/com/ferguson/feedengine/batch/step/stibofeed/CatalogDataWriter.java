package com.ferguson.feedengine.batch.step.stibofeed;

import java.util.List;
import java.util.Map;

import com.ferguson.feedengine.data.model.AssetBean;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.ferguson.feedengine.data.model.AttributeBean;
import com.ferguson.feedengine.data.model.CategoryBean;
import com.ferguson.feedengine.data.model.ESBean;

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
	@Qualifier("feedEngineCache")
	private Map<Object, Object> cache;
	
	@Override
	public void beforeStep(StepExecution stepExecution) {
	}

	@Override
	public void write(List<? extends ESBean> elements) throws Exception {
		for (ESBean element : elements) {
			if (element instanceof AssetBean) {
				assetBeanRepository.save(element);
			}
			if (element instanceof AttributeBean) {
				attributeBeanRepository.save(element);
			}
			if (element instanceof CategoryBean) {
				categoryBeanRepository.save(element);
			}
		}
		
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		return ExitStatus.COMPLETED;
	}
}
