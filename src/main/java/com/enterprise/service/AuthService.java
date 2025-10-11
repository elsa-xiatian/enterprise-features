package com.enterprise.service;

import com.enterprise.model.dto.LoginRequest;
import com.enterprise.model.dto.LoginResponse;

public interface AuthService {

    /**
     * 用户登录
     * @param loginRequest 登录请求参数
     * @return 登录结果（包含令牌等信息）
     */
    LoginResponse login(LoginRequest loginRequest);
}
