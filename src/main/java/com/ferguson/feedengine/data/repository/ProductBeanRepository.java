package com.ferguson.feedengine.data.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.ferguson.feedengine.data.model.ProductBean;

public interface ProductBeanRepository extends ElasticsearchRepository<ProductBean, Long> {

}
