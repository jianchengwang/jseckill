package com.example.jseckill.infrastructure.biz.user.db.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.jseckill.infrastructure.biz.user.db.po.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author jianchengwang
 * @date 2023/3/30
 */
@Mapper
public interface UserDao extends BaseMapper<User> {
    User getByUsername(String username);
}
