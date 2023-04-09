package com.example.jseckill.infrastructure.sk.repository;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.jseckill.domain.seckill.repository.SkGoodsRepository;
import com.example.jseckill.infrastructure.common.consts.CacheConstant;
import com.example.jseckill.infrastructure.common.converter.SkGoodsConverter;
import com.example.jseckill.infrastructure.sk.db.dao.SkGoodsDao;
import com.example.jseckill.infrastructure.sk.db.po.SkGoods;
import com.example.jseckill.infrastructure.user.db.po.User;
import com.example.jseckill.interfaces.operate.dto.SkGoodsCreateDTO;
import com.example.jseckill.interfaces.operate.dto.SkGoodsPreheatDTO;
import com.example.jseckill.interfaces.operate.query.SkGoodsQuery;
import com.example.jseckill.interfaces.operate.vo.SkGoodsVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.framework.config.mybatis.MpHelper;
import org.example.framework.exception.ClientException;
import org.example.framework.exception.FrameworkErrorCode;
import org.example.framework.pojo.PageInfo;
import org.example.framework.utils.PageUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

/**
 * @author jianchengwang
 * @date 2023/4/1
 */
@Service
@Slf4j
@RequiredArgsConstructor
@CacheConfig(cacheNames = CacheConstant.SK_GOODS_CACHE_NAME)
public class SkGoodsRepositoryImpl extends ServiceImpl<SkGoodsDao, SkGoods> implements SkGoodsRepository {

    private final SkGoodsDao skGoodsDao;

    @Override
    public IPage<SkGoodsVO> page(PageInfo pageInfo, SkGoodsQuery param) {
        LambdaQueryWrapper<SkGoods> query = MpHelper.lambdaQuery("a", BeanUtil.copyProperties(param, SkGoods.class));
        return skGoodsDao.page(PageUtils.buildPage(pageInfo), param, query);
    }

    @Override
    @Cacheable(key = "'" + CacheConstant.SK_GOODS_CACHE_PREFIX + "' + #id", unless = "#result == null")
    public SkGoods findById(Long id) {
        SkGoods skGoods = skGoodsDao.selectById(id);
        if(skGoods == null) {
            throw new ClientException("秒杀商品不存在", FrameworkErrorCode.RESOURCE_NOT_FOUND);
        }
        return skGoods;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Long createGoods(SkGoodsCreateDTO skGoodsCreateParam) {
        SkGoods skGoods = SkGoodsConverter.MAPPER.toPO(skGoodsCreateParam);
        skGoods.setStockNum(skGoodsCreateParam.getSkNum());
        skGoodsDao.insert(skGoods);
        return skGoods.getId();
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public int subStock(Long skGoodsId, BigInteger deltaStock) {
        return skGoodsDao.subStock(skGoodsId, deltaStock);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public int loadCacheStock(Long skGoodsId, BigInteger cacheStock) {
        return skGoodsDao.loadCacheStock(skGoodsId, cacheStock);
    }

    @Override
    @CachePut(key = "'" + CacheConstant.SK_GOODS_CACHE_PREFIX + "' + #id", unless = "#result == null")
    public SkGoods preheat(Long id, SkGoodsPreheatDTO warmUpParam) {
        SkGoods skGoods = findById(id);
        SkGoods updateObj = new SkGoods();
        updateObj.setId(id);
        updateObj.setEntryKey(warmUpParam.getEntryKey());
        updateObj.setBuyLimit(warmUpParam.getBuyLimit());
        updateObj.setId(id);
        skGoodsDao.updateById(updateObj);

        skGoods.setEntryKey(warmUpParam.getEntryKey());
        skGoods.setBuyLimit(warmUpParam.getBuyLimit());

        return skGoods;
    }
}
