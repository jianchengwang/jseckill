package com.example.jseckill.infrastructure.biz.seckill.db.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.jseckill.infrastructure.biz.seckill.db.po.SeckillGoods;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author jianchengwang
 * @date 2023/3/30
 */
@Mapper
public interface SeckillGoodsDao extends BaseMapper<SeckillGoods> {
}