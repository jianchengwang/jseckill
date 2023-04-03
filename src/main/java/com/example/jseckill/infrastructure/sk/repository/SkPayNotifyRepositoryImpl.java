package com.example.jseckill.infrastructure.sk.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.jseckill.domain.seckill.repository.SkPayNotifyRepository;
import com.example.jseckill.infrastructure.common.converter.PayNotifyConverter;
import com.example.jseckill.infrastructure.sk.db.dao.SkPayNotifyDao;
import com.example.jseckill.infrastructure.sk.db.po.SkPayNotify;
import com.example.jseckill.interfaces.client.dto.PayNotifyDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author jianchengwang
 * @date 2023/4/2
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SkPayNotifyRepositoryImpl extends ServiceImpl<SkPayNotifyDao, SkPayNotify> implements SkPayNotifyRepository {

    private final SkPayNotifyDao skPayNotifyDao;

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void batchInsert(List<PayNotifyDTO> payNotifyList) {
        if (CollectionUtils.isEmpty(payNotifyList)) {
            return;
        }
        List<SkPayNotify> insertList = PayNotifyConverter.MAPPER.toPOList(payNotifyList);
        this.saveBatch(insertList);
    }
}
