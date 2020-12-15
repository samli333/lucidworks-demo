package com.ferguson.feedengine.data.model;

public class TempBestSellerBean extends BaseBean {

    private String branch;
    private String rank;

    public TempBestSellerBean(String skuId, String branch, String rank) {
        super(skuId);
        this.branch = branch;
        this.rank = rank;
    }


    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }
}
