package com.enterprise.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ResourceRoleBindDTO {
    @NotNull(message = "接口ID不能为空")
    private Long resourceId; // 接口资源ID（对应sys_resource表的id）

    @NotNull(message = "角色ID不能为空")
    private Long roleId; // 角色ID（对应sys_role表的id）
}
