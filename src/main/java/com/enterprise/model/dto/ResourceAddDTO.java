package com.enterprise.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResourceAddDTO {

    @NotBlank(message = "接口路径不能为空")
    private String resourceUrl; // 接口路径（如 /api/study/record）

    @NotBlank(message = "请求方法不能为空")
    private String httpMethod; // 请求方法

    private String resourceName; // 接口名称
}
