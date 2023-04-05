package com.example.jseckill.infrastructure.user.reposity;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.jseckill.domain.user.repository.UserRepository;
import com.example.jseckill.infrastructure.common.enums.UserScopeEnum;
import com.example.jseckill.infrastructure.common.enums.UserStatusEnum;
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

import java.util.ArrayList;
import java.util.List;

/**
 * @author jianchengwang
 * @date 2023/3/31
 */
@Service
@Slf4j
@RequiredArgsConstructor
@CacheConfig(cacheNames = CacheConstant.USER_CACHE_NAME)
public class UserRepositoryImpl extends ServiceImpl<UserDao, User> implements UserRepository {

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

    @Override
    public void generateUser(Long generateNum) {
        String defaultPassword = "123456";
        String passwordSalt = UUID.fastUUID().toString(true);
        String password = SaSecureUtil.md5BySalt(defaultPassword, passwordSalt);
        List<User> insertList = new ArrayList<>();
        for (Long i = 0L; i < generateNum; i++) {
            User insertObj = new User();
            insertObj.setUsername("test" + i);
            insertObj.setPasswordSalt(passwordSalt);
            insertObj.setPassword(password);
            insertObj.setNickname("test" + i);
            insertObj.setMobile(String.format("139%08d", i));
            insertObj.setUserScope(UserScopeEnum.CLIENT);
            insertObj.setUserStatus(UserStatusEnum.NORMAL);
            insertList.add(insertObj);
        }
        this.saveBatch(insertList);
    }
}
