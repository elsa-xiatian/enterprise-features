package com.enterprise.model.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {

    private String token; // JWT令牌
    private String refreshToken; // 刷新令牌
    private Long expiresIn; // 令牌过期时间（秒）
    private String username; // 用户名
    private int loginStatus; // 登录状态（200-成功，201-需要二次验证）
    private String mfaToken; // 临时令牌（用于二次验证时标识用户）

}
