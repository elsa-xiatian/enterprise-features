package com.enterprise.service.impl;

import com.enterprise.enums.LoginStatusEnum;
import com.enterprise.model.dto.LoginRequest;
import com.enterprise.model.dto.LoginResponse;
import com.enterprise.model.dto.MfaVerifyRequest;
import com.enterprise.model.entity.User;
import com.enterprise.repository.UserRepository;
import com.enterprise.security.JwtTokenProvider;
import com.enterprise.security.UserDetailsServiceImpl;
import com.enterprise.service.AuthService;
import com.enterprise.service.RefreshTokenService;
import com.enterprise.service.SmsCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    private final SmsCodeService smsCodeService;
    //新增：注入RefreshtokenService
    private final RefreshTokenService refreshTokenService;
    // 新增：注入RedisTemplate
    private final RedisTemplate<String, String> redisTemplate;

    // MFA临时令牌的Redis配置
    private static final String MFA_TOKEN_KEY_PREFIX = "mfa:token:";
    // 临时令牌有效期：10分钟（足够用户输入验证码）
    private static final long MFA_TOKEN_EXPIRE_MINUTES = 10;

    private final UserDetailsServiceImpl userDetailsService;


    @Override
    public LoginResponse login(LoginRequest loginRequest) {


        // 1. 进行身份认证
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        //优先检查是否被禁用

        if(user.getDisabled()){
            throw new RuntimeException("账号被禁用，无法使用");
        }

        if(user.getLocked()){
            throw new RuntimeException("账户被锁定，无法登录");
        }


        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if(!encoder.matches(loginRequest.getPassword(), user.getPassword())){
            user.setLoginFailedCount(user.getLoginFailedCount()+1);
            if(user.getLoginFailedCount() >= 5){
                user.setLocked(true);
                user.setLockedTime(LocalDateTime.now());
            }
            userRepository.save(user);
            int trycount = 5-user.getLoginFailedCount();
            throw new RuntimeException("密码错误，无法登录，剩余"+trycount+"次访问机会");
        }

        if (user.getMfaEnabled() != null && user.getMfaEnabled() == 1) {
            // 3. 生成MFA临时令牌（UUID）
            String mfaToken = UUID.randomUUID().toString();
            // 构建Redis的key（格式：mfa:token:f47ac10b-58cc-4372-a567-0e02b2c3d479）
            String redisKey = MFA_TOKEN_KEY_PREFIX + mfaToken;

            // 存储临时令牌与用户名的关联到Redis（设置过期时间）
            redisTemplate.opsForValue().set(redisKey, user.getUsername(), MFA_TOKEN_EXPIRE_MINUTES, TimeUnit.MINUTES);

            // 发送短信验证码（原有逻辑不变）
            smsCodeService.sendCode(user.getPhone());

            // 返回需要二次验证的响应（原有逻辑不变）
            return LoginResponse.builder()
                    .loginStatus(LoginStatusEnum.REQUIRE_MFA.getCode())
                    .mfaToken(mfaToken)
                    .username(user.getUsername())
                    .build();
        } else {
            // 未开启MFA的逻辑（原有不变）
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtTokenProvider.generateToken(authentication);
            String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

            refreshTokenService.saveRefreshToken(refreshToken,user.getUsername());

            return LoginResponse.builder()
                    .loginStatus(LoginStatusEnum.SUCCESS.getCode())
                    .token(token)
                    .refreshToken(refreshToken)
                    .expiresIn(jwtTokenProvider.getExpirationInSeconds())
                    .username(user.getUsername())
                    .build();
        }
    }

    // 二次验证接口（修改临时令牌的获取逻辑）
    @Override
    public LoginResponse verifyMfa(MfaVerifyRequest request) {

        // 1. 从Redis获取临时令牌对应的用户名
        String redisKey = MFA_TOKEN_KEY_PREFIX + request.getMfaToken();
        String username = redisTemplate.opsForValue().get(redisKey);

        // 临时令牌无效（过期/不存在）
        if (username == null) {
            throw new RuntimeException("临时令牌无效或已过期，请重新登录");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // 2. 获取用户信息（原有逻辑不变）
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 3. 验证短信验证码（原有逻辑不变，已改用Redis）
        boolean isCodeValid = smsCodeService.validateCode(user.getPhone(), request.getCode());
        if (!isCodeValid) {
            throw new RuntimeException("验证码错误或已过期");
        }

        // 4. 生成JWT（原有逻辑不变）
        Authentication authentication = new UsernamePasswordAuthenticationToken(
               userDetails, null,
               userDetails.getAuthorities());


        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        refreshTokenService.saveRefreshToken(refreshToken,user.getUsername());

        // 5. 验证通过后，删除Redis中的临时令牌
        redisTemplate.delete(redisKey);

        return LoginResponse.builder()
                .loginStatus(LoginStatusEnum.SUCCESS.getCode())
                .token(token)
                .refreshToken(refreshToken)
                .expiresIn(jwtTokenProvider.getExpirationInSeconds())
                .username(user.getUsername())
                .build();
    }
    }

