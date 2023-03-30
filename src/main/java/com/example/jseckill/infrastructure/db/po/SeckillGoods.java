package com.example.jseckill.infrastructure.db.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author jianchengwang
 * @date 2023/3/30
 */
@Data
@TableName("t_seckill_goods")
public class SeckillGoods {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long activityId;
    private Long goodsId;
    private String goodsName;
    private Long goodsPrice;
    private Long supId;
    private Long skuId;
    private String content;
    private Long seckillPrice;
    private Long seckillNum;
    private Long storeCount;

    // getters and setters
}
