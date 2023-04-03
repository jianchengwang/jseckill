package com.example.jseckill.application;

import com.example.jseckill.domain.seckill.repository.SkGoodsRepository;
import com.example.jseckill.domain.seckill.repository.SkRedisRepository;
import com.example.jseckill.infrastructure.framework.exception.ClientException;
import com.example.jseckill.infrastructure.framework.exception.FrameworkErrorCode;
import com.example.jseckill.infrastructure.sk.db.po.SkGoods;
import com.example.jseckill.interfaces.operate.dto.SkGoodsCreateDTO;
import com.example.jseckill.interfaces.operate.dto.SkGoodsPreheatDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author jianchengwang
 * @date 2023/4/2
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SkGoodsApplication {
    private final SkGoodsRepository skGoodsRepository;
    private final SkRedisRepository redisRepository;

    public void preheat(Long skGoodsId, SkGoodsPreheatDTO preheatParam) {
        SkGoods skGoods = skGoodsRepository.findById(skGoodsId);
        if(skGoods.getStartTime().isBefore(LocalDateTime.now())) {
            throw new ClientException("秒杀活动已经开始，不能再修改", FrameworkErrorCode.PARAM_VALIDATE_ERROR);
        }
        skGoodsRepository.preheat(skGoodsId, preheatParam);
        redisRepository.preheatSkGoods(skGoods);
    }

    public Long createGoods(SkGoodsCreateDTO skGoodsCreateParam) {
        return skGoodsRepository.createGoods(skGoodsCreateParam);
    }
}
