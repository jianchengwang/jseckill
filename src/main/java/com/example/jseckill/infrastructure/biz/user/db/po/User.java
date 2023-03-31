package com.example.jseckill.infrastructure.biz.user.db.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.jseckill.infrastructure.common.pojo.PO;
import com.example.jseckill.infrastructure.enums.UserScopeEnum;
import com.example.jseckill.infrastructure.enums.UserStatusEnum;
import lombok.Data;

/**
 * @author jianchengwang
 * @date 2023/3/30
 */
@Data
@TableName("t_user")
public class User implements PO {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String username;
    private String nickname;
    private String mobile;
    private String password;
    private String passwordSalt;
    private UserScopeEnum userScope;
    private UserStatusEnum userStatus;

    // getters and setters
}
