package cn.personalweb.order.mq.queue;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueueConfig {

    /**
     * 创建Queue1，延时队列，过期后发给queue2
     */
    @Bean
    public Queue orderDelayQueue() {
        return QueueBuilder.durable("orderDelayQueue")
                // 死信交换机，当queue1中的消息过期后发给死信队列
                .withArgument("x-dead-letter-exchange", "orderListenerExchange")  //死信队列中的数据需要绑定到交换机
                .withArgument("x-dead-letter-routing-key","orderListenerQueue")  // 死信交换机将消息发个死信队列也就是自己
                .build();
    }

    /**
     * 创建queue2
     */
    @Bean
    public Queue orderListenerQueue() {
        return new Queue("orderListenerQueue",true);
    }

    /**
     * 创建交换机
     */
    @Bean
    public Exchange orderListenerExchange() {
        return new DirectExchange("orderListenerExchange");
    }

    /**
     * 队列queue2绑定交换机
     */
    public Binding orderListenerBinding(Queue orderListenerQueue, Exchange orderListenerExchange) {
        return BindingBuilder.bind(orderListenerQueue).to(orderListenerExchange).with("orderListenerQueue").noargs();
    }
}
