package com.ferguson.feedengine.batch.step.writees;

import java.util.Map;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import com.ferguson.feedengine.batch.step.convertor.CatalogDataCovertor;
import com.ferguson.feedengine.batch.utils.XMLStreamParser;
import com.ferguson.feedengine.data.model.ESBean;

public class WriteESProcessor implements ItemProcessor<Object, Object>, StepExecutionListener {

	public static final String CONVERTOR_SUFFIX = "Convertor";
	
	@Autowired
	private Map<String, CatalogDataCovertor> covertorMap;

	@Override
	public void beforeStep(StepExecution stepExecution) {
	}

	@Override
	public Object process(Object input) throws Exception {
		if (input instanceof Map) {
			Map element = (Map)input;
			CatalogDataCovertor convertor = null; 
			Object elementName = element.get(XMLStreamParser.ELEMENT_NAME);
			if (elementName instanceof String) {
				convertor = covertorMap.get(elementName + CONVERTOR_SUFFIX);
			}
			if (null == convertor) {
				return null;
			}
			// product esBean
			return convertor.convert(element);
		}
		// attribute,category,asset esBean; bestSeller,salesRank baseBean
		return input;
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		return ExitStatus.COMPLETED;
	}
}