package com.example.jseckill.domain.seckill.service.task;

import com.example.jseckill.domain.seckill.repository.SkRedisRepository;
import com.example.jseckill.domain.seckill.service.SkOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingQueue;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;

/**
 * @author jianchengwang
 * @date 2023/4/1
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class SkOrderExpireRunner implements CommandLineRunner {

    private final SkOrderService orderService;
    private final SkRedisRepository redisRepository;
    private final Executor executor;

    @Override
    public void run(String... args) {
        while (true){
            try {
                RBlockingQueue<Long> waitPayOrderQueue = redisRepository.getWaitPayOrderQueue();
                Long orderId = waitPayOrderQueue.take();
                if(orderId <= 0){
                    continue;
                }
                executor.execute(new OrderExpireRunnable(orderId));
            } catch (Exception e) {
                log.error(e.getMessage(),e);
            }
        }
    }

    public class OrderExpireRunnable implements Runnable {
        private final Long orderId;
        public OrderExpireRunnable(Long orderId) {
            this.orderId = orderId;
        }
        @Override
        public void run() {
            try {
                orderService.cancelOrder(orderId);
            } catch (Exception e) {
                log.error(e.getMessage(),e);
                // 重新放入队列
                redisRepository.pushWaitPayOrder(orderId);
            }
        }
    }
}
