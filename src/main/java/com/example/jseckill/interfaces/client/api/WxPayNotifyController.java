package com.example.jseckill.interfaces.client.api;

import cn.hutool.core.bean.BeanUtil;
import com.example.jseckill.application.SkPayApplication;
import com.example.jseckill.infrastructure.common.enums.PayMethodEnum;
import com.example.jseckill.infrastructure.framework.pojo.Response;
import com.example.jseckill.interfaces.client.dto.PayNotifyDTO;
import com.example.jseckill.interfaces.thirdparty.dto.WxPayNotifyDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jianchengwang
 * @date 2023/4/2
 */
@RestController
@RequestMapping("/api/client/wxpay/notify")
@RequiredArgsConstructor
@Tag(name = "客户端-微信支付回调")
public class WxPayNotifyController {

    private final SkPayApplication skPayApplication;

    @PostMapping("")
    public Response<String> notify(@Valid @RequestBody WxPayNotifyDTO wxPayNotifyDTO) {
        PayNotifyDTO payNotify = new PayNotifyDTO();
        BeanUtil.copyProperties(wxPayNotifyDTO, payNotify, false);
        payNotify.setPayMethod(PayMethodEnum.WECHAT);
        skPayApplication.payNotify(payNotify);
        if(skPayApplication.payNotify(payNotify)) {
            return Response.ok("SUCCESS");
        }
        return Response.ok("FAIL");
    }
}
