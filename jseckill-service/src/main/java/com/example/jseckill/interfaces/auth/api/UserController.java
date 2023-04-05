package com.example.jseckill.interfaces.auth.api;

import cn.dev33.satoken.annotation.SaIgnore;
import com.example.jseckill.application.UserApplication;
import com.example.jseckill.infrastructure.framework.pojo.Response;
import com.example.jseckill.interfaces.auth.dto.LoginByUsernameDTO;
import com.example.jseckill.interfaces.auth.vo.UserInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @author jianchengwang
 * @date 2023/3/31
 */
@RestController
@RequestMapping("/api/auth/user")
@RequiredArgsConstructor
@Tag(name = "认证模块-用户模块")
public class UserController {

    private final UserApplication userApplication;

    @Operation(summary = "用户名密码登录", description = "用户名密码登录")
    @PostMapping("loginByUsername")
    @SaIgnore
    public Response<String> login(@Valid @RequestBody LoginByUsernameDTO loginParam) {
        return Response.ok(userApplication.login(loginParam.getUsername(), loginParam.getPassword()));
    }

    @Operation(summary = "退出登录", description = "退出登录")
    @GetMapping("logout")
    @SaIgnore
    public Response<Void> logout() {
        userApplication.logout();
        return Response.ok();
    }

    @Operation(summary = "用户信息", description = "用户信息")
    @GetMapping("info")
    public Response<UserInfoVO> userInfo() {
        return Response.ok(userApplication.getUserInfo());
    }
}
