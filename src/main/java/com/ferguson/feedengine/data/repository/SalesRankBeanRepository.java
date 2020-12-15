package com.ferguson.feedengine.data.repository;

import com.ferguson.feedengine.data.model.SalesRankBean;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SalesRankBeanRepository extends ElasticsearchRepository<SalesRankBean, Long> {

}