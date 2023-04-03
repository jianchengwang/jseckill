package com.example.jseckill.infrastructure.sk.repository;

import cn.hutool.core.lang.UUID;
import com.example.jseckill.domain.seckill.entity.SkOrderDomain;
import com.example.jseckill.domain.seckill.entity.SkPayDomain;
import com.example.jseckill.domain.seckill.repository.SkRedisRepository;
import com.example.jseckill.infrastructure.common.consts.SkConstant;
import com.example.jseckill.infrastructure.framework.exception.ClientException;
import com.example.jseckill.infrastructure.framework.exception.FrameworkErrorCode;
import com.example.jseckill.infrastructure.framework.utils.RedisUtil;
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

    private final RedisUtil redisUtil;
    private final RedissonClient redissonClient;

    @Override
    public boolean hasSkToken(Long skGoodsId, Long userId) {
        String skTokenKey = String.format(SkConstant.SK_TOKEN_KEY, skGoodsId, userId);
        return Boolean.TRUE.equals(redisUtil.redisTemplate.hasKey(skTokenKey));
    }

    @Override
    public String getOrSetSkToken(Long skGoodsId, Long userId) {
        String skTokenKey = String.format(SkConstant.SK_TOKEN_KEY, skGoodsId, userId);
        if(Boolean.FALSE.equals(redisUtil.redisTemplate.hasKey(skTokenKey))) {
            redisUtil.setCacheObject(skTokenKey, UUID.fastUUID(), 5, TimeUnit.MINUTES);
        }
        return redisUtil.getCacheObject(skTokenKey).toString();
    }

    @Override
    public void preheatSkGoods(SkGoods skGoods) {
        Long skGoodsId = skGoods.getId();
        BigInteger stockNum = skGoods.getStockNum();
        RTransaction transaction = redissonClient.createTransaction(TransactionOptions.defaults());
        String skGoodsStockKey = String.format(SkConstant.SK_GOODS_STOCK, skGoodsId);
        RBucket<Long> skGoodsStock = transaction.getBucket(skGoodsStockKey);
        skGoodsStock.set(stockNum.longValue(), 2, TimeUnit.DAYS);
        transaction.commit();
    }

    @Override
    public BigInteger getSkGoodsStockNum(Long skGoodsId) {
        String skGoodsStockKey = String.format(SkConstant.SK_GOODS_STOCK, skGoodsId);
        if(Boolean.FALSE.equals(redisUtil.redisTemplate.hasKey(skGoodsStockKey))) {
            return BigInteger.ZERO;
        }
        RBucket<Long> skGoodsStock = redissonClient.getBucket(skGoodsStockKey);
        return BigInteger.valueOf(skGoodsStock.get());
    }

    @Override
    public long addSkGoodsStock(Long skGoodsId, long addStock) {
        String skGoodsStockKey = String.format(SkConstant.SK_GOODS_STOCK, skGoodsId);
        RAtomicLong stock = redissonClient.getAtomicLong(skGoodsStockKey);
        return stock.addAndGet(addStock);
    }

    @Override
    public long subSkGoodsStock(Long skGoodsId, long subStock) {
        String skGoodsStockKey = String.format(SkConstant.SK_GOODS_STOCK, skGoodsId);
        RAtomicLong stock = redissonClient.getAtomicLong(skGoodsStockKey);
        if(stock.get() < subStock) {
            return -1;
        } else {
            return stock.addAndGet(-subStock);
        }
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
        if(Boolean.TRUE.equals(redisUtil.redisTemplate.hasKey(skOrderCreateFlagKey))) {
            return redisUtil.getCacheObject(skOrderCreateFlagKey);
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
        RBlockingQueue<Long> blockingFairQueue = redissonClient.getBlockingQueue(SkConstant.SK_ORDER_WAITPAY_QUEUE);
        RDelayedQueue<Long> delayedQueue = redissonClient.getDelayedQueue(blockingFairQueue);
        delayedQueue.offer(null, 5, TimeUnit.MINUTES);
        return blockingFairQueue;
    }

    @Override
    public void createOrderSuccess(String skToken, Long skGoodsId, long buyNum, Long userId, String orderNo) {
        RTransaction transaction = redissonClient.createTransaction(TransactionOptions.defaults());
        // 待支付数量+1
        String waitPayCountKey = String.format(SkConstant.SK_USER_WAIT_PAY_COUNT, userId);
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
        redisUtil.setCacheObject(skOrderCreateFlagKey, "-1", 5, TimeUnit.MINUTES);
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
    public long getUserWaitPayCount(Long skGoodsId, Long userId) {
        String waitPayCountKey = String.format(SkConstant.SK_USER_WAIT_PAY_COUNT, skGoodsId, userId);
        if(redisUtil.redisTemplate.hasKey(waitPayCountKey)) {
            return redisUtil.getCacheObject(waitPayCountKey);
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
        if(redisUtil.redisTemplate.hasKey(userBuyCountKey)) {
            return redisUtil.getCacheObject(userBuyCountKey);
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
