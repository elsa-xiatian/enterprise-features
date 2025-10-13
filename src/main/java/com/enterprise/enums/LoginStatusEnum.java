package com.enterprise.enums;

public enum LoginStatusEnum {
    // 用户名密码验证通过，无需二次验证
    SUCCESS(200, "登录成功"),
    // 用户名密码验证通过，但需要二次验证
    REQUIRE_MFA(201, "需要二次验证");

    private final int code;
    private final String msg;

    LoginStatusEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    // getter方法
    public int getCode() { return code; }
    public String getMsg() { return msg; }
}
