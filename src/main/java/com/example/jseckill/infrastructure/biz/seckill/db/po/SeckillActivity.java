package com.example.jseckill.infrastructure.biz.seckill.db.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.jseckill.infrastructure.enums.ActivityStatusEnum;
import com.example.jseckill.infrastructure.common.pojo.PO;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author jianchengwang
 * @date 2023/3/30
 */
@Data
@TableName("t_seckill_activity")
public class SeckillActivity implements PO {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String activityName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private ActivityStatusEnum activityStatus;
    private String entryKey;

    // getters and setters
}
