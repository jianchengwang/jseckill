package com.example.jseckill.interfaces.auth.dto;

import com.example.jseckill.infrastructure.framework.pojo.DTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * @author jianchengwang
 * @date 2023/03/31
 */
@Schema(description = "客户端-用户模块-用户名密码登录参数")
@Data
public class LoginByUsernameDTO implements DTO {
    @NotEmpty
    @Schema(description = "用户名")
    private String username;

    @NotEmpty
    @Schema(description = "密码")
    private String password;
}
