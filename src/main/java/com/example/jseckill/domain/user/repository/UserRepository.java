package com.example.jseckill.domain.user.repository;

import com.example.jseckill.infrastructure.biz.user.db.po.User;

/**
 * @author jianchengwang
 * @date 2023/3/31
 */
public interface UserRepository {
    User findById(Long id);
    User findByUsername(String username);
}
