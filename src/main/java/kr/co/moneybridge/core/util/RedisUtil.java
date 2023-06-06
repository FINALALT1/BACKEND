package kr.co.moneybridge.core.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisUtil {
    private final RedisTemplate<String, String> redisTemplate;
    private final RedisTemplate<String, String> redisBlackListTemplate;

    public void set(String key, String refreshToken, Long milliSeconds) {
        redisTemplate.opsForValue().set(key, refreshToken, milliSeconds, TimeUnit.MILLISECONDS);
    }
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }
    public boolean delete(String key) {
        return redisTemplate.delete(key);
    }
    public void setBlackList(String key, String accessToken, Long milliSeconds) {
        redisBlackListTemplate.opsForValue().set(key, accessToken, milliSeconds, TimeUnit.MILLISECONDS);
    }
    public boolean hasKeyBlackList(String key) {
        return redisBlackListTemplate.hasKey(key);
    }
}
