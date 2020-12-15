package com.ferguson.feedengine.data.repository;

import com.ferguson.feedengine.data.model.BestSellerBean;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface BestSellerBeanRepository extends ElasticsearchRepository<BestSellerBean, Long> {

}