package com.ferguson.feedengine.controller;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestartJobController {
	@Autowired
	private JobExplorer jobExplorer;
	@Autowired
	private JobOperator jobOperator;
	
	
	@RequestMapping("/restartjob/{executionId}")
    public String restartjob(@PathVariable int executionId) throws Exception {
		System.out.println("executionId" + executionId);
		final Long restartId = jobOperator.restart(executionId);
		final JobExecution restartExecution = jobExplorer.getJobExecution(restartId);
        return "jobExecution's info: Id = " + restartExecution.getId() + " ,status = " + restartExecution.getExitStatus();
    }
}
