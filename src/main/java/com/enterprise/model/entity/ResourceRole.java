package com.enterprise.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Table(name = "sys_resource_role")
@Entity
public class ResourceRole {
    @Id // 主键标识
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增主键
    private Long id;
    private Long resourceId; // 接口ID
    private Long roleId; // 角色ID
}
