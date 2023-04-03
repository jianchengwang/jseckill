package com.example.jseckill.domain.seckill.entity;

import com.example.jseckill.domain.seckill.repository.SkRedisRepository;
import com.example.jseckill.infrastructure.framework.exception.ClientException;
import com.example.jseckill.infrastructure.framework.exception.FrameworkErrorCode;
import com.example.jseckill.interfaces.client.dto.CreateOrderDTO;

import java.math.BigInteger;
import java.util.Objects;

/**
 * @author jianchengwang
 * @date 2023/3/31
 */
public class SkUserDomain {
    private Long userId;
    private Long skGoodsId;
    private String entryKey;
    private SkRedisRepository redisRepository;
    public SkUserDomain(Long userId, Long skGoodsId, String entryKey, SkRedisRepository redisRepository) {
        this.userId = userId;
        this.skGoodsId = skGoodsId;
        this.entryKey = entryKey;
        this.redisRepository = redisRepository;
    }

    public SkUserDomain(CreateOrderDTO createOrderParam, SkRedisRepository redisRepository) {
        this.skGoodsId = createOrderParam.getSkGoodsId();
        this.userId = createOrderParam.getUserId();
        this.redisRepository = redisRepository;
    }

    public String validateEntryKeyAndGetToken(String validateEntryKey) {
        boolean isEntryKeyValid = Objects.equals(entryKey, validateEntryKey);
        if(isEntryKeyValid) {
            throw new ClientException(FrameworkErrorCode.LEGAL_REQUEST);
        }
        return redisRepository.getOrSetSkToken(skGoodsId, userId);
    }

    public void validateCanBuy(String skToken, BigInteger buyLimit) {
        boolean validateToken = redisRepository.hasSkToken(skGoodsId, userId) && Objects.equals(skToken, redisRepository.getOrSetSkToken(skGoodsId, userId));
        if (!validateToken) {
            throw new ClientException("令牌无效", FrameworkErrorCode.LEGAL_REQUEST);
        }
        BigInteger userBuyCount = BigInteger.valueOf(redisRepository.getUserBuyCount(skGoodsId, userId));
        if (userBuyCount.compareTo(buyLimit) >= 0) {
            throw new ClientException("购买超出限制", FrameworkErrorCode.LEGAL_REQUEST);
        }
        long userWaitPayCount = redisRepository.getUserWaitPayCount(skGoodsId, userId);
        if (userWaitPayCount > 0) {
            throw new ClientException("存在未支付订单", FrameworkErrorCode.LEGAL_REQUEST);
        }
    }
}
