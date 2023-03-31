package com.example.jseckill.interfaces.client.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * @author jianchengwang
 * @date 2023/03/31
 */
@Schema(description = "认证模块-用户名密码登录参数")
@Data
public class LoginByUsernameParam {
    @NotEmpty
    @Schema(description = "手机号")
    private String username;

    @NotEmpty
    @Schema(description = "密码")
    private String password;
}
