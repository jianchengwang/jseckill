package com.example.jseckill.infrastructure.enums;

import com.example.jseckill.infrastructure.common.pojo.IBaseEnum;

/**
 * @author jianchengwang
 * @date 2023/3/31
 */
public enum UserScopeEnum implements IBaseEnum<Integer> {
    /**
     * 普通用户
     */
    NORMAL(1, "普通用户"),
    /**
     * 管理员
     */
    ADMIN(2, "管理员");

    private Integer value;
    private String description;

    UserScopeEnum(Integer value, String description) {
        this.value = value;
        this.description = description;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }

    @Override
    public Object getDescription() {
        return this.description;
    }
}
