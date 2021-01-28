package com.ferguson.feedengine.batch.step.writees;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ferguson.feedengine.data.model.ESBean;
import com.ferguson.feedengine.data.model.ProductBean;

import io.micrometer.core.instrument.util.StringUtils;

public class CatalogDataWriteListener implements ItemWriteListener<ESBean> {

	@Autowired
	JobLauncher jobLauncher;
	
	@Autowired
	@Qualifier("generateJob")
	Job generateJob;

	@Override
	public void beforeWrite(List<? extends ESBean> items) {
		// TODO Auto-generated method stub
	}

	@Override
	public void afterWrite(List<? extends ESBean> items) {
		// TODO Auto-generated method stub
		if (null == items) {
			return;
		}

		String productIds = items.stream().filter(item -> {
			return item instanceof ProductBean;
		}).map(item -> {
			return item.getId();
		}).collect(Collectors.joining(","));
		if (StringUtils.isBlank(productIds)) {
			return;
		}
		launchGenerateJob(productIds);
	}

	private void launchGenerateJob(String productIds) {
		JobParametersBuilder paramBuilder = new JobParametersBuilder();
		paramBuilder.addString("productIds", productIds);
		try {
			jobLauncher.run(generateJob, paramBuilder.toJobParameters());
		} catch (JobExecutionAlreadyRunningException e) {
			// TODO Auto-generated catch block
		} catch (JobRestartException e) {
			// TODO Auto-generated catch block
		} catch (JobInstanceAlreadyCompleteException e) {
			// TODO Auto-generated catch block
		} catch (JobParametersInvalidException e) {
			// TODO Auto-generated catch block
		}
	}

	@Override
	public void onWriteError(Exception exception, List<? extends ESBean> items) {
		// TODO Auto-generated method stub

	}

}
