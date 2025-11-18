package com.enterprise.util;

import java.security.SecureRandom;

public class RandomPasswordUtil {
    // 密码字符池：包含大写、小写、数字、特殊字符（符合强度规则）
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMBER = "0123456789";
    private static final String SPECIAL = "!@#$%^&*()_+-=[]{};':\"|,.<>/?";
    private static final String ALL_CHAR = UPPER + LOWER + NUMBER + SPECIAL;

    // 随机数生成器（线程安全）
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * 生成符合强度规则的随机密码
     * @param length 密码长度（默认10位，可调整）
     * @return 随机密码（必包含大写、小写、数字、特殊字符中的至少3项）
     */
    public static String generateRandomPassword(int length) {
        if (length < 8) {
            throw new IllegalArgumentException("密码长度不能小于8位");
        }

        StringBuilder password = new StringBuilder(length);
        // 1. 确保至少包含3类字符
        password.append(UPPER.charAt(RANDOM.nextInt(UPPER.length()))); // 至少1个大写
        password.append(LOWER.charAt(RANDOM.nextInt(LOWER.length()))); // 至少1个小写
        password.append(NUMBER.charAt(RANDOM.nextInt(NUMBER.length()))); // 至少1个数字
        // 可选,取消注释则随机密码包含随机字符
        // password.append(SPECIAL.charAt(RANDOM.nextInt(SPECIAL.length())));

        // 2. 填充剩余长度（从所有字符中随机选取）
        for (int i = password.length(); i < length; i++) {
            password.append(ALL_CHAR.charAt(RANDOM.nextInt(ALL_CHAR.length())));
        }

        // 3. 打乱密码顺序
        return shufflePassword(password.toString());
    }

    // 重载方法：默认生成10位密码
    public static String generateRandomPassword() {
        return generateRandomPassword(10);
    }

    // 打乱字符串顺序
    private static String shufflePassword(String password) {
        char[] chars = password.toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);
            // 交换字符
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }
        return new String(chars);
    }
}
