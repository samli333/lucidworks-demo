package com.ferguson.feedengine.batch.step.csv;

import java.util.List;
import java.util.Map;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemWriter;

public class CSVDataWriter implements ItemWriter<Map>, StepExecutionListener {

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
	public void write(List<? extends Map> items) throws Exception {
		System.out.println("================= CSV write");
		
	}

}
