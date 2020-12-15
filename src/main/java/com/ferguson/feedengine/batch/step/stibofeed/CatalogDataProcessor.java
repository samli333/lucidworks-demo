package com.ferguson.feedengine.batch.step.stibofeed;

import java.util.Map;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemProcessor;

public class CatalogDataProcessor implements 
ItemProcessor<Map, Map>, StepExecutionListener {


  @Override
  public void beforeStep(StepExecution stepExecution) {
  }
  
  @Override
  public Map process(Map element) throws Exception {
	  return element;
  }

  @Override
  public ExitStatus afterStep(StepExecution stepExecution) {
      return ExitStatus.COMPLETED;
  }
}