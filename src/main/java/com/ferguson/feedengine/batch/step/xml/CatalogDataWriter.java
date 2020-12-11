package com.ferguson.feedengine.batch.step.xml;

import java.util.List;
import java.util.Map;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemWriter;

import com.alibaba.fastjson.JSONObject;

public class CatalogDataWriter implements 
ItemWriter<Map>, StepExecutionListener {



  @Override
  public void beforeStep(StepExecution stepExecution) {
  }

  @Override
  public void write(List<? extends Map> elements) throws Exception {
	  System.out.println("------------ "+Thread.currentThread().getName()+" Write element"); 
  }

  @Override
  public ExitStatus afterStep(StepExecution stepExecution) {
      return ExitStatus.COMPLETED;
  }
}
