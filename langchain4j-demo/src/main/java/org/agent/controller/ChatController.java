package org.agent.controller;

import org.agent.ai.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("")
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
    @Autowired
    private ChatService chatService;

    @PostMapping(value = "/chat", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public Flux<String> chat(@RequestBody String message) {
        return chatService.chat("djw",message);
    }
}
