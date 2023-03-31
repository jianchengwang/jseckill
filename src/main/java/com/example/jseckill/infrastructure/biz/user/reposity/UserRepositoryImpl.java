package com.example.jseckill.infrastructure.biz.user.reposity;

import com.example.jseckill.domain.user.repository.UserRepository;
import com.example.jseckill.infrastructure.biz.user.db.dao.UserDao;
import com.example.jseckill.infrastructure.biz.user.db.po.User;
import com.example.jseckill.infrastructure.common.exception.ClientException;
import com.example.jseckill.infrastructure.common.exception.FrameworkErrorCode;
import com.example.jseckill.infrastructure.consts.CacheConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @author jianchengwang
 * @date 2023/3/31
 */
@Service
@Slf4j
@RequiredArgsConstructor
@CacheConfig(cacheNames = CacheConstant.USER_CACHE_NAME)
public class UserRepositoryImpl implements UserRepository {

    private final UserDao userDao;

    @Override
    @Cacheable(key = "'" + CacheConstant.USER_CACHE_PREFIX + "' + #id", unless = "#result == null")
    public User findById(Long id) {
        User user = userDao.selectById(id);
        if(user == null) {
            throw new ClientException(FrameworkErrorCode.RESOURCE_NOT_FOUND, "用户不存在");
        }
        return user;
    }

    @Override
    public User findByUsername(String username) {
        return userDao.getByUsername(username);
    }
}
