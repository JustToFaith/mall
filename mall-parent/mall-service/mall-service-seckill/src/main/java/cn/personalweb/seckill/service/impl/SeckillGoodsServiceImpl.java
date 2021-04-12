package cn.personalweb.seckill.service.impl;

import cn.personalweb.seckill.pojo.SeckillGoods;
import cn.personalweb.seckill.service.SeckillGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeckillGoodsServiceImpl implements SeckillGoodsService {
    @Autowired
    private RedisTemplate redisTemplate;

    /***
     * Redis中根据Key获取秒杀商品列表
     * @param key
     * @return
     */
    @Override
    public List<SeckillGoods> list(String key) {
        return redisTemplate.boundHashOps("SeckillGoods_"+key).values();
    }

    /****
     * 根据商品ID查询商品详情
     * @param time:时间区间
     * @param id:商品ID
     * @return
     */
    @Override
    public SeckillGoods one(String time, Long id) {
        return (SeckillGoods) redisTemplate.boundHashOps("SeckillGoods_"+time).get(id);
    }
}
