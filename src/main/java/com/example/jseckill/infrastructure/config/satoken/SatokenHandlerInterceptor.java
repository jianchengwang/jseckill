package com.example.jseckill.infrastructure.config.satoken;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.strategy.SaStrategy;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;

/**
 * @author jianchengwang
 * @date 2023/3/31
 */
public class SatokenHandlerInterceptor extends SaInterceptor {

    public SatokenHandlerInterceptor() {
        super();
        this.auth = handler -> {
            SaRouter.match("/api/**", "", r -> StpUtil.checkLogin());
        };
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            if(handler instanceof HandlerMethod) {
                if (this.isAnnotation) {
                    Method method = ((HandlerMethod)handler).getMethod();
                    if (SaStrategy.me.isAnnotationPresent.apply(method, SaIgnore.class)) {
                        return true;
                    }
                    SaStrategy.me.checkMethodAnnotation.accept(method);
                }
                this.auth.run(handler);
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}

