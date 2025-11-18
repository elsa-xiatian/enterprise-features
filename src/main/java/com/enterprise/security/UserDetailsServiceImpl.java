package com.enterprise.security;


import com.enterprise.model.entity.User;
import com.enterprise.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. 查询用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));

        log.info("查询到用户：{}", user.getUsername());

        // 2. 检查用户状态
        if (user.getStatus() == 0) {
            throw new RuntimeException("用户已被禁用");
        }

        GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole());

        // 4. 构建UserDetails返回（包含角色信息）
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword()) // 数据库中加密后的密码
                .authorities(authority) // 传入用户角色（权限）
                .disabled(user.getDisabled())
                .accountExpired(false)
                .accountLocked(user.getLocked())
                .credentialsExpired(false)
                .build();


        // 3. 转换为Spring Security需要的UserDetails对象
        /*org.springframework.security.core.userdetails.User user1 = new org.springframework.security.core.userdetails.User(user.getUsername(),
                user.getPassword(),user.getStatus() == 1,true,true,true,
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
        return user1;*/
    }

}
