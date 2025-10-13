package com.enterprise.model.dto;

import lombok.Data;

@Data
public class MfaVerifyRequest {
    private String mfaToken; // 登录接口返回的临时令牌
    private String code; // 短信验证码
}
