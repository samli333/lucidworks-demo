package com.ferguson.feedengine.batch.step.writees;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.ferguson.feedengine.batch.JobConstants;
import com.ferguson.feedengine.batch.utils.FeedEngineCache;
import com.ferguson.feedengine.batch.utils.XMLStreamParser;
import com.ferguson.feedengine.batch.utils.XMLStreamParser.Operation;

public class WriteESReader implements ItemReader<Object>, StepExecutionListener, JobConstants {

	@Autowired
	private XMLStreamParser parser;
	
	@Autowired
    ResourcePatternResolver resoursePatternResolver;
	
	@Autowired
	@Qualifier("feedEngineCache")
	private FeedEngineCache feedEngineCache;
	
	private StepExecution stepExecution;
	
	private XMLEventReader reader;
	private Iterator<Map.Entry> iterator;

	@Override
	public void beforeStep(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
		String task = this.stepExecution.getExecutionContext().getString(STEP_PARAM_NAME_TASK);
		System.out.println("----------------" +this + Thread.currentThread().getName() + task);
		if (null == task) {
			return;
		}
		if (task.startsWith("file:")) {
			Resource resource = resoursePatternResolver.getResource(task);
			
			XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
			try {
				reader = xmlInputFactory.createXMLEventReader(new FileInputStream(resource.getFile()));
			} catch (XMLStreamException | IOException e) {
				// TODO Auto-generated catch block
			}
		} else {
			Map cache = (Map)feedEngineCache.get(task);
			if (null != cache) {
				iterator = cache.entrySet().iterator();
			}
		}
		
	}

	@Override
	public Object read() throws Exception {
//		this.stepExecution.setExitStatus(new ExitStatus("Complete But Skip Product Feed"));
		if (null != reader) {
			return parser.parse(reader, Operation.PARSE_PRODUCT_ONLY);
		}
		
		while (null != iterator && iterator.hasNext()) {
			Map.Entry next = iterator.next();
			if (null == next) {
				continue;
			}
			return next.getValue();
		}
		return null;
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		if (null != reader) {
			try {
				
				reader.close();
			} catch (XMLStreamException e) {
				// TODO Auto-generated catch block
			}
		}
		return ExitStatus.COMPLETED;
	}
}