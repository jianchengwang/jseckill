package com.example.jseckill.infrastructure.enums;

import com.example.jseckill.infrastructure.common.pojo.IBaseEnum;

/**
 * @author jianchengwang
 * @date 2023/3/31
 */
public enum PayMethodEnum implements IBaseEnum<Integer> {
    FREE(0, "免费"),
    ALIPAY(1, "支付宝"),
    WECHAT(2, "微信"),
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
