package com.ferguson.feedengine.batch;

import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ferguson.feedengine.batch.step.generate.DataSourceProcessor;
import com.ferguson.feedengine.batch.step.generate.DataSourceReader;
import com.ferguson.feedengine.batch.step.generate.DataSourceWriter;

@Configuration
public class GenerateJobConfiguration {
	@Autowired
	private JobBuilderFactory jobs;

	@Autowired
	private StepBuilderFactory steps;

	@Bean
	protected Step generateDataSourceStep() {
		return steps.get("generateDataSource").<Map, Map>chunk(1000).reader(dataSourceReader())
				.processor(dataSourceProcessor()).writer(dataSourceWriter()).build();
	}

	@Bean
	public ItemReader<Map> dataSourceReader() {
		return new DataSourceReader();
	}

	@Bean
	public ItemProcessor<Map, Map> dataSourceProcessor() {
		return new DataSourceProcessor();
	}

	@Bean
	public ItemWriter<Map> dataSourceWriter() {
		return new DataSourceWriter();
	}
	
	@Bean
    public Job generateJob() {
        return jobs.get("generate")
          .start(generateDataSourceStep())
          .build();
    }
}
