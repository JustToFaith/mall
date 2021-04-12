package cn.personalweb.seckill.timer;

import cn.personalweb.seckill.dao.SeckillGoodsMapper;
import cn.personalweb.seckill.pojo.SeckillGoods;
import entry.DateUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 定时任务执行：采用Spring的定时任务定时将符合参与秒杀的商品查询出来再存入到Redis缓存
 */
@Component
public class SeckillGoodsPushTask {

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 定时任务每30秒执行一次
     */
    @Scheduled(cron = "0/30 * * * * ?")
    public void loadGoodsPushRedis() {
        //获取时间段集合
        List<Date> dateMenus = DateUtil.getDateMenus();
        //循环时间段
        for (Date startTime : dateMenus) {
            // 把时间转换为字符串然后拼接成存到redis的key，namespace = SeckillGoods_20195712
            String extName = DateUtil.data2str(startTime,DateUtil.PATTERN_YYYYMMDDHH);

            //根据时间段数据查询对应的秒杀商品数据，要满足条件的，必须审核通过的，库存够的，时间争正确的
            Example example = new Example(SeckillGoods.class);
            Example.Criteria criteria = example.createCriteria();
            // 1)商品必须审核通过  status=1
            criteria.andEqualTo("status","1");
            // 2)库存>0
            criteria.andGreaterThan("stockCount",0);
            // 3)开始时间<=活动开始时间
            criteria.andGreaterThanOrEqualTo("startTime",startTime);
            // 4)活动结束时间<开始时间+2小时
            criteria.andLessThan("endTime", DateUtil.addDateHour(startTime,2));
            // 5)排除之前已经加载到Redis缓存中的商品数据
            Set keys = redisTemplate.boundHashOps("SeckillGoods_" + extName).keys();
            if(keys!=null && keys.size()>0){
                // 查询的数据id要不在redis中的
                criteria.andNotIn("id",keys);
            }

            //查询数据
            List<SeckillGoods> seckillGoods = seckillGoodsMapper.selectByExample(example);

            //将秒杀商品数据存入到Redis缓存
            for (SeckillGoods seckillGood : seckillGoods) {
                redisTemplate.boundHashOps("SeckillGoods_"+extName).put(seckillGood.getId(),seckillGood);
                // 设置过期时间2小时
                // fixme 这里时间不确定，有问题
//                redisTemplate.expireAt("SeckillGoods_"+extName,DateUtil.addDateHour(dateMenu, 2));
                redisTemplate.expireAt("SeckillGoods_"+extName,DateUtil.addDateHour(new Date(), 2));

                // 获取消费队列数组
                Long[] ids = pushIds(seckillGood.getStockCount() , seckillGood.getId());
                redisTemplate.boundListOps("SeckillGoodsCountList_" + seckillGood.getId()).leftPushAll(ids);
                // 自增计数器， 记录商品的数量
                redisTemplate.boundHashOps("SeckillGoodsCount").increment(seckillGood.getId(),seckillGood.getStockCount());

            }
        }
    }

    /***
     * 将商品ID存入到数组中 用于秒杀的时候消费，避免重复消费
     * @param len:长度
     * @param id :值
     * @return
     */
    public Long[] pushIds(int len,Long id){
        Long[] ids = new Long[len];
        for (int i = 0; i <ids.length ; i++) {
            ids[i]=id;
        }
        return ids;
    }
}
