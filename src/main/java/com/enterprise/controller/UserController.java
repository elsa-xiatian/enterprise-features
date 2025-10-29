package com.enterprise.controller;


import com.enterprise.common.Result;
import com.enterprise.model.dto.UserInfoResponse;
import com.enterprise.model.dto.UserUpdateRequest;
import com.enterprise.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.GrantedAuthority;


import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/info")
    public Result<?> getUserInfo(Authentication authentication) {
        // 从Authentication中获取当前登录用户信息（包含角色）
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return Result.success(Map.of(
                "username", userDetails.getUsername(),
                "roles", userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()) // 返回用户角色列表
        ));
    }

    /**
     * 查看用户信息（支持管理员查看所有用户，普通用户只能查看自己）
     * @param username 可选参数：管理员可传其他用户名，普通用户传了也无效
     */
    @GetMapping("/info-by-username")
    public Result<UserInfoResponse> getUserInfo(@RequestParam(required = false) String username) {
        UserInfoResponse userInfo = userService.getUserInfoByUsername(username);
        return Result.success(userInfo);
    }

    /**
     * 修改用户信息（普通用户只能修改自己，管理员可修改所有）
     */
    @PutMapping("/info")
    public Result<?> updateUserInfo(@RequestBody UserUpdateRequest request) {
        userService.updateUserInfo(request);
        return Result.success("修改成功");
    }
}
