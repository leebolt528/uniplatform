package com.bonc.uni.common.service.user;

import com.bonc.uni.common.entity.user.SysUser;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.Map;

public interface SysUserService {

    /**
     * 用户登陆
     * @param param 登陆参数
     * @return 登陆用户信息
     */
    String login(Map<String, Object> param, HttpSession session);

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

    /**
     * 更新用户数据
     * @param param 需要更新的信息
     * @param operator 操作人
     * @return 更新结果
     */
    @Transactional
    String updateUserInfo(Map <String, Object> param, SysUser operator);

    /**
     * 获取所有用户信息
     * @param param 参数
     * @return 所有用户信息
     */
    String getAllSysUserInfo(Map <String, Object> param);

    /**
     * 更新用户组及角色信息
     * @param param 参数
     * @param operator 操作者
     * @return 更新结果信息
     */
    @Transactional
    String updateGroupAndRole(Map <String, Object> param, SysUser operator);

    /**
     * 删除用户
     * @param param 参数
     * @param operator 操作者
     * @return 删除结果
     */
    @Transactional
    String deleteSysUser(Map <String, Object> param, SysUser operator);

    /**
     * 删除用户组
     * @param param 参数
     * @param operator 操作者
     * @return 删除结果
     */
    @Transactional
    String deleteSysGroup(Map <String, Object> param, SysUser operator);

    /**
     * 删除角色
     * @param param 参数
     * @param operator 操作者
     * @return 删除结果
     */
    @Transactional
    String deleteSysRole(Map <String, Object> param, SysUser operator);

    /**
     * 获取用户，按用户组分组
     * @return 所有用户
     */
    String getSysUserInfoGroupBySysGroup(SysUser operator);

    /**
     * 获取用户信息
     * @param onlineUser 当前在线用户
     * @return 当前用户信息
     */
    String getSysUserInfo(SysUser onlineUser);
}
