package com.example.jseckill.application;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.jseckill.domain.seckill.repository.SkGoodsRepository;
import com.example.jseckill.domain.seckill.repository.SkRedisRepository;
import com.example.jseckill.infrastructure.sk.db.po.SkGoods;
import com.example.jseckill.interfaces.operate.dto.SkGoodsCreateDTO;
import com.example.jseckill.interfaces.operate.dto.SkGoodsPreheatDTO;
import com.example.jseckill.interfaces.operate.query.SkGoodsQuery;
import com.example.jseckill.interfaces.operate.vo.SkGoodsVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.framework.pojo.PageInfo;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

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

    public IPage<SkGoodsVO> page(PageInfo pageInfo, SkGoodsQuery query) {
        return skGoodsRepository.page(pageInfo, query);
    }

    public void preheat(Long skGoodsId, SkGoodsPreheatDTO preheatParam) {
        SkGoods skGoods = skGoodsRepository.findById(skGoodsId);
//        if(skGoods.getStartTime().isBefore(LocalDateTime.now())) {
//            throw new ClientException("秒杀活动已经开始，不能再修改", FrameworkErrorCode.PARAM_VALIDATE_ERROR);
//        }
        skGoodsRepository.preheat(skGoodsId, preheatParam);
        redisRepository.preheatSkGoods(skGoods);
    }

    public Long createGoods(SkGoodsCreateDTO skGoodsCreateParam) {
        return skGoodsRepository.createGoods(skGoodsCreateParam);
    }

    public void loadCacheStock(Long skGoodsId) {
        BigInteger cacheStock = redisRepository.getSkGoodsStockNum(skGoodsId);
        skGoodsRepository.loadCacheStock(skGoodsId, cacheStock);
    }
}
