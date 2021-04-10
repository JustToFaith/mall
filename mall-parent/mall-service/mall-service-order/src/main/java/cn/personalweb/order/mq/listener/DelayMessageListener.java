package cn.personalweb.order.mq.listener;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 死信队列过期时间监听, 当在延迟队列过期之后发送到这个队列，然后消费他
 */
@Component
@RabbitListener(queues = "orderListenerQueue")
public class DelayMessageListener {

    /**
     * 处理延时队列
     * @param message
     */
    @RabbitHandler
    public void getDelayMessage(String message) {
        // TODO 这里取消订单，回滚
    }
}
