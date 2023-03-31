package com.example.jseckill.infrastructure.biz.seckill.db.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.jseckill.infrastructure.enums.PayMethodEnum;
import com.example.jseckill.infrastructure.enums.OrderStatusEnum;
import com.example.jseckill.infrastructure.common.pojo.PO;
import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * @author jianchengwang
 * @date 2023/3/30
 */
@Data
@TableName("t_seckill_order")
public class SeckillOrder implements PO {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long seckillGoodsId;
    private LocalDateTime orderTime;
    private BigInteger orderMoney;
    private LocalDateTime payTime;
    private BigInteger payMoney;
    private PayMethodEnum payMethod;
    private String payTransactionId;
    private Long userId;
    private OrderStatusEnum orderStatus;

    // getters and setters
}
