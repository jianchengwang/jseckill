package com.example.jseckill.infrastructure.consts;

/**
 * @author jianchengwang
 * @date 2023/3/31
 */
public interface SeckillConstant {

    String SECKILL_TOKEN_KEY = "seckill:token:%s:%s:%s"; // activityId:goodsId:userId 秒杀令牌

    String SECKILL_GOODS_COUNT = "seckill:goods:count:%s:%s"; // activityId:goodsId 秒杀商品数量

}
