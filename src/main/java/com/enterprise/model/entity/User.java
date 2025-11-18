package com.enterprise.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_user") // 系统用户表
public class User {

    //使用JPA快速搭建有关用户的字段
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username; // 用户名（登录账号）

    @Column(nullable = false)
    private String password; // 加密后的密码

    @Column(unique = true, length = 100)
    private String email; // 邮箱

    private String phone; // 手机号

    private Integer status; // 状态：1-正常，0-禁用

    private Integer mfaEnabled = 0;

    private String role; //新增字段，控制权限

    @CreationTimestamp
    private LocalDateTime createTime; // 创建时间

    @UpdateTimestamp
    private LocalDateTime updateTime; // 更新时间

    private Boolean locked = false; // 0→false，1→true

    private Integer loginFailedCount = 0; // 登录失败次数

    private Boolean disabled = false;//账户是否被禁用

    private LocalDateTime lockedTime; //记录账户锁定时间

}
