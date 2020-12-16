package com.ferguson.feedengine.data.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.ferguson.feedengine.data.model.CategoryBean;

public interface CategoryBeanRepository extends ElasticsearchRepository<CategoryBean, Long> {


}
