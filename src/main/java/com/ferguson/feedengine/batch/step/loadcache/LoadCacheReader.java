package com.ferguson.feedengine.batch.step.loadcache;

import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.RecordFieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.ferguson.feedengine.batch.JobConstants;
import com.ferguson.feedengine.batch.utils.XMLStreamParser;
import com.ferguson.feedengine.batch.utils.XMLStreamParser.Operation;
import com.ferguson.feedengine.data.model.BaseBean;
import com.ferguson.feedengine.data.model.SalesRankBean;
import com.ferguson.feedengine.data.model.TempBestSellerBean;

import io.micrometer.core.instrument.util.StringUtils;

public class LoadCacheReader implements ItemReader<Object>, StepExecutionListener, JobConstants{

	@Autowired
	private XMLStreamParser parser;

	@Autowired
	ResourcePatternResolver resoursePatternResolver;

	private XMLEventReader reader;

	private ItemStreamReader<BaseBean> itemReader;

	private StepExecution stepExecution;
	
	private boolean fileOpend = false;
	

	@Override
	public void beforeStep(StepExecution stepExecution) {

		this.stepExecution = stepExecution;
		String fileName = this.stepExecution.getExecutionContext().getString(STEP_PARAM_NAME_TASK);
		System.out.println("----------------" +this + Thread.currentThread().getName() + fileName);
		if (StringUtils.isBlank(fileName)) {
			throw new IllegalArgumentException("file name must be define.");
		}
		if (fileName.endsWith(".xml")) {
			initialXMLReader(fileName);
		} else {
			initialCSVFileReader(fileName);
		}
	}

	private void initialXMLReader(String fileName) throws FactoryConfigurationError {
		Resource resource = resoursePatternResolver.getResource(fileName);

		XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
		try {
			reader = xmlInputFactory.createXMLEventReader(new FileInputStream(resource.getFile()));
		} catch (XMLStreamException | IOException e) {
			// TODO Auto-generated catch block
		}
	}

	@Override
	public Object read() throws Exception {
//		this.stepExecution.setExitStatus(new ExitStatus("Complete But Skip Product Feed"));
		if (null != reader) {
			return parser.parse(reader, Operation.NOT_PARSE_PRODUCT);
		} else {
			if (!fileOpend) {
				itemReader.open(this.stepExecution.getExecutionContext());
				fileOpend = false;
			}
			return itemReader.read();
		}

	}

	private void initialCSVFileReader(String fileName) {
		FlatFileItemReader<BaseBean> reader = new FlatFileItemReader<>();
		DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
		String[] tokens = null;
		Class iclass = null;
		if (null == fileName) {
			return;
		}
		if (fileName.endsWith("best_seller_data.csv")) {
			tokens = new String[] { "skuId", "branch", "rank" };
			iclass = TempBestSellerBean.class;
		} else if (fileName.endsWith("sales_rank_data.csv")) {
			tokens = new String[] { "skuId", "sales" };
			iclass = SalesRankBean.class;
		} else {
			tokens = new String[] {};
		}
		tokenizer.setNames(tokens);
		Resource resource = resoursePatternResolver.getResource(fileName);
		reader.setResource(resource);
		DefaultLineMapper<BaseBean> lineMapper = new DefaultLineMapper<>();
		lineMapper.setLineTokenizer(tokenizer);
		RecordFieldSetMapper fieldSetMapper = new RecordFieldSetMapper(iclass);
		lineMapper.setFieldSetMapper(fieldSetMapper);
		reader.setLinesToSkip(1);
		reader.setLineMapper(lineMapper);
		this.itemReader = reader;
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		try {
			if (null != reader) {
				reader.close();	
			}
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
		}
		
		if (null != itemReader) {
			itemReader.close();
		}
		return ExitStatus.COMPLETED;
	}
}