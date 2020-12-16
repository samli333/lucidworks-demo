package com.ferguson.feedengine.data.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.ferguson.feedengine.data.model.AttributeBean;

public interface AttributeBeanRepository extends ElasticsearchRepository<AttributeBean, Long> {

}
