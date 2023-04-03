package com.example.jseckill.domain.seckill.entity;

import com.example.jseckill.infrastructure.framework.exception.ClientException;
import com.example.jseckill.infrastructure.framework.exception.FrameworkErrorCode;
import com.example.jseckill.infrastructure.sk.db.po.SkGoods;

import java.time.LocalDateTime;

/**
 * @author jianchengwang
 * @date 2023/4/1
 */
public class SkGoodsDomain {
    private Long skGoodsId;
    private SkGoods skGoods;

    public SkGoodsDomain(SkGoods skGoods) {
        this.skGoods = skGoods;
        this.skGoodsId = skGoods.getId();
    }

    public void validate() {
        LocalDateTime startTime = skGoods.getStartTime();
        LocalDateTime endTime = skGoods.getEndTime();
        LocalDateTime nowTime = LocalDateTime.now();
        if (nowTime.isBefore(startTime)) {
            throw new ClientException("秒杀还未开始", FrameworkErrorCode.LEGAL_REQUEST);
        }
        if (nowTime.isAfter(endTime)) {
            throw new ClientException("秒杀已经结束", FrameworkErrorCode.LEGAL_REQUEST);
        }
    }
}
