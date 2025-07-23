package org.agent;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Spring AI MCP Demo 主启动类
 * @author djw
 * @date 2025-01-16
 */
@SpringBootApplication
public class SpringAiDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAiDemoApplication.class, args);
        System.out.println("=================================================");
        System.out.println("Spring AI MCP Demo 启动成功！");
        System.out.println("访问地址: http://localhost:8081");
        System.out.println("=================================================");
    }
    @Bean
    CommandLineRunner runner() {
        return args -> {
            System.out.println("Spring AI MCP Demo 启动成功！");
            System.out.println("访问地址: http://localhost:8081");
        };
    }
}