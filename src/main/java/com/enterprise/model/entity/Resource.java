package com.enterprise.model.entity;

import jakarta.persistence.*; // Spring Boot 3+ 用 jakarta.persistence，Spring Boot 2.x 用 javax.persistence
import lombok.Data;

@Data
@Table(name = "sys_resource")
@Entity
public class Resource {
    @Id // 主键标识
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增主键
    private Long id;
    private String resourceUrl; // 接口路径
    private String httpMethod; // 请求方法
    private String resourceName; // 接口名称
}
