package com.ferguson.feedengine.batch;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class JobFacilitiesConfiguration {

//	@Bean
//	public JobRepository jobRepository() throws Exception {
//		MapJobRepositoryFactoryBean factory = new MapJobRepositoryFactoryBean();
//		factory.setTransactionManager(transactionManager());
//		return (JobRepository) factory.getObject();
//	}

//	@Bean
//	public PlatformTransactionManager transactionManager() {
//		return new ResourcelessTransactionManager();
//	}
//
//	@Bean
//	public JobLauncher jobLauncher() throws Exception {
//		SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
//		jobLauncher.setJobRepository(jobRepository());
//		return jobLauncher;
//	}
//
//	@Bean
//	public DataSource dataSource() {
//		return (DataSource) DataSourceBuilder.create().url("jdbc:h2:mem:testdb").driverClassName("org.h2.Driver")
//				.username("sa").password("password").build();
//	}
//
//	@Bean
//	public JdbcTemplate jdbcTemplate(DataSource dataSource) {
//		return new JdbcTemplate(dataSource);
//	}
	
}
