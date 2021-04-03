package cn.personalweb;

import entry.IdWorker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableEurekaClient // 开启eureka客户端
/**
 * 开启通用mapper的包扫描
 */
@MapperScan(basePackages = {"cn.personalweb.dao"})
public class GoodsApplication {
    public static void main(String[] args) {
        SpringApplication.run(GoodsApplication.class, args);
    }

    /***
     * IdWorker
     * @return
     */
//    @Bean
//    public IdWorker idWorker(){
//        return new IdWorker(0, 0);
//    }
}
