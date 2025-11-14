package com.enterprise.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ResourceRoleDeleteDTO {
    @NotNull(message = "接口与角色的关联ID不能为空")
    private Long id;
}
