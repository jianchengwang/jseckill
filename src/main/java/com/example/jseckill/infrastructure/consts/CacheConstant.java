package com.example.jseckill.infrastructure.consts;

/**
 * @author jianchengwang
 * @date 2023/3/31
 */
public interface CacheConstant {
    long DEFAULT_EXPIRE_TIME = 7 * 24 * 60 * 60;

    String USER_CACHE_NAME = "uesr_cache";
    String USER_CACHE_PREFIX = "user_";

    String SECKILL_ACTIVITY_CACHE_NAME = "seckill_activity_cache";
    String SECKILL_ACTIVITY_CACHE_PREFIX = "seckill_activity_";
    String SECKILL_GOODS_CACHE_NAME = "seckill_goods_cache";
    String SECKILL_GOODS_CACHE_PREFIX = "seckill_goods_";
}
