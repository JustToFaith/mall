package cn.personalweb;

import entry.IdWorker;
import entry.TokenDecode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;


@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@MapperScan(basePackages = "cn.personalweb.seckill.dao")
// 执行定时任务
@EnableScheduling
@EnableAsync
public class DecKillApplication {
    public static void main(String[] args) {
        SpringApplication.run(DecKillApplication.class, args);
    }

    @Bean
    public IdWorker idWorker() {
        return new IdWorker();
    }

    @Bean
    public TokenDecode tokenDecode() {
        return new TokenDecode();
    }

    @Autowired
    private Environment env;


    /***
     * 创建DirectExchange交换机
     * @return
     */
    @Bean
    public DirectExchange basicExchange(){
        return new DirectExchange(env.getProperty("mq.pay.exchange.order"), true,false);
    }

    /***
     * 创建队列
     * @return
     */
    @Bean(name = "queueOrder")
    public Queue queueOrder(){
        return new Queue(env.getProperty("mq.pay.queue.order"), true);
    }

    /***
     * 创建秒杀队列
     * @return
     */
    @Bean(name = "queueSeckillOrder")
    public Queue queueSeckillOrder(){
        return new Queue(env.getProperty("mq.pay.queue.seckillorder"), true);
    }

    /****
     * 队列绑定到交换机上
     * @return
     */
    @Bean
    public Binding basicBindingOrder(){
        return BindingBuilder
                .bind(queueOrder())
                .to(basicExchange())
                .with(env.getProperty("mq.pay.routing.orderkey"));
    }


    /****
     * 队列绑定到交换机上
     * @return
     */
    @Bean
    public Binding basicBindingSeckillOrder(){
        return BindingBuilder
                .bind(queueSeckillOrder())
                .to(basicExchange())
                .with(env.getProperty("mq.pay.routing.seckillorderkey"));
    }
}