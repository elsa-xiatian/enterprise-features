package com.enterprise.controller;


import com.enterprise.common.Result;
import com.enterprise.model.dto.LockUserDTO;
import com.enterprise.model.dto.UserInfoResponse;
import com.enterprise.model.dto.UserUpdateRequest;
import com.enterprise.model.entity.User;
import com.enterprise.repository.UserRepository;
import com.enterprise.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.GrantedAuthority;


import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
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


    @PostMapping("/lock")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result<String> LockUser(@RequestBody @Valid LockUserDTO lockUserDTO){
       User user = userRepository.findById(lockUserDTO.getUserId())
               .orElseThrow(() -> new RuntimeException("用户不存在"));

       user.setLocked(true);
       user.setLockedTime(LocalDateTime.now());
       userRepository.save(user);
       return Result.success("用户锁定成功");
    }

    @PostMapping("/unlock")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result<String> UnLockUser(@RequestBody @Valid LockUserDTO lockUserDTO){
        User user = userRepository.findById(lockUserDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        user.setLocked(false);
        user.setLoginFailedCount(0);
        user.setLockedTime(null);
        userRepository.save(user);
        return Result.success("用户解锁成功");
    }
    @PostMapping("/disable")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result<String> DisableUser(@RequestBody @Valid LockUserDTO disableDTO){
        User user = userRepository.findById(disableDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        user.setDisabled(true);
        userRepository.save(user);
        return Result.success("用户禁用成功");
    }

    @PostMapping("/disable")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result<String> UndisableUser(@RequestBody @Valid LockUserDTO undisableDTO){
        User user = userRepository.findById(undisableDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setDisabled(false);
        userRepository.save(user);
        return Result.success("用户解禁成功");
    }


}
