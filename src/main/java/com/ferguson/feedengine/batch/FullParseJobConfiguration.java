package com.ferguson.feedengine.batch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.partition.support.Partitioner;
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
import org.springframework.util.CollectionUtils;

import com.ferguson.feedengine.batch.partition.DefaultPartitioner;
import com.ferguson.feedengine.batch.step.loadcache.LoadCacheProcessor;
import com.ferguson.feedengine.batch.step.loadcache.LoadCacheReader;
import com.ferguson.feedengine.batch.step.loadcache.LoadCacheWriter;
import com.ferguson.feedengine.batch.step.writees.WriteESProcessor;
import com.ferguson.feedengine.batch.step.writees.WriteESReader;
import com.ferguson.feedengine.batch.step.writees.CatalogDataWriteListener;
import com.ferguson.feedengine.batch.step.writees.WriteESWriter;
import com.ferguson.feedengine.batch.utils.FeedEngineCache;
import com.ferguson.feedengine.data.model.ESBean;

@Configuration
@PropertySource(value = "classpath:stibo_parser.properties")
public class FullParseJobConfiguration {
	@Autowired
	ResourcePatternResolver resoursePatternResolver;

	@Autowired
	private JobBuilderFactory jobs;

	@Autowired
	private StepBuilderFactory steps;

	@Value("${catalog.full.feed.filepath}")
	private String catalogFullFeedFilepath;
	
	@Value("#{'${partitioner.es.sources}'.split(',')}")
	private List<String> partitionerESSources;
	
	@Value("${catalog.full.feed.stibofiles}")
	private String catalogFullFeedStiboFiles;

	@Bean
	public Partitioner preparationPartitioner() {
		DefaultPartitioner partitioner = new DefaultPartitioner();
		Resource[] resources;
		List<String> filePaths = null;
		try {
			resources = resoursePatternResolver.getResources(catalogFullFeedFilepath);
			if (null != resources) {
				filePaths = Arrays.stream(resources).map(resource -> {
					try {
						return resource.getURL().toExternalForm();
					} catch (IOException e) {
						// TODO Auto-generated catch block
					}
					return null;
				}).collect(Collectors.toList());
			}
		} catch (IOException e) {
			throw new RuntimeException("I/O problems when resolving the input file pattern.", e);
		}
		partitioner.setResources(filePaths);
		return partitioner;
	}

	/**
	 * Load cache step: In this step will read the csv files and stibo file(read non-product elements) with partitioner. And load these data
	 * into Cache.
	 *
	 * @return
	 */
	@Bean
	public Step loadCacheStep() {
		return steps.get("loadCacheStep").partitioner("loadCacheSlaveStep", preparationPartitioner()).step(loadCacheSlaveStep())
				.taskExecutor(loadCacheSlaveStepTaskExecutor()).build();
	}

	@Bean("loadCacheSlaveStep")
	public Step loadCacheSlaveStep() {
		return steps.get("loadCacheSlaveStep").<Object, Object>chunk(5).reader(cacheItemReader())
				.processor(cacheItemProcessor()).writer(cacheItemWriter()).build();
	}

	@StepScope
	@Bean
	public LoadCacheReader cacheItemReader() {
		return new LoadCacheReader();
	}

	@StepScope
	@Bean
	public LoadCacheProcessor cacheItemProcessor() {
		return new LoadCacheProcessor();
	}

	@StepScope
	@Bean
	public LoadCacheWriter cacheItemWriter() {
		return new LoadCacheWriter();
	}

	@Bean
	public TaskExecutor loadCacheSlaveStepTaskExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setMaxPoolSize(3);
		taskExecutor.setMaxPoolSize(3);
		taskExecutor.setCorePoolSize(3);
		taskExecutor.setQueueCapacity(3);
		taskExecutor.afterPropertiesSet();
		return taskExecutor;
	}
	
	
	/**
	 * writeESStep: In this step will:
	 * 1. read the stibo xml file using JAVA StAX(only read product element out), assemble the product bean in processor, and write into ES
	 * 2. read the attribute,category,asset,bestSeller,salesRank from cache and write it into ES
	 *
	 * @return
	 */
	@Bean
	protected Step writeESStep() {
		
		return steps.get("writeESStep").partitioner("writeESSlaveStep", writeESPartitioner()).step(writeESSlaveStep())
				.taskExecutor(writeESSlaveStepTaskExecutor()).build();
		
	}
	
	@Bean("writeESSlaveStep")
	public Step writeESSlaveStep() {
		return steps.get("stiboFileFeedStep").<Object, Object>chunk(2000).reader(writeESReader())
				.processor(writeESProcessor()).writer(writeESWriter()).build();
	}

	@Bean
	public Partitioner writeESPartitioner() {
		
		Resource[] resources;
		List<String> tasks = new ArrayList<String>();
		List<String> stiboFiles = null;
		List<String> cacheItems = null;
		try {
			resources = resoursePatternResolver.getResources(catalogFullFeedStiboFiles);
			if (null != resources) {
				stiboFiles = Arrays.stream(resources).map(resource -> {
					try {
						return resource.getURL().toExternalForm();
					} catch (IOException e) {
						// TODO Auto-generated catch block
					}
					return null;
				}).collect(Collectors.toList());
			}
		} catch (IOException e) {
			throw new RuntimeException("I/O problems when resolving the input file pattern.", e);
		}
		if (!CollectionUtils.isEmpty(stiboFiles)) {
			tasks.addAll(stiboFiles);
		}
		if (!CollectionUtils.isEmpty(partitionerESSources)) {
			tasks.addAll(partitionerESSources);
		}
		DefaultPartitioner partitioner = new DefaultPartitioner();
		partitioner.setResources(tasks);
		return partitioner;
	}
	
	@Bean
	public TaskExecutor writeESSlaveStepTaskExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setMaxPoolSize(6);
		taskExecutor.setMaxPoolSize(6);
		taskExecutor.setCorePoolSize(6);
		taskExecutor.setQueueCapacity(6);
		taskExecutor.afterPropertiesSet();
		return taskExecutor;
	}
	
	@StepScope
	@Bean
	public WriteESReader writeESReader() {
		return new WriteESReader();
	}

	@StepScope
	@Bean
	public WriteESProcessor writeESProcessor() {
		return new WriteESProcessor();
	}

	@StepScope
	@Bean
	public WriteESWriter writeESWriter() {
		return new WriteESWriter();
	}

	@Bean
	public ItemWriteListener<ESBean> catalogDataWriteListener() {
		return new CatalogDataWriteListener();
	}

	@Bean
	public Job fullParseJob() {
		return jobs.get("fullParseJob").start(loadCacheStep()).next(writeESStep())
				.build();
	}

	@Bean
	public FeedEngineCache feedEngineCache() {
		return new FeedEngineCache();
	}
}
