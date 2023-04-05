package com.example.jseckill.domain.user.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.jseckill.infrastructure.user.db.po.User;

/**
 * @author jianchengwang
 * @date 2023/3/31
 */
public interface UserRepository extends IService<User> {
    User findById(Long id);
    User findByUsername(String username);

    void generateUser(Long generateNum);
}
