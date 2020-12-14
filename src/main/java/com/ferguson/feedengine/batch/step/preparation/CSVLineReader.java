package com.ferguson.feedengine.batch.step.preparation;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

public class CSVLineReader implements ItemReader<Map>, StepExecutionListener {

	private int count = 0;
	
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
	public Map read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		System.out.println("================= CSV read");
		count++;
		if (count> 10) {
			return null;
		}
		return new HashMap();
	}

}
