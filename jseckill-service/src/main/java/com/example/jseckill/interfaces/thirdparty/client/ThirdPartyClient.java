package com.example.jseckill.interfaces.thirdparty.client;

import com.example.jseckill.interfaces.thirdparty.dto.WxPayInfoDTO;
import com.example.jseckill.interfaces.thirdparty.dto.WxPayNotifyDTO;
import jakarta.validation.Valid;
import org.example.framework.pojo.Response;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;

/**
 * @author jianchengwang
 * @date 2023/4/7
 */
public interface ThirdPartyClient {

    @PostExchange("")
    Response<WxPayNotifyDTO> wxpay(@Valid @RequestBody WxPayInfoDTO wxPayInfoDTO);

}
