package com.ferguson.feedengine.batch;

import java.io.IOException;
import java.util.Map;

import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.ferguson.feedengine.batch.partition.MultiResourceFilesPartitioner;
import com.ferguson.feedengine.batch.step.preparation.CacheItemProcessor;
import com.ferguson.feedengine.batch.step.preparation.CacheItemReader;
import com.ferguson.feedengine.batch.step.preparation.CacheItemWriter;
import com.ferguson.feedengine.batch.step.stibofeed.CatalogDataProcessor;
import com.ferguson.feedengine.batch.step.stibofeed.CatalogDataReader;
import com.ferguson.feedengine.batch.step.stibofeed.CatalogDataWriteListener;
import com.ferguson.feedengine.batch.step.stibofeed.CatalogDataWriter;
import com.ferguson.feedengine.batch.utils.FeedEngineCache;
import com.ferguson.feedengine.data.model.ESBean;

@Configuration
@PropertySource(value="classpath:stibo_parser.properties") 
public class FullParseJobConfiguration {
    @Autowired
    ResourcePatternResolver resoursePatternResolver;

    @Autowired
    private JobBuilderFactory jobs;

    @Autowired 
    private StepBuilderFactory steps;
    
    @Value("${catalog.full.feed.filepath}")
    private String catalogFullFeedFilepath;
    
    @Bean
    public MultiResourceFilesPartitioner partitioner() {
        MultiResourceFilesPartitioner partitioner = new MultiResourceFilesPartitioner();
        Resource[] resources;
        try {
            resources = resoursePatternResolver.getResources(catalogFullFeedFilepath);
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
        return steps.get("preparationStep")
                .partitioner("slaveStep", partitioner())
                .step(slaveStep())
                .taskExecutor(taskExecutor())
                .build();
    }
    
    @Bean("slaveStep")
    public Step slaveStep() {
        return steps.get("slaveStep").<Object, Object> chunk(5000)
        		.reader(cacheItemReader())
        		.processor(cacheItemProcessor())
        		.writer(cacheItemWriter())
                .build();
    }
    
    @StepScope
    @Bean
    public CacheItemReader cacheItemReader() {
        return new CacheItemReader();
    }
    @Bean
    public ItemProcessor<Object, Object> cacheItemProcessor() {
        return new CacheItemProcessor();
    }
    
    @Bean
    public ItemWriter<Object> cacheItemWriter() {
        return new CacheItemWriter();
    }
    
    
    
    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setMaxPoolSize(3);
        taskExecutor.setMaxPoolSize(3);
        taskExecutor.setCorePoolSize(3);
        taskExecutor.setQueueCapacity(3);
        taskExecutor.afterPropertiesSet();
        return taskExecutor;
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
        return steps.get("stiboFileFeedStep").<Map, ESBean> chunk(2000)
          .reader(catalogDataReaderReader())
          .processor(catalogDataProcessor())
          .writer(catalogDataWriter())
//          .listener(catalogDataWriteListener())
          .build();
    }
    
    @Bean
    public ItemReader<Map> catalogDataReaderReader() {
        return new CatalogDataReader();
    }
    
    @Bean
    public ItemProcessor<Map, ESBean> catalogDataProcessor() {
        return new CatalogDataProcessor();
    }
    
    @Bean
    public ItemWriter<ESBean> catalogDataWriter() {
        return new CatalogDataWriter();
    }
    
    
    @Bean
    public ItemWriteListener<ESBean> catalogDataWriteListener() {
        return new CatalogDataWriteListener();
    }
    
    
    @Bean
    protected Step stiboFileFeedBackup() {
        return steps.get("StiboFileFeedBackup").<Map, ESBean> chunk(1000)
                .reader(catalogDataReaderReader())
                .processor(catalogDataProcessor())
                .writer(catalogDataWriter()).listener(catalogDataWriteListener())
          .build();
    }
    
    
    @Bean
    public Job fullParseJob() {
        return jobs.get("fullParseJob")
          .start(preparationStep())
          .next(stiboFileFeedStep())
//          .next(stiboFileFeedBackup())
          .build();
    }

    @Bean
    public FeedEngineCache feedEngineCache() {
        return new FeedEngineCache();
    }
}
