package com.ferguson.feedengine.batch.step.stibofeed;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.ferguson.feedengine.batch.utils.XMLStreamParser;

public class CatalogDataReader implements ItemReader<Map>, StepExecutionListener {

	@Autowired
	private XMLStreamParser parser;
	
	@Value("${parser.xml.catalog.filepath}")
	public String filePath;

	@Autowired
    ResourcePatternResolver resoursePatternResolver;
	
	
	private XMLEventReader reader;
	private StepExecution stepExecution;
	

	@Override
	public void beforeStep(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
		
		Resource resource = resoursePatternResolver.getResource(filePath);
		
		XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
		try {
			reader = xmlInputFactory.createXMLEventReader(new FileInputStream(resource.getFile()));
		} catch (XMLStreamException | IOException e) {
			// TODO Auto-generated catch block
		}
		System.out.println("reader started");
	}

	@Override
	public Map read() throws Exception {
//		this.stepExecution.setExitStatus(new ExitStatus("Complete But Skip Product Feed"));
		return parser.parse(reader);
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		System.out.println("reader closed");
		try {
			reader.close();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
		}
		return ExitStatus.COMPLETED;
	}
}