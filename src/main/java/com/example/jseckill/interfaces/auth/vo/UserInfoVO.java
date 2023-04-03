package com.example.jseckill.interfaces.auth.vo;

import com.example.jseckill.infrastructure.framework.pojo.VO;
import com.example.jseckill.infrastructure.common.enums.UserStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author jianchengwang
 * @date 2023/3/31
 */
@Data
@Schema(description = "客户端-账号信息-VO")
public class UserInfoVO implements VO {

    @Schema(description = "编号")
    private Long id;

    @Schema(description = "账户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "手机号")
    private String mobile;

    @Schema(description = "用户状态")
    private UserStatusEnum userStatus;
}
