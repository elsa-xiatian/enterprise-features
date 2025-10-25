package com.enterprise.controller;

import com.enterprise.common.Result;
import com.enterprise.model.dto.LoginRequest;
import com.enterprise.model.dto.LoginResponse;
import com.enterprise.model.dto.MfaVerifyRequest;
import com.enterprise.model.dto.RefreshTokenRequest;
import com.enterprise.security.JwtTokenProvider;
import com.enterprise.service.AuthService;
import com.enterprise.service.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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
    private final RefreshTokenService refreshTokenService;

    private final UserDetailsService userDetailsService;

    private final JwtTokenProvider jwtTokenProvider;

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

    @PostMapping("/refresh-token")
    public Result<LoginResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        // 1. 验证Refresh Token有效性
        if (!refreshTokenService.validateRefreshToken(refreshToken)) {
            throw new RuntimeException("Refresh Token无效或已过期");
        }

        // 2. 从Refresh Token中提取用户名
        String username = jwtTokenProvider.getUsernameFromRefreshToken(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // 3. 生成新的Authentication对象
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );

        // 4. 生成新的Access Token和Refresh Token
        String newAccessToken = jwtTokenProvider.generateToken(authentication);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        // 5. 删除旧的Refresh Token，存储新的Refresh Token（防止重复使用）
        refreshTokenService.deleteRefreshToken(refreshToken);
        refreshTokenService.saveRefreshToken(newRefreshToken, username);

        // 6. 返回新Token
        return Result.success(LoginResponse.builder()
                .token(newAccessToken)
                .refreshToken(newRefreshToken)
                .expiresIn(jwtTokenProvider.getExpirationInSeconds())
                .refreshExpiresIn(jwtTokenProvider.getRefreshExpirationInSeconds())
                .username(username)
                .build());
    }
}


