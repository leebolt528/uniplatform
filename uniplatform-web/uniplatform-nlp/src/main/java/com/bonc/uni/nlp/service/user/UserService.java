package com.bonc.uni.nlp.service.user;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.transaction.annotation.Transactional;

import com.bonc.uni.common.entity.user.SysUser;

public interface UserService {

    /**
     * 用户登陆
     * @param param 登陆参数
     * @return 登陆用户信息
     */
    String login(String account, String password, HttpSession session);
    
    /**
     * 根据账户获取用户
     * @param account 账户
     * @return 查找到的用户
     */
    SysUser getSysUserByAccount(String account);
    
    /**
     * 添加用户
     * @param param 新增用户参数
     * @param operator 创建者
     * @return 新建用户结果
     */
    @Transactional
    String addUser(Map <String, Object> param, SysUser operator);
}
