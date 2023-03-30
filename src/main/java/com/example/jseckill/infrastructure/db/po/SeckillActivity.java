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
@TableName("t_seckill_activity")
public class SeckillActivity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String activityName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer activityStatus;

    // getters and setters
}
