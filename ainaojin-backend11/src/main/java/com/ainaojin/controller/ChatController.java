package com.ainaojin.controller;

import com.ainaojin.service.ChatService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ChatController {
    private final ChatService chatService;

    // 构造器注入Service
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * 聊天接口：适配前端传递的字符串类型roomId
     */
    @PostMapping("/{roomId}/chat")
    public String doChat(@PathVariable String roomId, @RequestParam String userPrompt) {
        // 转换roomId为Long（兼容前端超大数字）
        Long roomIdLong;
        try {
            roomIdLong = Long.parseLong(roomId);
        } catch (NumberFormatException e) {
            return "房间号格式错误！请重新进入房间";
        }
        return chatService.doChat(roomIdLong, userPrompt);
    }

    /**
     * 获取房间列表：返回空数组（适配前端数组解析）
     */
    @GetMapping("/rooms")
    public List<String> getRooms() {
        return List.of(); // Spring自动序列化为[]，而非字符串"[]"
    }
}