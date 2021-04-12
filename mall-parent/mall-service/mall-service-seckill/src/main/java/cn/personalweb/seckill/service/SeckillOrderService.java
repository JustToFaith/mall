package cn.personalweb.seckill.service;

import cn.personalweb.seckill.pojo.SeckillStatus;

public interface SeckillOrderService {
    /***
     * 添加秒杀订单
     * @param id:商品ID
     * @param time:商品秒杀开始时间
     * @param username:用户登录名
     * @return
     */
    Boolean add(Long id, String time, String username);


    /***
     * 抢单状态查询
     * @param username
     * @return
     */
    SeckillStatus queryStatus(String username);

    /***
     * 更新订单状态
     * @param out_trade_no
     * @param transaction_id
     * @param username
     */
    void updatePayStatus(String out_trade_no, String transaction_id,String username);

    /***
     * 关闭订单，回滚库存
     */
    void closeOrder(String username);
}
