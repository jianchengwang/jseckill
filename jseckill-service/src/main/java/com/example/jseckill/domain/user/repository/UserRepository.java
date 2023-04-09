package com.example.jseckill.domain.user.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.jseckill.infrastructure.user.db.po.User;
import com.example.jseckill.interfaces.operate.query.UserQuery;
import com.example.jseckill.interfaces.operate.vo.UserVO;
import org.example.framework.pojo.PageInfo;

/**
 * @author jianchengwang
 * @date 2023/3/31
 */
public interface UserRepository extends IService<User> {

    IPage<UserVO> page(PageInfo pageInfo, UserQuery param);

    User findById(Long id);
    User findByUsername(String username);

    void generateUser(Long generateNum);
}
