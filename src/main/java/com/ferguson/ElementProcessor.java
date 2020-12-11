package com.ferguson;

import java.util.Map;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemProcessor;

public class ElementProcessor implements 
ItemProcessor<Map, Map>, StepExecutionListener {


  @Override
  public void beforeStep(StepExecution stepExecution) {
  }
  
  @Override
  public Map process(Map element) throws Exception {
	  System.out.println("------------ "+Thread.currentThread().getName()+" process element");
	  return element;
  }

  @Override
  public ExitStatus afterStep(StepExecution stepExecution) {
      return ExitStatus.COMPLETED;
  }
}