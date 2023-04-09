package com.example.jseckill.application;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.jseckill.domain.user.repository.UserRepository;
import com.example.jseckill.interfaces.operate.query.UserQuery;
import com.example.jseckill.interfaces.operate.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.framework.pojo.PageInfo;
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

    public IPage<UserVO> page(PageInfo pageInfo, UserQuery query) {
        return userRepository.page(pageInfo, query);
    }
}
