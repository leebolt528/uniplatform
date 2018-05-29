package com.bonc.uni.common.service.user;


import com.bonc.uni.common.entity.user.SysGroup;
import com.bonc.uni.common.entity.user.SysUser;

import java.util.List;
import java.util.Map;

public interface SysGroupService {

    /**
     * 添加用户组
     * @param param 参数
     * @param operator 创建者
     * @return 新建用户组结果信息
     */
    String addSysGroup(Map <String, Object> param, SysUser operator);

    /**
     * 根据用户组名获取用户组信息
     * @param groupName 用户组名
     * @return 用户组信息
     */
    SysGroup getGroupByName(String groupName);

    /**
     * 修改用户组信息
     * @param param 参数
     * @param operator 操作人
     * @return 修改结果信息
     */
    String updateGroupInfo(Map <String, Object> param, SysUser operator);

    /**
     * 获取所有用户组信息
     * @return 所有用户组信息
     */
    List<SysGroup> getAll();

}
