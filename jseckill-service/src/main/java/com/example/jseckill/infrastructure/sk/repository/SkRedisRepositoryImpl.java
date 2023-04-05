package com.example.jseckill.infrastructure.sk.repository;

import cn.hutool.core.lang.UUID;
import com.example.jseckill.domain.seckill.entity.SkOrderDomain;
import com.example.jseckill.domain.seckill.entity.SkPayDomain;
import com.example.jseckill.domain.seckill.repository.SkRedisRepository;
import com.example.jseckill.infrastructure.common.consts.SkConstant;
import com.example.jseckill.infrastructure.framework.exception.ClientException;
import com.example.jseckill.infrastructure.framework.exception.FrameworkErrorCode;
import com.example.jseckill.infrastructure.sk.db.po.SkGoods;
import com.example.jseckill.interfaces.client.dto.PayNotifyDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author jianchengwang
 * @date 2023/3/31
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SkRedisRepositoryImpl implements SkRedisRepository {

    private final RedissonClient redissonClient;
    private final long TimeoutDays = 7;

    @Override
    public boolean hasSkToken(Long skGoodsId, Long userId) {
        String skTokenKey = String.format(SkConstant.SK_TOKEN_KEY, skGoodsId, userId);
        RBucket<String> bucket = redissonClient.getBucket(skTokenKey);
        return bucket.isExists();
    }

    @Override
    public String getOrSetSkToken(Long skGoodsId, Long userId) {
        String skTokenKey = String.format(SkConstant.SK_TOKEN_KEY, skGoodsId, userId);
        RBucket<String> bucket = redissonClient.getBucket(skTokenKey);
        if(!bucket.isExists()) {
            String skToken = UUID.fastUUID().toString(true);
            bucket.set(skToken, 5, TimeUnit.MINUTES);
            return skToken;
        }
        return bucket.get();
    }

    @Override
    public void preheatSkGoods(SkGoods skGoods) {
        Long skGoodsId = skGoods.getId();
        BigInteger stockNum = skGoods.getStockNum();
        RTransaction transaction = redissonClient.createTransaction(TransactionOptions.defaults());
        String skGoodsStockKey = String.format(SkConstant.SK_GOODS_STOCK, skGoodsId);
        RBucket<Long> skGoodsStock = transaction.getBucket(skGoodsStockKey);
        skGoodsStock.set(stockNum.longValue(), TimeoutDays, TimeUnit.DAYS);
        transaction.commit();
    }

    @Override
    public BigInteger getSkGoodsStockNum(Long skGoodsId) {
        String skGoodsStockKey = String.format(SkConstant.SK_GOODS_STOCK, skGoodsId);
        RBucket<Long> skGoodsStock = redissonClient.getBucket(skGoodsStockKey);
        if(skGoodsStock.isExists()) {
            return BigInteger.valueOf(skGoodsStock.get());
        }
        return BigInteger.ZERO;
    }

    @Override
    public long addSkGoodsStock(Long skGoodsId, long addStock) {
        String skGoodsStockKey = String.format(SkConstant.SK_GOODS_STOCK, skGoodsId);
        RTransaction transaction = redissonClient.createTransaction(TransactionOptions.defaults());
        RBucket<Long> bucket = redissonClient.getBucket(skGoodsStockKey);
        if(!bucket.isExists()) {
            throw new ClientException("redis skGoodsStock not exists", FrameworkErrorCode.SERVER_ERROR, skGoodsId, addStock);
        }
        long stock = bucket.get() + addStock;
        bucket.set(stock, TimeoutDays, TimeUnit.DAYS);
        transaction.commit();
        return stock;
    }

    @Override
    public long subSkGoodsStock(Long skGoodsId, long subStock) {
        String skGoodsStockKey = String.format(SkConstant.SK_GOODS_STOCK, skGoodsId);
        RTransaction transaction = redissonClient.createTransaction(TransactionOptions.defaults());
        RBucket<Long> bucket = transaction.getBucket(skGoodsStockKey);
        if(!bucket.isExists()) {
            throw new ClientException("redis skGoodsStock not exists", FrameworkErrorCode.SERVER_ERROR, skGoodsId, subStock);
        }
        long stock = bucket.get() - subStock;
        if(stock < 0) {
            stock = -1;
        } else {
            bucket.set(stock, TimeoutDays, TimeUnit.DAYS);
        }
        transaction.commit();
        return stock;
    }

    @Override
    public long incrSkOrderSeq(String orderSeq) {
        String orderSeqKey = String.format(SkConstant.SK_ORDER_SEQ, orderSeq);
        RAtomicLong skOrderSeq = redissonClient.getAtomicLong(orderSeqKey);
        return skOrderSeq.incrementAndGet();
    }

    @Override
    public void pushSkOrder(SkOrderDomain skOrder) {
        RBoundedBlockingQueue<SkOrderDomain> queue = redissonClient.getBoundedBlockingQueue(SkConstant.SK_ORDER_CREATE_QUEUE);
        queue.trySetCapacity(SkConstant.SK_ORDER_QUEUE_CAPACITY);
        try {
            queue.put(skOrder);
        } catch (InterruptedException e) {
            log.error("pushSkOrder error", e);
            throw new ClientException("pushSkOrder error", FrameworkErrorCode.SERVER_ERROR);
        }
    }

    @Override
    public SkOrderDomain popSkOrder() {
        RBoundedBlockingQueue<SkOrderDomain> queue = redissonClient.getBoundedBlockingQueue(SkConstant.SK_ORDER_CREATE_QUEUE);
        queue.trySetCapacity(SkConstant.SK_ORDER_QUEUE_CAPACITY);
        try {
            return queue.take();
        } catch (InterruptedException e) {
            log.error("popSkOrder error", e);
            throw new ClientException("popSkOrder error", FrameworkErrorCode.SERVER_ERROR);
        }
    }

    @Override
    public String checkOrderCreateSuccess(String skToken) {
        String skOrderCreateFlagKey = String.format(SkConstant.SK_ORDER_CREATE_SUCCESS, skToken);
        RBucket<String> bucket = redissonClient.getBucket(skOrderCreateFlagKey);
        if(bucket.isExists()) {
            return bucket.get();
        }
        return "0";
    }

    @Override
    public void pushWaitPayOrder(Long orderId) {
        RBlockingQueue<Long> blockingFairQueue = redissonClient.getBlockingQueue(SkConstant.SK_ORDER_WAITPAY_QUEUE);
        RDelayedQueue<Long> delayedQueue = redissonClient.getDelayedQueue(blockingFairQueue);
        delayedQueue.offer(orderId,5, TimeUnit.MINUTES);
    }

    @Override
    public RBlockingQueue<Long> getWaitPayOrderQueue() {
        return redissonClient.getBlockingQueue(SkConstant.SK_ORDER_WAITPAY_QUEUE);
    }

    @Override
    public void createOrderSuccess(String skToken, Long skGoodsId, long buyNum, Long userId, String orderNo) {
        RTransaction transaction = redissonClient.createTransaction(TransactionOptions.defaults());
        // 待支付数量+1
        String waitPayCountKey = String.format(SkConstant.SK_USER_WAIT_PAY_COUNT, skGoodsId, userId);
        RBucket<Long> waitPayBucket = transaction.getBucket(waitPayCountKey);
        if(waitPayBucket.isExists()) {
            waitPayBucket.set(waitPayBucket.get() + 1);
        } else {
            waitPayBucket.set(1L);
        }
        // 购买成功数量+1
        String userBuyCountKey = String.format(SkConstant.SK_USER_BUY_COUNT, skGoodsId, userId);
        RBucket<Long> userBuyCount = transaction.getBucket(userBuyCountKey);
        if(userBuyCount.isExists()) {
            userBuyCount.set(userBuyCount.get() + buyNum);
        } else {
            userBuyCount.set(buyNum);
        }
        // 创建订单成功标识
        String skOrderCreateSuccessKey = String.format(SkConstant.SK_ORDER_CREATE_SUCCESS, skToken);
        RBucket<String> createSuccessBucket = transaction.getBucket(skOrderCreateSuccessKey);
        createSuccessBucket.set(orderNo);
        // 提交事务
        transaction.commit();
    }

    @Override
    public void createOrderFail(String skToken) {
        String skOrderCreateFlagKey = String.format(SkConstant.SK_ORDER_CREATE_SUCCESS, skToken);
        RBucket<String> bucket = redissonClient.getBucket(skOrderCreateFlagKey);
        bucket.set("-1", 5, TimeUnit.MINUTES);
    }

    @Override
    public void createOrderCancel(Long skGoodsId, Long userId, long buyNum) {
        RTransaction transaction = redissonClient.createTransaction(TransactionOptions.defaults());
        // 待支付数量-1
        String waitPayCountKey = String.format(SkConstant.SK_USER_WAIT_PAY_COUNT, skGoodsId, userId);
        RBucket<Long> waitPayBucket = transaction.getBucket(waitPayCountKey);
        if(waitPayBucket.isExists()) {
            waitPayBucket.set(waitPayBucket.get() - 1);
        }
        // 购买成功数量-1
        String userBuyCountKey = String.format(SkConstant.SK_USER_BUY_COUNT, skGoodsId, userId);
        RBucket<Long> userBuyCount = transaction.getBucket(userBuyCountKey);
        if(userBuyCount.isExists()) {
            userBuyCount.set(userBuyCount.get() - buyNum);
        }
        // 提交事务
        transaction.commit();
    }

    @Override
    public void paySuccess(Long skGoodsId, Long userId) {
        RTransaction transaction = redissonClient.createTransaction(TransactionOptions.defaults());
        // 待支付数量-1
        String waitPayCountKey = String.format(SkConstant.SK_USER_WAIT_PAY_COUNT, skGoodsId, userId);
        RBucket<Long> waitPayBucket = transaction.getBucket(waitPayCountKey);
        if(waitPayBucket.isExists()) {
            waitPayBucket.set(waitPayBucket.get() - 1);
        }
        // 提交事务
        transaction.commit();
    }

    @Override
    public long getUserWaitPayCount(Long skGoodsId, Long userId) {
        String waitPayCountKey = String.format(SkConstant.SK_USER_WAIT_PAY_COUNT, skGoodsId, userId);
        RBucket<Long> bucket = redissonClient.getBucket(waitPayCountKey);
        if(bucket.isExists()) {
            return bucket.get();
        }
        return 0;
    }

    @Override
    public void pushPayNotify(List<PayNotifyDTO> payNotifyList) {
        RBoundedBlockingQueue<PayNotifyDTO> queue = redissonClient.getBoundedBlockingQueue(SkConstant.SK_ORDER_PAY_NOTIFY_QUEUE);
        queue.trySetCapacity(SkConstant.SK_ORDER_QUEUE_CAPACITY);
        try {
            for(PayNotifyDTO payNotify: payNotifyList) {
                queue.put(payNotify);
            }
        } catch (InterruptedException e) {
            log.error("pushPayNotifyLog error", e);
        }
    }

    @Override
    public List<PayNotifyDTO> popPayNotify(int limit) {
        RBoundedBlockingQueue<PayNotifyDTO> queue = redissonClient.getBoundedBlockingQueue(SkConstant.SK_ORDER_PAY_NOTIFY_QUEUE);
        queue.trySetCapacity(SkConstant.SK_ORDER_QUEUE_CAPACITY);
        return queue.poll(limit);
    }

    @Override
    public void pushOrderPaySuccessSet(String orderNo) {
        RSet<String> set = redissonClient.getSet(SkConstant.SK_ORDER_PAY_SUCCESS_SET);
        set.add(orderNo);
    }

    @Override
    public boolean containsOrderPaySuccessSet(String orderNo) {
        RSet<String> set = redissonClient.getSet(SkConstant.SK_ORDER_PAY_SUCCESS_SET);
        return set.contains(orderNo);
    }

    @Override
    public long getUserBuyCount(Long skGoodsId, Long userId) {
        String userBuyCountKey = String.format(SkConstant.SK_USER_BUY_COUNT, skGoodsId, userId);
        RBucket<Long> bucket = redissonClient.getBucket(userBuyCountKey);
        if(bucket.isExists()) {
            return bucket.get();
        }
        return 0;
    }

    @Override
    public void pushPaySuccessNotify(SkPayDomain skPayDomain) {
        RBoundedBlockingQueue<SkPayDomain> queue = redissonClient.getBoundedBlockingQueue(SkConstant.SK_ORDER_PAY_SUCCESS_NOTIFY_QUEUE);
        queue.trySetCapacity(SkConstant.SK_ORDER_QUEUE_CAPACITY);
        try {
            queue.put(skPayDomain);
        } catch (InterruptedException e) {
            log.error("pushPayNotify error", e);
            throw new ClientException("pushPayNotify error", FrameworkErrorCode.SERVER_ERROR);
        }
    }

    @Override
    public SkPayDomain popPaySuccessNotify() {
        RBoundedBlockingQueue<SkPayDomain> queue = redissonClient.getBoundedBlockingQueue(SkConstant.SK_ORDER_PAY_SUCCESS_NOTIFY_QUEUE);
        queue.trySetCapacity(SkConstant.SK_ORDER_QUEUE_CAPACITY);
        try {
            return queue.take();
        } catch (InterruptedException e) {
            log.error("popPayNotify error", e);
            throw new ClientException("popPayNotify error", FrameworkErrorCode.SERVER_ERROR);
        }
    }
}
