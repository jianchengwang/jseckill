package com.example.jseckill.domain.seckill.entity;

import com.example.jseckill.domain.seckill.repository.SkRedisRepository;
import com.example.jseckill.infrastructure.common.enums.OrderStatusEnum;
import com.example.jseckill.infrastructure.framework.exception.ClientException;
import com.example.jseckill.infrastructure.framework.exception.FrameworkErrorCode;
import com.example.jseckill.infrastructure.sk.db.po.SkOrder;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author jianchengwang
 * @date 2023/4/1
 */
public class SkOrderDomain {
    private String skToken;
    private Long skGoodsId;
    private BigInteger skPrice;
    private BigInteger buyNum;
    private Long userId;
    private SkRedisRepository redisRepository;
    private int tryTimes = 0;

    public SkOrderDomain(String skToken, Long skGoodsId, BigInteger skPrice, BigInteger buyNum, Long userId, SkRedisRepository redisRepository) {
        this.skToken = skToken;
        this.skGoodsId = skGoodsId;
        this.skPrice = skPrice;
        this.buyNum = buyNum;
        this.userId = userId;
        this.redisRepository = redisRepository;
        this.skToken = skToken;
    }

    public SkOrderDomain(SkOrder skOrder, SkRedisRepository redisRepository) {
        this.skGoodsId = skOrder.getSkGoodsId();
        this.skPrice = skOrder.getSkPrice();
        this.buyNum = skOrder.getBuyNum();
        this.userId = skOrder.getUserId();
        this.redisRepository = redisRepository;
    }

    public SkOrder createOrder() {
        LocalDateTime nowTime = LocalDateTime.now();
        SkOrder skOrder = new SkOrder();
        skOrder.setOrderNo(buildOrderNo(nowTime));
        skOrder.setSkGoodsId(skGoodsId);
        skOrder.setSkPrice(skPrice);
        skOrder.setUserId(userId);
        skOrder.setBuyNum(buyNum);
        skOrder.setOrderStatus(OrderStatusEnum.NEW);
        skOrder.setOrderTime(nowTime);
        skOrder.setOrderMoney(buyNum.multiply(skPrice));
        return skOrder;
    }

    public String getSkToken() {
        return skToken;
    }

    public int incrTryTimes() {
        tryTimes++;
        return tryTimes;
    }

    private final static DateTimeFormatter DayFormatter = DateTimeFormatter.ofPattern("yyMMdd");
    private final String OrderSeqFmt = "%s%s%05d";
    private final String OrderNoFmt = "%s%04d";
    private String buildOrderNo(LocalDateTime nowTime) {
        LocalDateTime dayBegin = LocalDateTime.of(nowTime.getYear(), nowTime.getMonth(), nowTime.getDayOfMonth(), 0, 0, 0);
        //格式化当前时间为【年的后2位+月+日】
        String yymmddDateStr = DayFormatter.format(nowTime);
        //计算当前时间走过的秒
        long differSecond = (nowTime.getSecond() - dayBegin.getSecond());
        //获取【业务编码】 + 【年的后2位+月+日+秒】，作为自增key；
        String orderSeq = String.format(OrderSeqFmt, "sk", yymmddDateStr, differSecond);
        //通过key，采用redis自增函数，实现单秒自增；不同的key，从0开始自增，同时设置60秒过期
        Long incrId = redisRepository.incrSkOrderSeq(orderSeq);
        //生成订单编号
        String orderNo = String.format(OrderNoFmt, orderSeq, incrId);
        return orderNo;
    }

    public void addStock() {
        redisRepository.addSkGoodsStock(skGoodsId, buyNum.longValue());
    }

    public void subStock() {
        if(redisRepository.subSkGoodsStock(skGoodsId, buyNum.longValue()) == -1) {
            throw new ClientException("库存不足", FrameworkErrorCode.LEGAL_REQUEST);
        }
    }

    public void createOrderSuccess(String orderNo) {
        redisRepository.createOrderSuccess(skToken, skGoodsId, buyNum.longValue(), userId, orderNo);
    }

    public void createOrderFail() {
        redisRepository.createOrderFail(skToken);
    }

    public void createOrderCancel() {
        redisRepository.createOrderCancel(skGoodsId, userId, buyNum.longValue());
    }

    public void confirmPayInfo(BigInteger payMoney) {

    }
}