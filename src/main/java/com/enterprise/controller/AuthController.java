package com.enterprise.controller;

import com.enterprise.common.Result;
import com.enterprise.model.dto.LoginRequest;
import com.enterprise.model.dto.LoginResponse;
import com.enterprise.model.dto.MfaVerifyRequest;
import com.enterprise.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "认证接口", description = "处理用户登录等认证相关操作")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "使用用户名和密码进行登录，成功后返回JWT令牌")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authService.login(loginRequest);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/verify-mfa")
    public Result<LoginResponse> verifyMfa(@RequestBody MfaVerifyRequest request){
        return Result.success(authService.verifyMfa(request));
    }

}
