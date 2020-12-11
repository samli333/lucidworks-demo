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

import com.ferguson.feedengine.batch.step.csv.CSVDataProcessor;
import com.ferguson.feedengine.batch.step.csv.CSVDataWriter;
import com.ferguson.feedengine.batch.step.csv.CSVLineReader;
import com.ferguson.feedengine.batch.step.xml.CatalogDataProcessor;
import com.ferguson.feedengine.batch.step.xml.CatalogDataWriter;
import com.ferguson.feedengine.batch.step.xml.XMLStreamReader;

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
    @Bean("preparationStep")
    protected Step preparationStep(
    		@Qualifier("csvLineReader")ItemReader<Map> reader,
    		@Qualifier("csvDataProcessor")ItemProcessor<Map, Map> processor, 
    		@Qualifier("csvDataWriter")ItemWriter<Map> writer) {
    	// TODO: will use Partitioning feature in this step.
        return steps.get("preparation").<Map, Map> chunk(30)
          .reader(reader)
          .processor(processor)
          .writer(writer)
          .build();
    }
    
    @Bean("csvLineReader")
    public ItemReader<Map> csvLineReader() {
        return new CSVLineReader();
    }

    @Bean("csvDataProcessor")
    public ItemProcessor<Map, Map> csvDataProcessor() {
        return new CSVDataProcessor();
    }

    @Bean("csvDataWriter")
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
    @Bean("stiboFileFeedStep")
    protected Step stiboFileFeedStep(
    		@Qualifier("xmlStreamReader")ItemReader<Map> reader,
    		@Qualifier("catalogDataProcessor")ItemProcessor<Map, Map> processor, 
    		@Qualifier("catalogDataWriter")ItemWriter<Map> writer) {
        return steps.get("StiboFileFeed").<Map, Map> chunk(30)
          .reader(reader)
          .processor(processor)
          .writer(writer)
          .build();
    }
    
    
    @Bean("xmlStreamReader")
    public ItemReader<Map> xmlStreamReader() {
        return new XMLStreamReader();
    }

    @Bean("catalogDataProcessor")
    public ItemProcessor<Map, Map> catalogDataProcessor() {
        return new CatalogDataProcessor();
    }

    @Bean("catalogDataWriter")
    public ItemWriter<Map> catalogDataWriter() {
        return new CatalogDataWriter();
    }
    
    
//    @Bean("stiboFileFeedBackup")
//    protected Step stiboFileFeedBackup(ItemReader<Map> reader,
//      ItemProcessor<Map, Map> processor, ItemWriter<Map> writer) {
//        return steps.get("StiboFileFeedBackup").<Map, Map> chunk(30)
//          .reader(reader)
//          .processor(processor)
//          .writer(writer)
//          .build();
//    }

    @Bean("fullFeedJob")
    public Job job() {
        return jobs
          .get("fullFeed")
          .start(preparationStep(csvLineReader(), csvDataProcessor(), csvDataWriter()))
          .next(stiboFileFeedStep(xmlStreamReader(), catalogDataProcessor(), catalogDataWriter()))
          .build();
    }
}
