package com.ferguson.feedengine.batch.step.preparation;

import java.util.Map;

import com.ferguson.feedengine.data.model.BaseBean;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemProcessor;

public class CSVDataProcessor implements ItemProcessor<BaseBean, BaseBean>, StepExecutionListener {

	
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
	public BaseBean process(BaseBean item) throws Exception {
		System.out.println("=================" + item.getClass());
		return item;
	}

}
