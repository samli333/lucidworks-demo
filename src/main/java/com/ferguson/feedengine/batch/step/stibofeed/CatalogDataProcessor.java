package com.ferguson.feedengine.batch.step.stibofeed;

import java.util.Map;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import com.ferguson.feedengine.batch.step.stibofeed.convertor.CatalogDataCovertor;
import com.ferguson.feedengine.batch.utils.XMLStreamParser;
import com.ferguson.feedengine.data.model.ESBean;

public class CatalogDataProcessor implements ItemProcessor<Map, ESBean>, StepExecutionListener {

	public static final String CONVERTOR_SUFFIX = "Convertor";
	
	@Autowired
	private Map<String, CatalogDataCovertor> covertorMap;

	@Override
	public void beforeStep(StepExecution stepExecution) {
	}

	@Override
	public ESBean process(Map element) throws Exception {
		if (null == element) {
			return null;
		}
		CatalogDataCovertor convertor = null; 
		Object elementName = element.get(XMLStreamParser.ELEMENT_NAME);
		if (elementName instanceof String) {
			convertor = covertorMap.get(elementName + CONVERTOR_SUFFIX);
		}
		if (null == convertor) {
			return null;
		}
		return convertor.convert(element);
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		return ExitStatus.COMPLETED;
	}
}