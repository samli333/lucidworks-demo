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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

import com.ferguson.feedengine.batch.step.generate.DataSourceProcessor;
import com.ferguson.feedengine.batch.step.generate.DataSourceReader;
import com.ferguson.feedengine.batch.step.generate.DataSourceWriter;
import com.ferguson.feedengine.batch.step.preparation.CSVDataProcessor;
import com.ferguson.feedengine.batch.step.preparation.CSVDataWriter;
import com.ferguson.feedengine.batch.step.preparation.CSVLineReader;
import com.ferguson.feedengine.batch.step.stibofeed.CatalogDataProcessor;
import com.ferguson.feedengine.batch.step.stibofeed.CatalogDataReader;
import com.ferguson.feedengine.batch.step.stibofeed.CatalogDataWriter;

public class FullFeedJobConfiguration {
	@Autowired 
    private JobBuilderFactory jobs;

    @Autowired 
    private StepBuilderFactory steps;

    /**
     * preparation Step
     * In this step will read the csv files. And load these data into Cache, also write these data in to ES.
     * 
     * @param reader
     * @param processor
     * @param writer
     * @return
     */
    @Bean
    protected Step preparationStep() {
    	// TODO: will use Partitioning feature in this step.
        return steps.get("preparation").<Map, Map> chunk(30)
          .reader(csvLineReader())
          .processor(csvDataProcessor())
          .writer(csvDataWriter())
          .build();
    }
    
    @Bean
    public ItemReader<Map> csvLineReader() {
        return new CSVLineReader();
    }

    @Bean
    public ItemProcessor<Map, Map> csvDataProcessor() {
        return new CSVDataProcessor();
    }

    @Bean
    public ItemWriter<Map> csvDataWriter() {
        return new CSVDataWriter();
    }
    

    /**
     * "StiboFileFeed" Step
     * In this step will read the stibo xml file using JAVA StAX.
     * Will make sure Attribute, Classification, Asset first.(load these data into Cache, also write these data in to ES)
     * then process the Product element.
     * if the product element occur before above elements(Attribute, Classification, Asset) in stibo xml file,
     * will skip product element in this step. Will jump to "StiboFileFeedBackup" Step to cover product element feed.
     * 
     * @param reader
     * @param processor
     * @param writer
     * @return
     */
    @Bean
    protected Step stiboFileFeedStep() {
        return steps.get("stiboFileFeedStep").<Map, Map> chunk(30)
          .reader(catalogDataReaderReader())
          .processor(catalogDataProcessor())
          .writer(catalogDataWriter())
          .build();
    }
    
    
    @Bean
    public ItemReader<Map> catalogDataReaderReader() {
        return new CatalogDataReader();
    }

    @Bean
    public ItemProcessor<Map, Map> catalogDataProcessor() {
        return new CatalogDataProcessor();
    }

    @Bean
    public ItemWriter<Map> catalogDataWriter() {
        return new CatalogDataWriter();
    }
    
    
    @Bean
    protected Step stiboFileFeedBackup() {
        return steps.get("StiboFileFeedBackup").<Map, Map> chunk(30)
        		.reader(catalogDataReaderReader())
                .processor(catalogDataProcessor())
                .writer(catalogDataWriter())
          .build();
    }
    
    
    @Bean
    protected Step generateDataSourceStep() {
        return steps.get("generateDataSource").<Map, Map> chunk(30)
          .reader(dataSourceReader())
          .processor(dataSourceProcessor())
          .writer(dataSourceWriter())
          .build();
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
    public Job fullFeedJob() {
        return jobs
          .get("fullFeed")
          .start(preparationStep())
          .next(stiboFileFeedStep())
          .on("Complete But Skip Product Feed").to(stiboFileFeedBackup())
          .on("*").to(generateDataSourceStep())
          .from(stiboFileFeedStep())
          .on("*").to(generateDataSourceStep())
          .end()
          .build();
    }
}
