package com.example.jseckill.infrastructure.biz.seckill.repository;

import com.example.jseckill.domain.seckill.repository.SeckillActivityRepository;
import com.example.jseckill.infrastructure.biz.seckill.db.dao.SeckillActivityDao;
import com.example.jseckill.infrastructure.biz.seckill.db.po.SeckillActivity;
import com.example.jseckill.infrastructure.common.exception.ClientException;
import com.example.jseckill.infrastructure.common.exception.FrameworkErrorCode;
import com.example.jseckill.infrastructure.consts.CacheConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @author jianchengwang
 * @date 2023/3/31
 */
@Service
@Slf4j
@RequiredArgsConstructor
@CacheConfig(cacheNames = CacheConstant.SECKILL_ACTIVITY_CACHE_NAME)
public class SeckillActivityRepositoryImpl implements SeckillActivityRepository {

    private final SeckillActivityDao activityDao;

    @Override
    @Cacheable(key = "'" + CacheConstant.SECKILL_ACTIVITY_CACHE_PREFIX + "' + #id", unless = "#result == null")
    public SeckillActivity findById(Long id) {
        SeckillActivity seckillActivity = activityDao.selectById(id);
        if(seckillActivity == null) {
            throw new ClientException(FrameworkErrorCode.RESOURCE_NOT_FOUND, "活动不存在");
        }
        return seckillActivity;
    }
}
