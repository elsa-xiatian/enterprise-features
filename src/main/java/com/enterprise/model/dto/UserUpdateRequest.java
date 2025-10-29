package com.enterprise.model.dto;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String username; // 目标用户的用户名（普通用户只能传自己的）
    private String phone; // 要修改的手机号
}
