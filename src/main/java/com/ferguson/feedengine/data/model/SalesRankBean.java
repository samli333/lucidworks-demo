package com.ferguson.feedengine.data.model;

import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "sales_rank")
public class SalesRankBean extends BaseBean {

    private String sales;

    public SalesRankBean(String skuId, String sales) {
        super(skuId);
        this.sales = sales;
    }

    public String getSales() {
        return sales;
    }

    public void setSales(String sales) {
        this.sales = sales;
    }
}
