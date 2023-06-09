package org.example.framework.config.permission.user;

import org.example.framework.pojo.IBaseEnum;

/**
 * @author jianchengwang
 * @date 2023/3/31
 */
public enum UserScopeEnum implements IBaseEnum<Integer> {
    /**
     * 普通用户
     */
    CLIENT(1, "客户端", "ROLE_CLIENT"),
    /**
     * 管理员
     */
    OPERATE(2, "运营端", "ROLE_OPERATE");

    private Integer value;
    private String description;
    private String role;

    UserScopeEnum(Integer value, String description, String role) {
        this.value = value;
        this.description = description;
        this.role = role;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }

    @Override
    public Object getDescription() {
        return this.description;
    }

    public String getRole() {
        return this.role;
    }
}
