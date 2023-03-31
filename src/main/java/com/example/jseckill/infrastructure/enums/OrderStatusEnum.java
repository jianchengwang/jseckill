package com.example.jseckill.infrastructure.enums;

import com.example.jseckill.infrastructure.common.pojo.IBaseEnum;

/**
 * @author jianchengwang
 * @date 2023/3/31
 */
public enum OrderStatusEnum implements IBaseEnum<Integer> {
    NEW(0, "新建订单"),
    WAIT_PAY(1, "下单成功，等待支付"),
    CANCEL(2, "取消订单"),
    TIMEOUT(3, "订单超时"),
    PAY_SUCCESS(4, "支付完成"),
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
