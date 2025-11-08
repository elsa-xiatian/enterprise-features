package com.enterprise.controller;

import com.enterprise.common.Result;
import com.enterprise.model.entity.User;
import com.enterprise.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserRepository userRepository;

    // 管理员接口：仅ROLE_ADMIN可访问
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user-list")
    public Result<?> getUserList() {
        // 管理员权限：查询所有用户（普通用户无此权限）
        List<User> userList = userRepository.findAll();
        return Result.success(userList.stream()
                .map(user -> Map.of(
                        "id", user.getId(),
                        "username", user.getUsername(),
                        "role", user.getRole()
                ))
                .collect(Collectors.toList()));
    }
}
