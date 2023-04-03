package com.example.jseckill.infrastructure.sk.db.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.jseckill.infrastructure.sk.db.po.SkGoods;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigInteger;

/**
 * @author jianchengwang
 * @date 2023/3/30
 */
@Mapper
public interface SkGoodsDao extends BaseMapper<SkGoods> {
    int subStock(Long id, BigInteger deltaStock);
}
