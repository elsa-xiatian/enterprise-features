package com.enterprise.model.entity;

import lombok.Data;
import jakarta.persistence.*;

@Data
@Table(name = "sys_role")
@Entity
public class Role {
    @Id // 主键标识
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增主键
    private Long id;
    private String roleName; // 对应数据库role_name（如ROLE_ADMIN）
    private String roleDesc;
}
