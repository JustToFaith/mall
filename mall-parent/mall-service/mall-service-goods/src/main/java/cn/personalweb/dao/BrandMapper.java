package cn.personalweb.dao;

import cn.personalweb.goods.pojo.Brand;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


public interface BrandMapper extends Mapper<Brand> {

    /**
     * 根据分类id查询品牌集合
     * @param categoryId
     * @return
     */
    @Select("select tb.* from tb_brand tb, tb_category_brand tcb where tb.id = tcb.brand_id and tcb.category_id = #{categoryId}")
    List<Brand> findByCategory(Integer categoryId);
}
