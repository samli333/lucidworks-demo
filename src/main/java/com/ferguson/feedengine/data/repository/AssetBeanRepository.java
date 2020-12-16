package com.ferguson.feedengine.data.repository;

import com.ferguson.feedengine.data.model.AssetBean;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface AssetBeanRepository extends ElasticsearchRepository<AssetBean, Long> {

}
