package com.example.jseckill.infrastructure.common.enums;

import com.example.jseckill.infrastructure.framework.pojo.IBaseEnum;

/**
 * @author jianchengwang
 * @date 2023/3/31
 */
public enum OrderStatusEnum implements IBaseEnum<Integer> {
    NEW(0, "新建订单"),
    PAY_SUCCESS(1, "支付完成"),
    PAY_SUCCESS_WAIT(2, "支付完成，待处理"),
    CANCEL(3, "取消订单"),
    ;

    private final Integer value;
    private final String description;

    OrderStatusEnum(Integer value, String description) {
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
