package com.example.jseckill.infrastructure.config.permission.user;

import com.example.jseckill.infrastructure.enums.UserScopeEnum;
import com.example.jseckill.infrastructure.enums.UserStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author jianchengwang
 * @date 2023/3/31
 */
@Data
public class TokenUser implements Serializable {
    @Schema(description = "主键")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "手机号")
    private String mobile;

    @Schema(description = "用户归属")
    private UserScopeEnum userScope;

    @Schema(description = "用户状态")
    private UserStatusEnum userStatus;
}
