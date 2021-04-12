package cn.personalweb.seckill.service;

import cn.personalweb.seckill.pojo.SeckillGoods;

import java.util.List;

public interface SeckillGoodsService {
    /***
     * 获取指定时间对应的秒杀商品列表
     * @param key
     */
    List<SeckillGoods> list(String key);

    /****
     * 根据ID查询商品详情
     * @param time:时间区间
     * @param id:商品ID
     */
    SeckillGoods one(String time,Long id);
}
