package com.example.jseckill.infrastructure.common.converter;

import com.example.jseckill.infrastructure.sk.db.po.SkPayNotify;
import com.example.jseckill.interfaces.client.dto.PayNotifyDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author jianchengwang
 * @date 2023/4/2
 */
@Mapper
public interface PayNotifyConverter {
    PayNotifyConverter MAPPER = Mappers.getMapper( PayNotifyConverter.class );

    List<SkPayNotify> toPOList(List<PayNotifyDTO> payNotifyList);
}
