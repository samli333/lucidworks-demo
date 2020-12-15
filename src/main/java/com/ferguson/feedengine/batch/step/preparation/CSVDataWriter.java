package com.ferguson.feedengine.batch.step.preparation;

import com.ferguson.feedengine.data.model.BaseBean;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public class CSVDataWriter implements ItemWriter<BaseBean>, StepExecutionListener {

    @Autowired
    private ElasticsearchRepository repository;

    @Override
    public void beforeStep(StepExecution stepExecution) {
		ExecutionContext executionContext = stepExecution.getExecutionContext();
		System.out.println("executionContext" + executionContext);
	}

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        // TODO Auto-generated method stub
        return ExitStatus.COMPLETED;
    }

    @Override
    public void write(List<? extends BaseBean> items) throws Exception {
        repository.saveAll(items);
    }

}
