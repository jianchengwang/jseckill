package com.example.jseckill.application;

import com.example.jseckill.domain.seckill.entity.SkPayDomain;
import com.example.jseckill.domain.seckill.repository.SkRedisRepository;
import com.example.jseckill.interfaces.client.dto.PayNotifyDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author jianchengwang
 * @date 2023/4/2
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SkPayApplication {

    private final SkRedisRepository redisRepository;

    public boolean payNotify(PayNotifyDTO payNotify) {
        try {
            // 校验签名
            SkPayDomain skPayDomain = new SkPayDomain(payNotify);
            skPayDomain.verifySign();
            // 推入支付回调队列
            redisRepository.pushPayNotify(List.of(payNotify));
            if(payNotify.isSuccess()) {
                // 推入订单支付成功标识
                redisRepository.pushOrderPaySuccessSet(payNotify.getOutTradeNo());
                // 推入支付回调成功队列
                redisRepository.pushPaySuccessNotify(skPayDomain);
            }
        } catch (Exception e) {
            log.error("payNotify error", e);
            return false;
        }
        return true;
    }
}
