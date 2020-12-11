package com.ferguson;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemReader;

public class ElementReader implements ItemReader<Map>, StepExecutionListener {

	private XMLEventReader reader;

	@Override
	public void beforeStep(StepExecution stepExecution) {
		
		String filePath = "/Lucidworks/156032797-156032811.xml";
//		String filePath = "/Lucidworks/exported-1559527628716.txt";
		XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
		try {
			reader = xmlInputFactory.createXMLEventReader(new FileInputStream(filePath));
		} catch (FileNotFoundException | XMLStreamException e) {
			// TODO Auto-generated catch block
		}
		System.out.println("reader started");
	}

	@Override
	public Map read() throws Exception {
		System.out.println("++++++++++++ "+Thread.currentThread().getName()+" reader read element");
		
		return CatalogFileParser.parse(reader);
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