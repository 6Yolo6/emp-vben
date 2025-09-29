package com.ldjt.emp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Redis测试Controller
 *
 * @author wdf
 * @since 2025/9/29 10:00
 */
@RestController
@RequestMapping("/api/redis")
public class RedisController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 测试Redis存储
     */
    @PostMapping("/set")
    public Map<String, Object> setValue(@RequestParam String key, @RequestParam String value) {
        redisTemplate.opsForValue().set(key, value, 30, TimeUnit.MINUTES);
        
        Map<String, Object> result = new HashMap<>();
        result.put("message", "存储成功");
        result.put("key", key);
        result.put("value", value);
        result.put("expireTime", "30分钟");
        
        return result;
    }

    /**
     * 测试Redis读取
     */
    @GetMapping("/get/{key}")
    public Map<String, Object> getValue(@PathVariable String key) {
        Object value = redisTemplate.opsForValue().get(key);
        
        Map<String, Object> result = new HashMap<>();
        result.put("key", key);
        result.put("value", value);
        result.put("exists", value != null);
        
        return result;
    }

    /**
     * 测试Redis删除
     */
    @DeleteMapping("/del/{key}")
    public Map<String, Object> deleteValue(@PathVariable String key) {
        Boolean deleted = redisTemplate.delete(key);
        
        Map<String, Object> result = new HashMap<>();
        result.put("key", key);
        result.put("deleted", deleted);
        
        return result;
    }

    /**
     * 测试Hash存储
     */
    @PostMapping("/hash")
    public Map<String, Object> setHashValue(
            @RequestParam String hashKey,
            @RequestParam String field,
            @RequestParam String value) {
        
        redisTemplate.opsForHash().put(hashKey, field, value);
        
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Hash存储成功");
        result.put("hashKey", hashKey);
        result.put("field", field);
        result.put("value", value);
        
        return result;
    }

    /**
     * 获取Hash中的所有数据
     */
    @GetMapping("/hash/{hashKey}")
    public Map<String, Object> getHashValues(@PathVariable String hashKey) {
        Map<Object, Object> hashValues = redisTemplate.opsForHash().entries(hashKey);
        
        Map<String, Object> result = new HashMap<>();
        result.put("hashKey", hashKey);
        result.put("data", hashValues);
        
        return result;
    }
}
