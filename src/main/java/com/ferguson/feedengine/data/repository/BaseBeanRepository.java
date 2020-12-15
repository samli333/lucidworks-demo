package com.ferguson.feedengine.data.repository;

import com.ferguson.feedengine.data.model.BaseBean;
import com.ferguson.feedengine.data.model.BestSellerBean;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface BaseBeanRepository extends ElasticsearchRepository<BaseBean, Long> {

}