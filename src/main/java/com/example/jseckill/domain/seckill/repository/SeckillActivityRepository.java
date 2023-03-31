package com.example.jseckill.domain.seckill.repository;

import com.example.jseckill.infrastructure.biz.seckill.db.po.SeckillActivity;

/**
 * @author jianchengwang
 * @date 2023/3/31
 */
public interface SeckillActivityRepository {
    SeckillActivity findById(Long id);
}
