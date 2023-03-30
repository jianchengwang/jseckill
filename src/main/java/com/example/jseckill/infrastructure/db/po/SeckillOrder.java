package com.example.jseckill.infrastructure.db.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author jianchengwang
 * @date 2023/3/30
 */
@Data
@TableName("t_seckill_order")
public class SeckillOrder {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long seckillGoodsId;
    private Long payMoney;
    private LocalDateTime createTime;
    private LocalDateTime payTime;
    private Integer payType;
    private String payTransactionId;
    private Long userId;
    private Integer orderStatus;

    // getters and setters
}
