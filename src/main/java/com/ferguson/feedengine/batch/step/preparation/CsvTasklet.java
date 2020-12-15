package com.ferguson.feedengine.batch.step.preparation;

import com.ferguson.feedengine.data.model.BaseBean;
import com.ferguson.feedengine.data.model.BestSellerBean;
import com.ferguson.feedengine.data.model.TempBestSellerBean;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CsvTasklet implements Tasklet, StepExecutionListener {

    private ItemStreamReader<BaseBean> itemReader;
    @Autowired
    @Qualifier("bestSellerBeanRepository")
    private ElasticsearchRepository bestSellerRepository;
    @Autowired
    @Qualifier("salesRankBeanRepository")
    private ElasticsearchRepository salesRankRepository;
    private String filename;

    public CsvTasklet(ItemStreamReader<BaseBean> itemReader) {
        this.itemReader = itemReader;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        ExecutionContext executionContext = stepExecution.getExecutionContext();
        this.filename = (String) executionContext.get("fileName");
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        List<BaseBean> items = new ArrayList<>();
        BaseBean item;
        try {
            itemReader.open(chunkContext.getStepContext().getStepExecution().getExecutionContext());
            while ((item = itemReader.read()) != null) {
                items.add(item);
            }
        } finally {
            itemReader.close();
        }


        switch (filename) {
            case "best_seller_data.csv":
                Map<String, List<BaseBean>> map = items.stream().collect(Collectors.groupingBy(i -> i.getSkuId()));
                List<BaseBean> lItems = map.entrySet().stream().map(e -> {
                    Map<String, String> collect = e.getValue().stream().collect(Collectors.toMap(bean -> {
                        TempBestSellerBean b = (TempBestSellerBean) bean;
                        return b.getBranch() + "_sales";
                    }, bean -> ((TempBestSellerBean) bean).getRank()));
                    BestSellerBean bean = new BestSellerBean(e.getKey(), collect);
                    return bean;
                }).collect(Collectors.toList());
                bestSellerRepository.saveAll(lItems);
                break;
            case "sales_rank_data.csv":
                salesRankRepository.saveAll(items);
                break;
            default:
                break;
        }

        return RepeatStatus.FINISHED;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return ExitStatus.COMPLETED;
    }
}