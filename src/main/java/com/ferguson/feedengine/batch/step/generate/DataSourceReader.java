package com.ferguson.feedengine.batch.step.generate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.util.StringUtils;

public class DataSourceReader implements ItemReader<Map>, StepExecutionListener {

	private final Logger logger = LoggerFactory.getLogger(DataSourceReader.class);

	private String[] productIds;

	@Override
	public void beforeStep(StepExecution stepExecution) {
		// TODO Auto-generated method stub
		String productIds = stepExecution.getJobExecution().getJobParameters().getString("productIds");
		if (null != productIds) {
			this.productIds = StringUtils.split(productIds, ",");
		}
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		// TODO Auto-generated method stub
		return ExitStatus.COMPLETED;
	}

	@Override
	public Map read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

		for (String productId : this.productIds) {
			logger.info("product id: {}", productId);
		}
		return new HashMap();
	}

}
