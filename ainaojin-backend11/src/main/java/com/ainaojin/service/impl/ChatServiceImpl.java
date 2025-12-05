package com.ainaojin.service.impl;

import com.ainaojin.service.ChatService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatServiceImpl implements ChatService {
    // 脑筋急转弯题库
    private final Map<String, String> riddles = new HashMap<>();
    // 存储每个房间当前的题目
    private final Map<Long, String> roomRiddleMap = new ConcurrentHashMap<>();
    // 存储每个房间已出过的题目（避免重复）
    private final Map<Long, Set<String>> roomUsedRiddles = new ConcurrentHashMap<>();
    private final Random random = new Random();

    // 初始化题库
    public ChatServiceImpl() {
        riddles.put("什么门永远关不上？", "球门");
        riddles.put("什么水永远用不完？", "泪水");
        riddles.put("什么书里毛病最多？", "医学书");
        riddles.put("什么路最窄？", "冤家路窄");
        riddles.put("什么东西越洗越脏？", "水");
        riddles.put("什么球不能踢？", "地球");
        riddles.put("什么车没有轮？", "风车");
    }

    @Override
    public String doChat(Long roomId, String userPrompt) {
        // 去除输入首尾空格，提升体验
        String prompt = userPrompt.trim();

        // 处理“开始”指令
        if ("开始".equals(prompt)) {
            roomUsedRiddles.putIfAbsent(roomId, new HashSet<>());
            // 获取未出过的题目
            List<String> unusedRiddles = getUnusedRiddles(roomId);
            if (unusedRiddles.isEmpty()) {
                roomRiddleMap.remove(roomId);
                roomUsedRiddles.remove(roomId);
                return "题库已答完！游戏结束！【游戏已结束】";
            }
            // 随机选一题
            String randomQuestion = unusedRiddles.get(random.nextInt(unusedRiddles.size()));
            roomRiddleMap.put(roomId, randomQuestion);
            roomUsedRiddles.get(roomId).add(randomQuestion);
            return "欢迎来到AI急转弯！🎉\n" + randomQuestion;
        }

        // 处理“结束”指令
        if ("结束".equals(prompt)) {
            roomRiddleMap.remove(roomId);
            roomUsedRiddles.remove(roomId);
            return "游戏已结束！感谢参与！【游戏已结束】";
        }

        // 游戏未启动
        String currentQuestion = roomRiddleMap.get(roomId);
        if (currentQuestion == null) {
            return "请先发送“开始”启动游戏！";
        }

        // 验证答案（精确匹配）
        String correctAnswer = riddles.get(currentQuestion);
        if (correctAnswer.equals(prompt)) {
            // 答对后自动出下一题
            List<String> unusedRiddles = getUnusedRiddles(roomId);
            if (unusedRiddles.isEmpty()) {
                roomRiddleMap.remove(roomId);
                roomUsedRiddles.remove(roomId);
                return "回答正确！✅ 答案是：" + correctAnswer + "\n题库已答完！游戏结束！【游戏已结束】";
            }
            String nextQuestion = unusedRiddles.get(random.nextInt(unusedRiddles.size()));
            roomRiddleMap.put(roomId, nextQuestion);
            roomUsedRiddles.get(roomId).add(nextQuestion);
            return "回答正确！✅ 答案是：" + correctAnswer + "\n下一题：" + nextQuestion;
        } else {
            return "回答错误！❌ 再想想？";
        }
    }

    // 获取房间未出过的题目
    private List<String> getUnusedRiddles(Long roomId) {
        Set<String> used = roomUsedRiddles.getOrDefault(roomId, new HashSet<>());
        return riddles.keySet().stream()
                .filter(q -> !used.contains(q))
                .toList();
    }
}