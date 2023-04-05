package com.example.jseckill.infrastructure.common.converter;

import com.example.jseckill.infrastructure.sk.db.po.SkGoods;
import com.example.jseckill.interfaces.client.vo.SkGoodsInfoVO;
import com.example.jseckill.interfaces.operate.dto.SkGoodsCreateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author jianchengwang
 * @date 2023/4/2
 */
@Mapper
public interface SkGoodsConverter {
    SkGoodsConverter MAPPER = Mappers.getMapper( SkGoodsConverter.class );

    SkGoodsInfoVO toSkGoodsInfoVO(SkGoods skGoods);

    SkGoods toPO(SkGoodsCreateDTO skGoodsCreateDTO);
}
