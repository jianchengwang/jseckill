package com.example.jseckill.infrastructure.common.converter;

import com.example.jseckill.infrastructure.user.db.po.User;
import com.example.jseckill.infrastructure.framework.config.permission.user.TokenUser;
import com.example.jseckill.interfaces.auth.vo.UserInfoVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author jianchengwang
 * @date 2023/3/31
 */
@Mapper
public interface UserConverter {
    UserConverter MAPPER = Mappers.getMapper( UserConverter.class );

    TokenUser toTokenUser(User user);

    UserInfoVO toUserInfoVO(TokenUser tokenUser);
}
