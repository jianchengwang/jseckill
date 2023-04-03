package com.example.jseckill.interfaces.client.api;

import cn.dev33.satoken.annotation.SaIgnore;
import com.example.jseckill.application.SeckillApplication;
import com.example.jseckill.infrastructure.framework.config.permission.user.TokenUserUtil;
import com.example.jseckill.infrastructure.framework.pojo.Response;
import com.example.jseckill.interfaces.client.dto.ConfirmPayInfoDTO;
import com.example.jseckill.interfaces.client.dto.CreateOrderDTO;
import com.example.jseckill.interfaces.client.vo.SkGoodsInfoVO;
import com.example.jseckill.interfaces.thirdparty.dto.WxPayInfoDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author jianchengwang
 * @date 2023/3/30
 */
@RestController
@RequestMapping("/api/client/seckill")
@RequiredArgsConstructor
@Tag(name = "客户端-秒杀模块")
public class SeckillController {

    @Value("${payUrl}")
    private String payUrl;

    private final SeckillApplication seckillApplication;

    private final RestTemplate restTemplate;
    private final ExecutorService payExecutor = Executors.newFixedThreadPool(5);

    @Operation(summary = "获取商品信息", description = "获取商品信息")
    @GetMapping("goodsInfo/{skGoodsId}")
    @SaIgnore
    public Response<SkGoodsInfoVO> getGoodsInfo(@PathVariable Long skGoodsId) {
        return Response.ok(seckillApplication.getGoodsInfo(skGoodsId));
    }

    @Operation(summary = "获取令牌", description = "获取令牌")
    @GetMapping("token/{skGoodsId}")
    public Response<String> getSkToken(@PathVariable Long skGoodsId, String entryKey) {
        Long userId = TokenUserUtil.currentUser();
        String skToken = seckillApplication.getSkToken(skGoodsId, userId, entryKey);
        return Response.ok(skToken);
    }

    @Operation(summary = "提交订单", description = "提交订单")
    @PostMapping("order")
    public Response<Void> createOrder(@Valid @RequestBody CreateOrderDTO createOrderParam) {
        createOrderParam.setUserId(TokenUserUtil.currentUser());
        seckillApplication.createOrder(createOrderParam);
        return Response.ok();
    }

    @Operation(summary = "校验是否成功创建订单", description = "校验是否成功创建订单，-1创建失败；0未创建；>0为订单编号")
    @GetMapping("order/check/{skToken}")
    public Response<String> checkCreateOrderSuccess(@PathVariable String skToken) {
        return Response.ok(seckillApplication.checkCreateOrderSuccess(skToken));
    }

    @Operation(summary = "确认支付信息", description = "确认支付信息")
    @PostMapping("order/confirmPayInfo")
    public Response<Void> confirmPayInfo(@Valid @RequestBody ConfirmPayInfoDTO confirmPayInfoParam) {
        WxPayInfoDTO wxPayInfoDTO = seckillApplication.confirmPayInfo(confirmPayInfoParam);
        // 跳过支付环节，直接支付成功
        payExecutor.execute(new CallPayRunnable(wxPayInfoDTO));
        return Response.ok();
    }

    public class CallPayRunnable implements Runnable {
        private final WxPayInfoDTO wxPayInfoDTO;
        public CallPayRunnable(WxPayInfoDTO wxPayInfoDTO) {
            this.wxPayInfoDTO = wxPayInfoDTO;
        }
        @Override
        public void run() {
            try {
                restTemplate.postForObject(payUrl, wxPayInfoDTO, Response.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
