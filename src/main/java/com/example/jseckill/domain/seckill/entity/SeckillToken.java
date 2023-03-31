package com.example.jseckill.domain.seckill.entity;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author jianchengwang
 * @date 2023/3/31
 */
@Component
@RequiredArgsConstructor
public class SeckillToken {
    private Long activityId;
    private Long goodsId;
    private Long userId;

    public SeckillToken(Long activityId, Long goodsId, Long userId) {
        this.activityId = activityId;
        this.goodsId = goodsId;
        this.userId = userId;
    }
}
