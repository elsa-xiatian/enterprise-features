package com.enterprise.model.dto;

import lombok.Data;

@Data
public class UserInfoResponse {
        private Long id;
        private String username;
        private String phone;
        private String role; // 角色信息
        private Integer status;

}
