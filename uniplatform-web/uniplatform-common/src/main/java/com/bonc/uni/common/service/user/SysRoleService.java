package com.bonc.uni.common.service.user;


import com.bonc.uni.common.entity.user.SysRole;
import com.bonc.uni.common.entity.user.SysUser;

import java.util.List;
import java.util.Map;

public interface SysRoleService {

    /**
     * 添加角色
     * @param param 参数
     * @param operator 创建人
     * @return 添加结果信息
     */
    String addRole(Map <String, Object> param, SysUser operator);

    /**
     * 通过角色名获取角色信息
     * @param roleName 角色名
     * @return 角色信息
     */
    SysRole getRoleByName(String roleName);

    /**
     * 获取所有角色信息
     * @return 所有角色信息
     */
    List<SysRole> getAll();

    /**
     * 更新角色信息
     * @param param 参数
     * @param operator 操作者
     * @return 更新结果
     */
    String updateRoleInfo(Map <String, Object> param, SysUser operator);

}
