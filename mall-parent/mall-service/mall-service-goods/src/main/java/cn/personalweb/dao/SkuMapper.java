package cn.personalweb.dao;
import cn.personalweb.goods.pojo.Sku;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


public interface SkuMapper extends Mapper<Sku> {

    @Select("select *from tb_sku where status = #{status} limit 0,500")
    public List<Sku> selectAllStatus(Sku sku);
}
