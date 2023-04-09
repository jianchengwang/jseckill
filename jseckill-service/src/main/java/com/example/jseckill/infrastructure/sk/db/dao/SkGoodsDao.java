package com.example.jseckill.infrastructure.sk.db.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.jseckill.infrastructure.sk.db.po.SkGoods;
import com.example.jseckill.interfaces.operate.query.SkGoodsQuery;
import com.example.jseckill.interfaces.operate.vo.SkGoodsVO;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigInteger;

/**
 * @author jianchengwang
 * @date 2023/3/30
 */
@Mapper
public interface SkGoodsDao extends BaseMapper<SkGoods> {
    IPage<SkGoodsVO> page(IPage<SkGoods> page, SkGoodsQuery param, LambdaQueryWrapper<SkGoods> ew);
    int subStock(Long id, BigInteger deltaStock);

    int loadCacheStock(Long id, BigInteger cacheStock);
}
