package com.example.jseckill.domain.seckill.service;

import com.example.jseckill.domain.seckill.entity.SkOrderDomain;
import com.example.jseckill.domain.seckill.repository.SkOrderRepository;
import com.example.jseckill.domain.seckill.repository.SkRedisRepository;
import com.example.jseckill.infrastructure.common.enums.OrderStatusEnum;
import com.example.jseckill.infrastructure.sk.db.po.SkOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author jianchengwang
 * @date 2023/4/1
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SkOrderService {

    private final SkOrderRepository orderRepository;
    private final SkRedisRepository redisRepository;

    @Transactional(rollbackFor = Throwable.class)
    public void cancelOrder(Long orderId) {
        SkOrder skOrder = orderRepository.findById(orderId);
        if(skOrder.getOrderStatus() != OrderStatusEnum.NEW || skOrder.getOrderStatus() != OrderStatusEnum.PAY_CONFIRM) {
            return;
        }
        // 如果已经进入支付回调，不再取消订单
        if(redisRepository.containsOrderPaySuccessSet(skOrder.getOrderNo())) {
            orderRepository.updateOrderStatus(orderId, OrderStatusEnum.PAY_SUCCESS_WAIT);
            return;
        }
        SkOrderDomain skOrderDomain = new SkOrderDomain(skOrder);
        orderRepository.updateOrderStatus(orderId, OrderStatusEnum.CANCEL);
        // 库存回滚
        skOrderDomain.addStock(redisRepository);
        // 取消订单
        skOrderDomain.createOrderCancel(redisRepository);
    }
}
