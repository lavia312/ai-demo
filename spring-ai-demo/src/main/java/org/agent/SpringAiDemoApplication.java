package org.agent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
        System.out.println("Spring AI Demo 启动成功！");
        System.out.println("聊天地址: http://localhost:8081");
        System.out.println("日报地址: http://localhost:8081/report");
        System.out.println("=================================================");
    }
}