package cn.personalweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author liz
 */
@SpringBootApplication
@EnableEurekaServer // 开启eureka服务
public class EurekaApplication {
    /**
     * 加载启动类，以启动类为当前springboot的配置标准
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(EurekaApplication.class, args);
    }
}
