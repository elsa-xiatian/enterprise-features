package com.enterprise.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LockUserDTO {
    //传递的是用户id信息，在多个方法中复用
    @NotNull(message = "用户id不能为空")
    private long UserId;
}
