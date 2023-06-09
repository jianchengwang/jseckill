package com.example.jseckill.interfaces.operate.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.jseckill.application.SkGoodsApplication;
import com.example.jseckill.interfaces.operate.dto.SkGoodsCreateDTO;
import com.example.jseckill.interfaces.operate.dto.SkGoodsPreheatDTO;
import com.example.jseckill.interfaces.operate.query.SkGoodsQuery;
import com.example.jseckill.interfaces.operate.vo.SkGoodsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.framework.pojo.PageInfo;
import org.example.framework.pojo.Response;
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

    @Operation(summary = "商品分页", description = "商品分页")
    @GetMapping("page")
    public Response<IPage<SkGoodsVO>> page(PageInfo pageInfo, SkGoodsQuery query) {
        return Response.ok(skGoodsApplication.page(pageInfo, query));
    }

    @Operation(summary = "创建商品", description = "创建商品")
    @PostMapping("create")
    public Response<Long> create(@Valid @RequestBody SkGoodsCreateDTO skGoodsCreateParam) {
        return Response.ok(skGoodsApplication.createGoods(skGoodsCreateParam));
    }

    @Operation(summary = "预热商品", description = "预热商品")
    @PutMapping("{skGoodsId}/preheat")
    public Response<String> preheat(@PathVariable Long skGoodsId, @Valid @RequestBody SkGoodsPreheatDTO preheatParam) {
        skGoodsApplication.preheat(skGoodsId, preheatParam);
        return Response.ok();
    }

    @Operation(summary = "加载缓存内存", description = "加载缓存内存")
    @PutMapping("{skGoodsId}/loadCacheStock")
    public Response<String> loadCacheStock(@PathVariable Long skGoodsId) {
        skGoodsApplication.loadCacheStock(skGoodsId);
        return Response.ok();
    }
}
