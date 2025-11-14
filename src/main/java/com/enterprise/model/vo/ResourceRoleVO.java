package com.enterprise.model.vo;

import lombok.Data;

import java.util.List;


@Data
public class ResourceRoleVO {
    private Long resourceId; // 接口ID
    private String resourceUrl; // 接口路径
    private String httpMethod; // 请求方法
    private String resourceName; // 接口名称
    private List<String> roleNames; // 绑定的角色名列表（如["ROLE_ADMIN", "ROLE_USER"]）

}
