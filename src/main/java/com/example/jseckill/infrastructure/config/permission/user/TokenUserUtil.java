package com.example.jseckill.infrastructure.config.permission.user;

import cn.dev33.satoken.stp.StpUtil;
import org.springframework.stereotype.Component;

/**
 * @author jianchengwang
 * @date 2023/3/31
 */
@Component
public class TokenUserUtil {
    public static Long currentUser() {
        try {
            return StpUtil.getLoginId(0L);
        } catch (Exception e) {
        }
        return 0L;
    }
}
