package com.enterprise.util;

public class PasswordStrengthUtil {
    //正则表达式
    private static final String REG_UPPER = ".*[A-Z].*";
    private static final String REG_LOWER = ".*[a-z].*";
    private static final String REG_NUMBER = ".*\\d.*";
    private static final String REG_SPECIAL = ".*[!@#$%^&*()_+-=\\[\\]{};':\"\\\\|,.<>\\/?].*";

    public static boolean checkPasswordStrength(String password){
        if(password == null || password.length() < 8) return false;

        int count = 0;
        if(password.matches(REG_UPPER)) count++;
        if(password.matches(REG_LOWER)) count++;
        if(password.matches(REG_NUMBER)) count++;
        if(password.matches(REG_SPECIAL)) count++;

        return count >= 3;
        }


    public static String getStrengthTip(String password) {
        if (password == null || password.length() < 8) {
            return "密码长度需至少8位";
        }
        int strengthCount = 0;
        if (password.matches(REG_UPPER)) strengthCount++;
        if (password.matches(REG_LOWER)) strengthCount++;
        if (password.matches(REG_NUMBER)) strengthCount++;
        if (password.matches(REG_SPECIAL)) strengthCount++;

        return switch (strengthCount) {
            case 0, 1 -> "密码强度过弱，请包含大写字母、小写字母、数字、特殊字符中的至少3项";
            case 2 -> "密码强度中等，建议增加特殊字符提升安全性";
            case 3, 4 -> "密码强度合格";
            default -> "密码格式无效";
        };
    }
    }

