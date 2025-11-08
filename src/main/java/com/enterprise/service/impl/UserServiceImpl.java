package com.enterprise.service.impl;

import com.enterprise.model.dto.UserInfoResponse;
import com.enterprise.model.dto.UserUpdateRequest;
import com.enterprise.model.entity.User;
import com.enterprise.repository.UserRepository;
import com.enterprise.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    // 新增：判断当前登录用户是否是目标用户（资源所有者）
    public boolean isOwner(String targetUsername, Authentication authentication) {
        String currentUsername = authentication.getName();
        return currentUsername.equals(targetUsername);
    }

    /**
     * 查询用户信息（资源权限控制核心逻辑）
     */
    @Override
    public UserInfoResponse getUserInfoByUsername(String targetUsername) {

        // 1. 获取当前登录用户信息（从SecurityContext中）
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName(); // 当前登录用户的username
        Collection<? extends GrantedAuthority> currentAuthorities = authentication.getAuthorities(); // 当前用户角色

        // 2. 判断当前用户是否为管理员
        boolean isAdmin = currentAuthorities.stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        // 3. 资源权限控制：普通用户只能查询自己的信息
        String queryUsername;
        if (isAdmin) {
            // 管理员：如果传了targetUsername，查指定用户；否则查自己
            queryUsername = (targetUsername != null) ? targetUsername : currentUsername;
        } else {
            // 普通用户：只能查自己，忽略传入的targetUsername
            queryUsername = currentUsername;
        }

        // 4. 查询数据库并返回结果
        User user = userRepository.findByUsername(queryUsername)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 转换为响应DTO
        UserInfoResponse response = new UserInfoResponse();
        BeanUtils.copyProperties(user, response);
        return response;
    }

    /**
     * 修改用户信息（资源权限控制核心逻辑）
     */
    @PreAuthorize("hasRole('ADMIN') || @userService.isOwner(#request.username, authentication)")
    @Override
    public void updateUserInfo(UserUpdateRequest request) {


        // 1. 获取当前登录用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        // 2. 资源权限控制：普通用户只能修改自己的信息
        String targetUsername = request.getUsername();
        if (!isAdmin && !targetUsername.equals(currentUsername)) {
            throw new RuntimeException("无权限修改其他用户的信息");
        }

        // 3. 执行修改操作
        User user = userRepository.findByUsername(targetUsername)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setPhone(request.getPhone()); // 仅修改手机号（可扩展其他字段）
        userRepository.save(user);
    }

}
