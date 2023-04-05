package com.example.jseckill.interfaces.operate.api;

import com.example.jseckill.application.SkGoodsApplication;
import com.example.jseckill.infrastructure.framework.pojo.Response;
import com.example.jseckill.interfaces.operate.dto.SkGoodsCreateDTO;
import com.example.jseckill.interfaces.operate.dto.SkGoodsPreheatDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @author jianchengwang
 * @date 2023/4/2
 */
@RestController
@RequestMapping("/api/operate/skGoods")
@RequiredArgsConstructor
@Tag(name = "运营端-秒杀商品")
public class SkGoodsController {

    private final SkGoodsApplication skGoodsApplication;

    @Operation(summary = "创建商品", description = "创建商品")
    @PostMapping("create")
    public Response<Long> create(@Valid @RequestBody SkGoodsCreateDTO skGoodsCreateParam) {
        return Response.ok(skGoodsApplication.createGoods(skGoodsCreateParam));
    }

    @Operation(summary = "预热商品", description = "预热商品")
    @PutMapping("preheat/{skGoodsId}")
    public Response<String> preheat(@PathVariable Long skGoodsId, @Valid @RequestBody SkGoodsPreheatDTO preheatParam) {
        skGoodsApplication.preheat(skGoodsId, preheatParam);
        return Response.ok();
    }
}
