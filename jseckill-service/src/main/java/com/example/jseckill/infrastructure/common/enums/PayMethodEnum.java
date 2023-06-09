package com.example.jseckill.infrastructure.common.enums;

import org.example.framework.pojo.IBaseEnum;

/**
 * @author jianchengwang
 * @date 2023/3/31
 */
public enum PayMethodEnum implements IBaseEnum<Integer> {
    FREE(0, "免费"),
    WECHAT(1, "微信"),
    ALIPAY(2, "支付宝"),
    ;

    private final Integer value;
    private final String description;

    PayMethodEnum(Integer value, String description) {
        this.value = value;
        this.description = description;
    }

    @Override
    public Object getDescription() {
        return description;
    }

    @Override
    public Integer getValue() {
        return value;
    }
}
