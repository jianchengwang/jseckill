package com.example.jseckill.domain.seckill.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.jseckill.infrastructure.common.enums.OrderStatusEnum;
import com.example.jseckill.infrastructure.common.enums.PayMethodEnum;
import com.example.jseckill.infrastructure.sk.db.po.SkOrder;
import com.example.jseckill.interfaces.client.dto.PayNotifyDTO;

import java.math.BigInteger;

public interface SkOrderRepository extends IService<SkOrder> {
    SkOrder findById(Long id);
    SkOrder findByOrderNo(String orderNo);
    void createOrder(SkOrder skOrder);
    int updateOrderStatus(Long id, OrderStatusEnum orderStatus);

    int confirmPayInfo(Long id, PayMethodEnum payMethod, BigInteger payMoney);

    void payNotifySuccess(PayNotifyDTO payNotify);
}
