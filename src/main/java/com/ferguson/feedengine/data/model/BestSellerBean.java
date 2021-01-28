package com.ferguson.feedengine.data.model;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.util.Map;

@Document(indexName = "best_seller")
public class BestSellerBean extends BaseBean {

	@Field(index=false)
    private Map<String, String> skuBranchSales;

    public BestSellerBean(String skuId, Map<String, String> branchRankMap) {
        super(skuId);
        this.skuBranchSales = branchRankMap;
    }

    public Map<String, String> getSkuBranchSales() {
        return skuBranchSales;
    }

    public void setSkuBranchSales(Map<String, String> skuBranchSales) {
        this.skuBranchSales = skuBranchSales;
    }
}
