package cn.personalweb.seckill.service.impl;

import cn.personalweb.seckill.dao.SeckillGoodsMapper;
import cn.personalweb.seckill.dao.SeckillOrderMapper;
import cn.personalweb.seckill.pojo.SeckillGoods;
import cn.personalweb.seckill.pojo.SeckillOrder;
import cn.personalweb.seckill.pojo.SeckillStatus;
import cn.personalweb.seckill.service.SeckillGoodsService;
import cn.personalweb.seckill.service.SeckillOrderService;
import cn.personalweb.seckill.task.MultiThreadingCreateOrder;
import entry.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;

public class SeckillOrderServiceImpl implements SeckillOrderService {



    @Autowired
    private MultiThreadingCreateOrder multiThreadingCreateOrder;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;


    /****
     * 添加订单
     * @param id 商品id
     * @param time 时间
     * @param username 用户名
     */
    @Override
    public Boolean add(Long id, String time, String username) {
        // 通过递增判断用户是否有重复提交，redis是单线程的，所以返回的值永远不会重复
        Long userQueueCount = redisTemplate.boundHashOps("UserQueueCount").increment(username, 1);
        if (userQueueCount > 1) {
            // 100状态码表示有重复
            throw new RuntimeException(String.valueOf(StatusCode.REPERROR));
        }
        //排队信息封装, 多线程异步在另一边调用这个细腻些
        SeckillStatus seckillStatus = new SeckillStatus(username, new Date(),1, id,time);

        //将秒杀抢单信息存入到Redis中,这里采用List方式存储,List本身是一个队列
        redisTemplate.boundListOps("SeckillOrderQueue").leftPush(seckillStatus);

        //将抢单状态存入到Redis中
        redisTemplate.boundHashOps("UserQueueStatus").put(username,seckillStatus);

        // 多线程创建订单
        multiThreadingCreateOrder.createOrder();

        return true;
    }

    /***
     * 抢单状态查询
     * @param username
     * @return
     */
    @Override
    public SeckillStatus queryStatus(String username) {
        return (SeckillStatus) redisTemplate.boundHashOps("UserQueueStatus").get(username);
    }

    /***
     * 更新订单状态 支付成功
     * @param out_trade_no
     * @param transaction_id
     * @param username
     */
    @Override
    public void updatePayStatus(String out_trade_no, String transaction_id,String username) {
        //订单数据从Redis数据库查询出来
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.boundHashOps("SeckillOrder").get(username);
        //修改状态
        seckillOrder.setStatus("1");

        //支付时间
        seckillOrder.setPayTime(new Date());
        //同步到MySQL中
        seckillOrderMapper.insertSelective(seckillOrder);

        //清空Redis缓存
        redisTemplate.boundHashOps("SeckillOrder").delete(username);
        //清空用户排队数据
        redisTemplate.boundHashOps("UserQueueCount").delete(username);
        //删除抢购状态信息
        redisTemplate.boundHashOps("UserQueueStatus").delete(username);
    }

    /***
     * 关闭订单，回滚库存
     * @param username
     */
    @Override
    public void closeOrder(String username) {
        //将消息转换成SeckillStatus
        SeckillStatus seckillStatus = (SeckillStatus) redisTemplate.boundHashOps("UserQueueStatus").get(username);
        //获取Redis中订单信息
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.boundHashOps("SeckillOrder").get(username);
        //如果Redis中有订单信息，说明用户未支付
        if(seckillStatus!=null && seckillOrder!=null){
            //删除订单
            redisTemplate.boundHashOps("SeckillOrder").delete(username);
            //回滚库存
            //1)从Redis中获取该商品
            SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps("SeckillGoods_"+seckillStatus.getTime()).get(seckillStatus.getGoodsId());

            //2)如果Redis中没有，则从数据库中加载
            if(seckillGoods==null){
                seckillGoods = seckillGoodsMapper.selectByPrimaryKey(seckillStatus.getGoodsId());
            }

            //3)数量+1  (递增数量+1，队列数量+1)
            Long surplusCount = redisTemplate.boundHashOps("SeckillGoodsCount").increment(seckillStatus.getGoodsId(), 1);
            seckillGoods.setStockCount(surplusCount.intValue());
            redisTemplate.boundListOps("SeckillGoodsCountList_" + seckillStatus.getGoodsId()).leftPush(seckillStatus.getGoodsId());

            //4)数据同步到Redis中
            redisTemplate.boundHashOps("SeckillGoods_"+seckillStatus.getTime()).put(seckillStatus.getGoodsId(),seckillGoods);

            //清理排队标示
            redisTemplate.boundHashOps("UserQueueCount").delete(seckillStatus.getUsername());

            //清理抢单标示
            redisTemplate.boundHashOps("UserQueueStatus").delete(seckillStatus.getUsername());
        }
    }
}
