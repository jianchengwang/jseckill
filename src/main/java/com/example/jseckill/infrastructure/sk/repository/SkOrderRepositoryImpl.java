package com.example.jseckill.infrastructure.sk.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.jseckill.domain.seckill.repository.SkOrderRepository;
import com.example.jseckill.infrastructure.common.enums.OrderStatusEnum;
import com.example.jseckill.infrastructure.common.enums.PayMethodEnum;
import com.example.jseckill.infrastructure.framework.exception.ClientException;
import com.example.jseckill.infrastructure.framework.exception.FrameworkErrorCode;
import com.example.jseckill.infrastructure.sk.db.dao.SkOrderDao;
import com.example.jseckill.infrastructure.sk.db.po.SkOrder;
import com.example.jseckill.interfaces.client.dto.PayNotifyDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

/**
 * @author jianchengwang
 * @date 2023/4/1
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SkOrderRepositoryImpl extends ServiceImpl<SkOrderDao, SkOrder> implements SkOrderRepository {
    private final SkOrderDao skOrderDao;

    @Override
    public SkOrder findById(Long id) {
        SkOrder skOrder = skOrderDao.selectById(id);
        if(skOrder == null) {
            throw new ClientException("订单不存在", FrameworkErrorCode.RESOURCE_NOT_FOUND);
        }
        return skOrder;
    }

    @Override
    public SkOrder findByOrderNo(String orderNo) {
        LambdaQueryWrapper<SkOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SkOrder::getOrderNo, orderNo);
        SkOrder skOrder = baseMapper.selectOne(queryWrapper);
        if(skOrder == null) {
            throw new ClientException("订单不存在", FrameworkErrorCode.RESOURCE_NOT_FOUND);
        }
        return skOrder;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void createOrder(SkOrder skOrder) {
        skOrderDao.insert(skOrder);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void updateOrderStatus(Long id, OrderStatusEnum orderStatus) {
        SkOrder skOrder = findById(id);
        skOrderDao.updateOrderStatus(id, skOrder.getOrderStatus(), orderStatus);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void confirmPayInfo(Long id, PayMethodEnum payMethod, BigInteger payMoney) {
        SkOrder skOrder = findById(id);
        if(skOrder.getOrderStatus() != OrderStatusEnum.NEW) {
            throw new ClientException("订单状态不正确", FrameworkErrorCode.RESOURCE_NOT_FOUND);
        }
        SkOrder updateObj = new SkOrder();
        updateObj.setId(id);
        updateObj.setPayMethod(payMethod);
        updateObj.setPayMoney(payMoney);
        skOrderDao.updateById(updateObj);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void payNotifySuccess(PayNotifyDTO payNotify) {
        String orderNo = payNotify.getOutTradeNo();
        SkOrder skOrder = findByOrderNo(orderNo);
        if(skOrder.getOrderStatus() != OrderStatusEnum.NEW) {
            throw new ClientException("订单状态不正确", FrameworkErrorCode.RESOURCE_NOT_FOUND);
        }
        SkOrder updateObj = new SkOrder();
        updateObj.setId(skOrder.getId());
        updateObj.setPayTime(payNotify.getPayTime());
        updateObj.setPayTransactionId(payNotify.getPayTransactionId());
        updateObj.setOrderStatus(OrderStatusEnum.PAY_SUCCESS);
        skOrderDao.updateById(updateObj);
    }
}
