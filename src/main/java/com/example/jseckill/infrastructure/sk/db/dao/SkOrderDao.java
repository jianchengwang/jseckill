package com.example.jseckill.infrastructure.sk.db.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.jseckill.infrastructure.common.enums.OrderStatusEnum;
import com.example.jseckill.infrastructure.sk.db.po.SkOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author jianchengwang
 * @date 2023/3/30
 */
@Mapper
public interface SkOrderDao extends BaseMapper<SkOrder> {

    int updateOrderStatus(Long id, OrderStatusEnum oldOrderStatus, OrderStatusEnum orderStatus);
}
