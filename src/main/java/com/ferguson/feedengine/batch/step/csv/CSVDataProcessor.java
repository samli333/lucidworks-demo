package com.ferguson.feedengine.batch.step.csv;

import java.util.Map;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemProcessor;

public class CSVDataProcessor implements ItemProcessor<Map, Map>, StepExecutionListener {

	
	@Override
	public void beforeStep(StepExecution stepExecution) {
		// TODO Auto-generated method stub

	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		// TODO Auto-generated method stub
		return ExitStatus.COMPLETED;
	}

	@Override
	public Map process(Map item) throws Exception {
		System.out.println("================= CSV process");
		return item;
	}

}
