package com.example.jseckill.application;

import com.example.jseckill.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * @author jianchengwang
 * @date 2023/4/5
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TestApplication {
    private final UserRepository userRepository;
    private final RedissonClient redissonClient;
    private final JdbcTemplate jdbcTemplate;

    public void generateUser(Long num) {
        userRepository.generateUser(num);
    }

    public void clearData() {
        jdbcTemplate.execute("""
            truncate table t_sk_pay_notify;
            truncate table t_sk_order;
            truncate table t_sk_goods;
        """);
        redissonClient.getKeys().flushdb();
    }
}
