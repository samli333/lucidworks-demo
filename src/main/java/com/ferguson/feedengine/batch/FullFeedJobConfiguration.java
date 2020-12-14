package com.ferguson.feedengine.batch;

import java.io.IOException;
import java.util.Map;

import com.ferguson.feedengine.batch.item.file.mapping.MapFieldSetMapper;
import com.ferguson.feedengine.batch.partition.MultiResourceFilesPartitioner;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.*;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class FullFeedJobConfiguration {
    @Autowired
    ResourcePatternResolver resoursePatternResolver;

    @Autowired
    private JobBuilderFactory jobs;

    @Autowired 
    private StepBuilderFactory steps;

    @Bean
    public MultiResourceFilesPartitioner partitioner() {
        MultiResourceFilesPartitioner partitioner = new MultiResourceFilesPartitioner();
        Resource[] resources;
        try {
            resources = resoursePatternResolver.getResources("file:src/main/resources/input/*.csv");
        } catch (IOException e) {
            throw new RuntimeException("I/O problems when resolving the input file pattern.", e);
        }
        partitioner.setResources(resources);
        return partitioner;
    }

    /**
     * preparation Step
     * In this step will read the csv files. And load these data into Cache, also write these data in to ES.
     *
     * @return
     */
    @Bean
    public Step preparationStep() {
        return steps.get("partitionStep")
                .partitioner("slaveStep", partitioner())
                .step(slaveStep())
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean("slaveStep")
    public Step slaveStep() {
        // TODO: will use Partitioning feature in this step.
        return steps.get("slaveStep").<Map, Map>chunk(30)
                .reader(csvLineReader(null))
                .processor(csvDataProcessor())
                .writer(csvDataWriter())
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setMaxPoolSize(2);
        taskExecutor.setCorePoolSize(2);
        taskExecutor.setQueueCapacity(2);
        taskExecutor.afterPropertiesSet();
        return taskExecutor;
    }

    @StepScope
    @Bean
    public FlatFileItemReader<Map<String, String>> csvLineReader(
            @Value("#{stepExecutionContext[fileName]}") String filename)
            throws UnexpectedInputException, ParseException {
        FlatFileItemReader<Map<String, String>> reader = new FlatFileItemReader<>();
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        String[] tokens = null;
        switch (filename) {
            case "best_seller_data.csv":
                tokens = new String[]{"skuId", "branchId", "rank"};
                break;
            case "sales_rank_data.csv":
                tokens = new String[]{"skuId", "revenue"};
                break;
            default:
                tokens = new String[]{};
                break;
        }
        tokenizer.setNames(tokens);
        ClassPathResource resource = new ClassPathResource("input/" + filename);
        reader.setResource(resource);
        DefaultLineMapper<Map<String, String>> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(new MapFieldSetMapper());
        reader.setLinesToSkip(1);
        reader.setLineMapper(lineMapper);
        return reader;
    }

    @StepScope
    @Bean("csvDataProcessor")
    public ItemProcessor<Map, Map> csvDataProcessor() {
        return new CSVDataProcessor();
    }

    @StepScope
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
