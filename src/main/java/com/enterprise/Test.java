package com.enterprise;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.SecureRandom;
import java.util.Base64;

public class Test {

    public static void main(String[] args) {
        // 数据库中存储的密码

        // 输入的明文密码
        String rawPassword = "123456";

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println(encodedPassword);
        boolean matches = encoder.matches(rawPassword, encodedPassword);
        System.out.println("密码是否匹配：" + matches); // 若为false，则密码加密错误

        SecureRandom random = new SecureRandom();
        byte[] key = new byte[64]; // 512位
        random.nextBytes(key);
        String base64Key = Base64.getEncoder().encodeToString(key);
        System.out.println("JWT密钥：" + base64Key); // 复制此结果到配置文件
    }
}
