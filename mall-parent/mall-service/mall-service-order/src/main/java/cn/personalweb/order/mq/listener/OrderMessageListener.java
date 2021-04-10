package cn.personalweb.order.mq.listener;

import cn.personalweb.order.service.OrderService;
import com.alibaba.fastjson.JSON;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Map;

/**
 * 支付服务收到微信的回调之后发送消息到mq，
 * 订单服务：订单信息监听，用户支付成功或失败后回调发送给服务端，服务端在判断支付是不是成功，如果成就修改订单状态，否则就删除订单
 */
@Component
@RabbitListener(queues = "${mq.pay.queue.order}")
public class OrderMessageListener {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private OrderService orderService;

    /***
     * 接收消息
     */
    @RabbitHandler
    public void consumeMessage(String msg) throws ParseException {
        //将数据转成Map
        Map<String,String> result = JSON.parseObject(msg,Map.class);

        //return_code=SUCCESS
        String return_code = result.get("return_code");
        //业务结果
        String result_code = result.get("result_code");

        //业务结果 result_code=SUCCESS/FAIL，修改订单状态
        if(return_code.equalsIgnoreCase("success") ){
            //获取订单号
            String outtradeno = result.get("out_trade_no");
            //业务结果
            if(result_code.equalsIgnoreCase("success")){
                if(outtradeno!=null){
                    //修改订单状态  out_trade_no
                    orderService.updateStatus(outtradeno,result.get("time_end"),result.get("transaction_id"));
                }
            }else{
                // TODO 关闭支付， 调用微信服务器，关闭订单，这里还没写

                //订单删除
                orderService.deleteOrder(outtradeno);

            }
        }

    }
}
