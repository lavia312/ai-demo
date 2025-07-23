package org.agent.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Web页面控制器
 * @author djw
 * @date 2025-01-16
 */
@Controller
public class WebController {

    /**
     * 首页
     * @return 聊天页面
     */
    @GetMapping("/")
    public String index() {
        return "chat";
    }

    /**
     * 聊天页面
     * @return 聊天页面
     */
    @GetMapping("/chat")
    public String chat() {
        return "chat";
    }


    /**
     * 流式响应测试页面
     * @return 流式测试页面
     */
    @GetMapping("/stream-chat")
    public String streamTest() {
        return "stream-chat";
    }

    /**
     * 日报生成页面
     * @return 日报页面
     */
    @GetMapping("/daily-report")
    public String dailyReport() {
        return "daily-report";
    }

}
