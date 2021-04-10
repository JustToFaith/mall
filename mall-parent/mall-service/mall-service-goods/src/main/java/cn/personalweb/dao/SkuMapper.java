package cn.personalweb.dao;
import cn.personalweb.goods.pojo.Sku;
import cn.personalweb.order.pojo.OrderItem;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


public interface SkuMapper extends Mapper<Sku> {

    @Select("select *from tb_sku where status = #{status} limit 0,500")
    public List<Sku> selectAllStatus(Sku sku);

    /**
     * 递减库存
     * @param orderItem
     * @return
     */
    @Update("UPDATE tb_sku SET num=num-#{num},sale_num=sale_num+#{num} WHERE id=#{skuId} AND num>=#{num}")
    int decrCount(OrderItem orderItem);
}
