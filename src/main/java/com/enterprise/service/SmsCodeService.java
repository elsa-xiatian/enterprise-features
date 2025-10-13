package com.enterprise.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class SmsCodeService {
    // 注入RedisTemplate（Spring Boot自动配置）
    private final RedisTemplate<String, String> redisTemplate;

    // 构造器注入（@RequiredArgsConstructor可简化，这里显式写便于理解）
    public SmsCodeService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 验证码key前缀（避免与其他Redis数据冲突）
    private static final String SMS_CODE_KEY_PREFIX = "mfa:sms:code:";
    // 验证码有效期：5分钟（与之前一致）
    private static final long CODE_EXPIRE_MINUTES = 5;

    /**
     * 生成验证码并存储到Redis，同时模拟发送
     * @param phone 接收验证码的手机号
     */
    public void sendCode(String phone) {
        // 1. 生成6位数字验证码
        String code = String.format("%06d", new Random().nextInt(999999));

        // 2. 构建Redis的key（格式：mfa:sms:code:13800138000）
        String redisKey = SMS_CODE_KEY_PREFIX + phone;

        // 3. 存储验证码到Redis（设置过期时间）
        // opsForValue()：操作Redis的String类型数据
        // set(key, value, 过期时间, 时间单位)
        redisTemplate.opsForValue().set(redisKey, code, CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);

        // 4. 模拟发送（控制台打印，方便调试）
        System.out.println("【模拟发送短信】手机号：" + phone + "，验证码：" + code + "（有效期" + CODE_EXPIRE_MINUTES + "分钟）");
    }

    /**
     * 验证验证码是否有效
     * @param phone 手机号
     * @param inputCode 用户输入的验证码
     * @return true-验证通过，false-验证失败
     */
    public boolean validateCode(String phone, String inputCode) {
        // 1. 构建Redis的key
        String redisKey = SMS_CODE_KEY_PREFIX + phone;

        // 2. 从Redis获取存储的验证码
        // get(key)：获取key对应的value，若key不存在则返回null
        String cachedCode = redisTemplate.opsForValue().get(redisKey);

        // 3. 验证逻辑：验证码不存在/为空/不匹配，均返回false
        if (cachedCode == null || inputCode == null || !cachedCode.equals(inputCode)) {
            return false;
        }

        // 4. 验证通过后，删除Redis中的验证码（防止重复使用）
        // delete(key)：删除指定key
        redisTemplate.delete(redisKey);

        return true;
    }
}
