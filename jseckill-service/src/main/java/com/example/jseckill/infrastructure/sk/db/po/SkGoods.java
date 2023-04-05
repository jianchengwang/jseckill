package com.example.jseckill.infrastructure.sk.db.po;

import com.baomidou.mybatisplus.annotation.*;
import com.example.jseckill.infrastructure.framework.pojo.PO;
import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * @author jianchengwang
 * @date 2023/3/30
 */
@Data
@TableName("t_sk_goods")
public class SkGoods implements PO {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String goodsName;
    private BigInteger goodsPrice;
    private BigInteger skPrice;
    private BigInteger skNum;
    private BigInteger stockNum;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String entryKey;
    private BigInteger buyLimit;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateAt;
    @TableField(fill = FieldFill.UPDATE)
    private Long updateBy;

    // getters and setters
}
