package cn.personalweb.search.dao;

import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.stereotype.Repository;
import cn.personalweb.search.pojo.SkuInfo;


@Repository
public interface SkuEsMapper extends ElasticsearchCrudRepository<SkuInfo, Long> {
}
