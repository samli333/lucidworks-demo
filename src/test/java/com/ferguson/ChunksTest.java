package com.ferguson;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ferguson.feedengine.batch.FullFeedJobConfiguration;
import com.ferguson.feedengine.batch.JobFacilitiesConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)

@ContextConfiguration(classes = {LucidworksPocApplication.class, JobFacilitiesConfiguration.class, FullFeedJobConfiguration.class})
@EnableAutoConfiguration
public class ChunksTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    public void givenChunksJob_whenJobEnds_thenStatusCompleted() 
      throws Exception {
 
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
 
        assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus()); 
    }
}