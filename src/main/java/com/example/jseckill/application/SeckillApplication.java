package com.example.jseckill.application;

import com.example.jseckill.domain.seckill.entity.SkGoodsDomain;
import com.example.jseckill.domain.seckill.entity.SkOrderDomain;
import com.example.jseckill.domain.seckill.entity.SkUserDomain;
import com.example.jseckill.domain.seckill.repository.SkGoodsRepository;
import com.example.jseckill.domain.seckill.repository.SkOrderRepository;
import com.example.jseckill.domain.seckill.repository.SkRedisRepository;
import com.example.jseckill.infrastructure.common.converter.SkGoodsConverter;
import com.example.jseckill.infrastructure.common.enums.PayMethodEnum;
import com.example.jseckill.infrastructure.framework.exception.ClientException;
import com.example.jseckill.infrastructure.framework.exception.FrameworkErrorCode;
import com.example.jseckill.infrastructure.sk.db.po.SkGoods;
import com.example.jseckill.infrastructure.sk.db.po.SkOrder;
import com.example.jseckill.interfaces.client.dto.ConfirmPayInfoDTO;
import com.example.jseckill.interfaces.client.dto.CreateOrderDTO;
import com.example.jseckill.interfaces.client.vo.SkGoodsInfoVO;
import com.example.jseckill.interfaces.thirdparty.dto.WxPayInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

/**
 * @author jianchengwang
 * @date 2023/3/31
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SeckillApplication {

    @Value("${backendNotifyUrl}")
    private String backendNotifyUrl;

    private final SkGoodsRepository skGoodsRepository;
    private final SkOrderRepository orderRepository;
    private final SkRedisRepository redisRepository;

    public SkGoodsInfoVO getGoodsInfo(Long skGoodsId) {
        SkGoods skGoods = skGoodsRepository.findById(skGoodsId);
        SkGoodsInfoVO goodsInfoVO = SkGoodsConverter.MAPPER.toSkGoodsInfoVO(skGoods);
        goodsInfoVO.setStockNum(redisRepository.getSkGoodsStockNum(skGoodsId));
        return goodsInfoVO;
    }

    public String getSkToken(Long skGoodsId, Long userId, String entryKey) {
        // 校验活动是否开始
        SkGoods skGoods = skGoodsRepository.findById(skGoodsId);
        SkGoodsDomain skGoodsDomain = new SkGoodsDomain(skGoods);
        skGoodsDomain.validate();
        // 校验密钥并返回令牌
        SkUserDomain skUserDomain = new SkUserDomain(userId, skGoodsId, entryKey, redisRepository);
        return skUserDomain.validateEntryKeyAndGetToken(skGoods.getEntryKey());
    }

    public void createOrder(CreateOrderDTO createOrderParam) {
        Long skGoodsId = createOrderParam.getSkGoodsId();
        Long userId = createOrderParam.getUserId();
        BigInteger buyNum = createOrderParam.getBuyNum();
        // 校验活动是否开始
        SkGoods skGoods = skGoodsRepository.findById(skGoodsId);
        SkGoodsDomain skGoodsDomain = new SkGoodsDomain(skGoods);
        skGoodsDomain.validate();
        // 校验令牌
        SkUserDomain skUserDomain = new SkUserDomain(createOrderParam, redisRepository);
        String skToken = createOrderParam.getSkToken();
        // 校验用户是否可以购买
        skUserDomain.validateCanBuy(skToken, skGoods.getBuyLimit());
        // 创建订单
        SkOrderDomain skOrderDomain = new SkOrderDomain(skToken, skGoodsId, skGoods.getSkPrice(), buyNum, userId, redisRepository);
        // 预减库存
        skOrderDomain.subStock();
        // 推送订单到队列
        try {
            redisRepository.pushSkOrder(skOrderDomain);
        } catch (Exception e) {
            // 推送失败，回滚库存
            skOrderDomain.addStock();
            throw new ClientException("活动火爆，请稍后再试", FrameworkErrorCode.LEGAL_REQUEST);
        }
    }

    public String checkCreateOrderSuccess(String skToken) {
        return redisRepository.checkOrderCreateSuccess(skToken);
    }

    public WxPayInfoDTO confirmPayInfo(ConfirmPayInfoDTO confirmPayInfoParam) {
        String orderNo = confirmPayInfoParam.getOrderNo();
        PayMethodEnum payMethod = confirmPayInfoParam.getPayMethod();
        BigInteger payMoney = confirmPayInfoParam.getPayMoney();
        SkOrder skOrder = orderRepository.findByOrderNo(orderNo);
        SkOrderDomain skOrderDomain = new SkOrderDomain(skOrder, redisRepository);
        skOrderDomain.confirmPayInfo(payMoney);
        orderRepository.confirmPayInfo(skOrder.getId(), payMethod, payMoney);

        WxPayInfoDTO wxPayInfoDTO = new WxPayInfoDTO();
        wxPayInfoDTO.setOutTradeNo(skOrder.getOrderNo());
        wxPayInfoDTO.setPayMoney(payMoney);
        wxPayInfoDTO.setSign("sign");
        wxPayInfoDTO.setExpireTime(skOrder.getOrderTime().plusMinutes(5));
        wxPayInfoDTO.setBackendNotifyUrl(backendNotifyUrl);
        return wxPayInfoDTO;
    }
}
