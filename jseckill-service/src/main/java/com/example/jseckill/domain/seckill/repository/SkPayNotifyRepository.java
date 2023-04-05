package com.example.jseckill.domain.seckill.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.jseckill.infrastructure.sk.db.po.SkPayNotify;
import com.example.jseckill.interfaces.client.dto.PayNotifyDTO;

import java.util.List;

public interface SkPayNotifyRepository extends IService<SkPayNotify> {
    void batchInsert(List<PayNotifyDTO> payNotifyList);
}
