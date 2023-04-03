package com.example.jseckill.domain.seckill.service.task;

import com.example.jseckill.domain.seckill.entity.SkPayDomain;
import com.example.jseckill.domain.seckill.repository.SkOrderRepository;
import com.example.jseckill.domain.seckill.repository.SkRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.concurrent.Executor;

/**
 * @author jianchengwang
 * @date 2023/4/2
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class SkOrderPaySuccessNotifyRunner implements CommandLineRunner {

    private final SkOrderRepository orderRepository;
    private final SkRedisRepository redisRepository;
    private final Executor executor;
    private final PlatformTransactionManager transactionManager;

    @Override
    public void run(String... args) {
        while (true){
            try {
                SkPayDomain skPayDomain = redisRepository.popPaySuccessNotify();
                //do some
                if(skPayDomain == null) {
                    continue;
                }
                executor.execute(new SkOrderPayNotifySuccessRunnable(skPayDomain));
                //do some
            } catch (Exception e) {
                log.error(e.getMessage(),e);
            }
        }
    }

    public class SkOrderPayNotifySuccessRunnable implements Runnable {
        private SkPayDomain skPayDomain;
        public SkOrderPayNotifySuccessRunnable(SkPayDomain skPayDomain){
            this.skPayDomain = skPayDomain;
        }
        @Override
        public void run() {
            TransactionStatus tx = null;
            try {
                tx = transactionManager.getTransaction(new DefaultTransactionDefinition());
                orderRepository.payNotifySuccess(skPayDomain.getPayNotify());
                transactionManager.commit(tx);
            } catch (Exception e) {
                log.error(e.getMessage(),e);
                // 回滚事务
                transactionManager.rollback(tx);
                // 重试三次
                if(skPayDomain.incrTryTimes() < 3) {
                    redisRepository.pushPaySuccessNotify(skPayDomain);
                }
            }
        }
    }
}
