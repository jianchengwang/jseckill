package com.example.jseckill.infrastructure.biz.seckill.db.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.jseckill.infrastructure.common.pojo.PO;
import lombok.Data;

import java.math.BigInteger;

/**
 * @author jianchengwang
 * @date 2023/3/30
 */
@Data
@TableName("t_seckill_goods")
public class SeckillGoods implements PO {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long activityId;
    private Long goodsId;
    private String goodsName;
    private BigInteger goodsPrice;
    private Long supId;
    private Long skuId;
    private String content;
    private BigInteger seckillPrice;
    private BigInteger seckillNum;
    private BigInteger storeCount;

    // getters and setters
}
