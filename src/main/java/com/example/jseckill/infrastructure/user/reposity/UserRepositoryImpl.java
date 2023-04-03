package com.example.jseckill.infrastructure.user.reposity;

import com.example.jseckill.domain.user.repository.UserRepository;
import com.example.jseckill.infrastructure.user.db.dao.UserDao;
import com.example.jseckill.infrastructure.user.db.po.User;
import com.example.jseckill.infrastructure.framework.exception.ClientException;
import com.example.jseckill.infrastructure.framework.exception.FrameworkErrorCode;
import com.example.jseckill.infrastructure.common.consts.CacheConstant;
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
            throw new ClientException("用户不存在", FrameworkErrorCode.RESOURCE_NOT_FOUND);
        }
        return user;
    }

    @Override
    public User findByUsername(String username) {
        return userDao.getByUsername(username);
    }
}
