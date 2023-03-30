package com.example.jseckill.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jianchengwang
 * @date 2023/3/30
 */
@RestController
@RequestMapping("/api/seckill")
@RequiredArgsConstructor
@Tag(name = "秒杀模块")
public class SeckillController {

}
