package com.example.jseckill.domain.seckill.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.jseckill.infrastructure.sk.db.po.SkGoods;
import com.example.jseckill.interfaces.operate.dto.SkGoodsCreateDTO;
import com.example.jseckill.interfaces.operate.dto.SkGoodsPreheatDTO;
import com.example.jseckill.interfaces.operate.query.SkGoodsQuery;
import com.example.jseckill.interfaces.operate.vo.SkGoodsVO;
import org.example.framework.pojo.PageInfo;

import java.math.BigInteger;

public interface SkGoodsRepository extends IService<SkGoods> {
    IPage<SkGoodsVO> page(PageInfo pageInfo, SkGoodsQuery param);
    SkGoods findById(Long id);
    Long createGoods(SkGoodsCreateDTO skGoodsCreateParam);
    int subStock(Long skGoodsId, BigInteger deltaStock);

    int loadCacheStock(Long skGoodsId, BigInteger cacheStock);

    SkGoods preheat(Long skGoodsId, SkGoodsPreheatDTO warmUpParam);
}
