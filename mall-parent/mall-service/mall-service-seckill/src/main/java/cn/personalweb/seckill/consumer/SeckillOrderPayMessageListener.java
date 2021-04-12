package cn.personalweb.seckill.consumer;

import cn.personalweb.seckill.service.SeckillOrderService;
import com.alibaba.fastjson.JSON;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 监听支付成功后回调回来的消息
 */
@Component
@RabbitListener(queues = "${mq.pay.queue.seckillorder}")
public class SeckillOrderPayMessageListener {

    @Autowired
    private SeckillOrderService seckillOrderService;

    /**
     * 监听消费消息
     * @param message
     */
    @RabbitHandler
    public void consumeMessage(@Payload String message){
        System.out.println(message);
        //将消息转换成Map对象
        Map<String,String> resultMap = JSON.parseObject(message,Map.class);
        String return_code = resultMap.get("return_code");
        String result_code = resultMap.get("result_code");
        if (return_code.equalsIgnoreCase("success")) {

            String out_trade_no = resultMap.get("out_trade_no");

            Map<String, String> attachMap = JSON.parseObject(resultMap.get("attach"), Map.class);
            if (result_code.equalsIgnoreCase("success")) {
                // 支付成功
                seckillOrderService.updatePayStatus(out_trade_no, resultMap.get("transaction_id"), attachMap.get("username"));

            } else {
                //支付失败,删除订单
                seckillOrderService.closeOrder(attachMap.get("username"));
            }
        }
        System.out.println("监听到的消息:"+resultMap);
    }
}
