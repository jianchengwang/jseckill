package com.example.jseckill.application;

import com.example.jseckill.domain.seckill.repository.SeckillActivityRepository;
import com.example.jseckill.infrastructure.biz.seckill.db.po.SeckillActivity;
import com.example.jseckill.infrastructure.common.exception.ClientException;
import com.example.jseckill.infrastructure.common.exception.FrameworkErrorCode;
import com.example.jseckill.infrastructure.common.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author jianchengwang
 * @date 2023/3/31
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SeckillApplication {

    private final SeckillActivityRepository seckillActivityRepository;
    private final RedisUtil redisUtil;

    public String genSeckillToken(String entryKey, Long activityId, Long goodsId, Long userId) {
        SeckillActivity seckillActivity = seckillActivityRepository.findById(activityId);
        boolean isEntryKeyValid = Objects.equals(entryKey, seckillActivity.getEntryKey());
        if(isEntryKeyValid) {
            throw new ClientException(FrameworkErrorCode.LEGAL_REQUEST);
        }
        return null;
    }
}
