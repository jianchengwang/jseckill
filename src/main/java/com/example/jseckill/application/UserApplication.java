package com.example.jseckill.application;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.example.jseckill.domain.user.repository.UserRepository;
import com.example.jseckill.infrastructure.biz.user.db.po.User;
import com.example.jseckill.infrastructure.common.exception.ClientException;
import com.example.jseckill.infrastructure.common.exception.FrameworkErrorCode;
import com.example.jseckill.infrastructure.config.permission.user.TokenUser;
import com.example.jseckill.infrastructure.config.permission.user.TokenUserContextHolder;
import com.example.jseckill.infrastructure.converter.UserConverter;
import com.example.jseckill.infrastructure.enums.UserStatusEnum;
import com.example.jseckill.infrastructure.errors.AuthErrorCode;
import com.example.jseckill.interfaces.client.vo.UserInfoVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author jianchengwang
 * @date 2023/3/31
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserApplication {

    private final UserRepository userRepository;

    public String login(String username, String password) {
        User user = userRepository.findByUsername(username);
        if(user == null){
            throw new ClientException(FrameworkErrorCode.RESOURCE_NOT_FOUND, "用户不存在");
        }
        String checkPassword = SaSecureUtil.md5BySalt(password, user.getPasswordSalt());
        if(!user.getPassword().equals(checkPassword)) {
            throw new ClientException(AuthErrorCode.USER_PASSWORD_ERROR);
        }
        if(UserStatusEnum.NORMAL != user.getUserStatus()){
            throw new ClientException(AuthErrorCode.USER_NOT_NORMAL);
        }

        // 登录，用户编号作为loginId
        StpUtil.login(user.getId());
        SaTokenInfo token = StpUtil.getTokenInfo();

        // 放到用户上下文
        TokenUser tokenUser = UserConverter.MAPPER.toTokenUser(user);
        TokenUserContextHolder.setCurrentUser(tokenUser);

        return token.getTokenValue();
    }

    public void logout() {
        StpUtil.logout();
    }

    public UserInfoVO getUserInfo() {
        TokenUser tokenUser = TokenUserContextHolder.currentUser();
        return UserConverter.MAPPER.toUserInfoVO(tokenUser);
    }
}
