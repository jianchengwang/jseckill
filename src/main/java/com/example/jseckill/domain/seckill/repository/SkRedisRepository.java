package com.example.jseckill.domain.seckill.repository;

import com.example.jseckill.domain.seckill.entity.SkOrderDomain;
import com.example.jseckill.domain.seckill.entity.SkPayDomain;
import com.example.jseckill.infrastructure.sk.db.po.SkGoods;
import com.example.jseckill.interfaces.client.dto.PayNotifyDTO;
import org.redisson.api.RBlockingQueue;

import java.math.BigInteger;
import java.util.List;

public interface SkRedisRepository {
    boolean hasSkToken(Long skGoodsId, Long userId);
    String getOrSetSkToken(Long skGoodsId, Long userId);
    void preheatSkGoods(SkGoods skGoods);
    BigInteger getSkGoodsStockNum(Long skGoodsId);
    long addSkGoodsStock(Long skGoodsId, long addStock);
    long subSkGoodsStock(Long skGoodsId, long subStock);
    long incrSkOrderSeq(String orderSeq);
    void pushSkOrder(SkOrderDomain skOrder);
    SkOrderDomain popSkOrder();
    String checkOrderCreateSuccess(String skToken);
    void pushWaitPayOrder(Long orderId);
    RBlockingQueue<Long> getWaitPayOrderQueue();
    void createOrderSuccess(String skToken, Long skGoodsId, long buyNum, Long userId, String orderNo);
    void createOrderFail(String skToken);
    void createOrderCancel(Long skGoodsId, Long userId, long buyNum);
    long getUserBuyCount(Long skGoodsId, Long userId);
    long getUserWaitPayCount(Long skGoodsId, Long userId);
    void pushPayNotify(List<PayNotifyDTO> payNotifyList);
    List<PayNotifyDTO> popPayNotify(int limit);
    void pushOrderPaySuccessSet(String orderNo);
    boolean containsOrderPaySuccessSet(String orderNo);
    void pushPaySuccessNotify(SkPayDomain payNotify);
    SkPayDomain popPaySuccessNotify();
}
