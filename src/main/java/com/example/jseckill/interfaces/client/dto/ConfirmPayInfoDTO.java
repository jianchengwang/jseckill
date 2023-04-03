package com.example.jseckill.interfaces.client.dto;

import com.example.jseckill.infrastructure.common.enums.PayMethodEnum;
import com.example.jseckill.infrastructure.framework.pojo.DTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigInteger;

/**
 * @author jianchengwang
 * @date 2023/4/2
 */
@Schema(description = "客户端-秒杀模块-确认支付信息")
@Data
public class ConfirmPayInfoDTO implements DTO {
    @NotNull
    @Schema(description = "订单编号")
    private Long orderId;
    @NotNull
    @Schema(description = "支付方式")
    private PayMethodEnum payMethod;
    @NotNull
    @Min(0)
    @Schema(description = "支付金额")
    private BigInteger payMoney;
}