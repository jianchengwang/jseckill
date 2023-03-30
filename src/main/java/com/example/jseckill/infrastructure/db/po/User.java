package com.example.jseckill.infrastructure.db.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author jianchengwang
 * @date 2023/3/30
 */
@Data
@TableName("t_user")
public class User {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String nickname;
    private String mobile;
    private String password;
    private String passwordSalt;

    // getters and setters
}
