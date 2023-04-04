package com.example.jseckill.interfaces.operate.api;

import com.example.jseckill.domain.user.repository.UserRepository;
import com.example.jseckill.infrastructure.framework.pojo.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jianchengwang
 * @date 2023/4/4
 */
@RestController
@RequestMapping("/api/operate/test")
@RequiredArgsConstructor
@Tag(name = "运营端-测试数据")
public class TestController {

    private final UserRepository userRepository;

    @Operation(summary = "生成测试用户", description = "生成测试用户")
    @PostMapping("generateUser")
    public Response<Void> generateUser(Long num) {
        userRepository.generateUser(num);
        return Response.ok();
    }

}
