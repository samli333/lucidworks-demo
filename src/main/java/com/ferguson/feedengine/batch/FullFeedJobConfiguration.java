package com.ferguson.feedengine.batch;

import com.ferguson.feedengine.batch.partition.MultiResourceFilesPartitioner;
import com.ferguson.feedengine.batch.step.generate.DataSourceProcessor;
import com.ferguson.feedengine.batch.step.generate.DataSourceReader;
import com.ferguson.feedengine.batch.step.generate.DataSourceWriter;
import com.ferguson.feedengine.batch.step.preparation.CsvTasklet;
import com.ferguson.feedengine.batch.step.stibofeed.CatalogDataProcessor;
import com.ferguson.feedengine.batch.step.stibofeed.CatalogDataReader;
import com.ferguson.feedengine.batch.step.stibofeed.CatalogDataWriter;
import com.ferguson.feedengine.batch.utils.Cache;
import com.ferguson.feedengine.data.model.BaseBean;
import com.ferguson.feedengine.data.model.ESBean;
import com.ferguson.feedengine.data.model.TempBestSellerBean;
import com.ferguson.feedengine.data.model.SalesRankBean;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.*;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.RecordFieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.IOException;
import java.util.Map;

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
        return steps.get("slaveStep")
                .tasklet(csvTasklet())
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setMaxPoolSize(2);
        taskExecutor.setMaxPoolSize(2);
        taskExecutor.setCorePoolSize(2);
        taskExecutor.setQueueCapacity(2);
        taskExecutor.afterPropertiesSet();
        return taskExecutor;
    }

    @StepScope
    @Bean
    public FlatFileItemReader<BaseBean> csvLineReader(
            @Value("#{stepExecutionContext[fileName]}") String filename)
            throws UnexpectedInputException, ParseException {
        FlatFileItemReader<BaseBean> reader = new FlatFileItemReader<>();
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        String[] tokens = null;
        Class iclass = null;
        switch (filename) {
            case "best_seller_data.csv":
                tokens = new String[]{"skuId", "branch", "rank"};
                iclass = TempBestSellerBean.class;
                break;
            case "sales_rank_data.csv":
                tokens = new String[]{"skuId", "sales"};
                iclass = SalesRankBean.class;
                break;
            default:
                tokens = new String[]{};
                break;
        }
        tokenizer.setNames(tokens);
        ClassPathResource resource = new ClassPathResource("input/" + filename);
        reader.setResource(resource);
        DefaultLineMapper<BaseBean> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);
        RecordFieldSetMapper fieldSetMapper = new RecordFieldSetMapper(iclass);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        reader.setLinesToSkip(1);
        reader.setLineMapper(lineMapper);
        return reader;
    }

    @StepScope
    @Bean("csvTasklet")
    public CsvTasklet csvTasklet() {
        return new CsvTasklet(csvLineReader(null));
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
        return steps.get("stiboFileFeedStep").<Map, ESBean> chunk(30)
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
    public ItemProcessor<Map, ESBean> catalogDataProcessor() {
        return new CatalogDataProcessor();
    }

    @Bean
    public ItemWriter<ESBean> catalogDataWriter() {
        return new CatalogDataWriter();
    }
    
    
    @Bean
    protected Step stiboFileFeedBackup() {
        return steps.get("StiboFileFeedBackup").<Map, ESBean> chunk(30)
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

    @Bean
    public Cache cache() {
        return new Cache();
    }
}
