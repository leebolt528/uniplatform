package com.bonc.uni.common.service.user.imp;


import com.bonc.uni.common.dao.user.SysGroupRepository;
import com.bonc.uni.common.entity.user.SysGroup;
import com.bonc.uni.common.entity.user.SysUser;
import com.bonc.uni.common.service.user.SysGroupService;
import com.bonc.uni.common.util.MapUtil;
import com.bonc.uni.common.util.ResultUtil;
import com.bonc.usdp.odk.common.detector.RegexUtil;
import com.bonc.usdp.odk.common.string.StringUtil;
import com.bonc.usdp.odk.logmanager.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
import java.util.Map;


@Service
public class SysGroupServiceImpl implements SysGroupService {

    private SysGroupRepository sysGroupRepository;

    @Autowired
    public SysGroupServiceImpl(SysGroupRepository sysGroupRepository) {
        this.sysGroupRepository = sysGroupRepository;
    }

    @Override
    public String addSysGroup(Map<String, Object> param, SysUser operator) {
        if (param == null || param.isEmpty()) {
            return ResultUtil.error("添加用户组失败，请填写相关信息", null);
        }

        if (operator == null) {
            return ResultUtil.error("添加用户组失败，请先登陆", null);
        }

        String name = (String) param.get("name");
        if (StringUtil.isEmpty(name)) {
            return ResultUtil.error("添加用户组失败，请填写名称", null);
        }
        if (sysGroupRepository.findSysGroupByName(name) != null) {
            return ResultUtil.error("添加用户组失败，用户组名已存在", null);
        }
        String code = (String) param.get("code");
//        if (StringUtil.isEmpty(code)) {
//            return ResultUtil.error("添加用户组失败，请填写代码", null);
//        }
        String permission = (String) param.get("permission");
//        if (StringUtil.isEmpty(permission)) {
//            return ResultUtil.error("添加用户组失败，请填写权限", null);
//        }

        SysGroup sysGroup = new SysGroup();
        sysGroup.setName(name);
        sysGroup.setCode(code);
        sysGroup.setPermission(permission);
        sysGroup.setCreateTime(new Date(new java.util.Date().getTime()));
        sysGroup.setCreatorId(operator.getId());
        SysGroup addedSysGroup = null;

        try{
            addedSysGroup = sysGroupRepository.save(sysGroup);
        } catch (Exception e) {
            LogManager.Exception(e);
        }

        if (addedSysGroup == null) {
            return ResultUtil.error("添加用户组失败", null);
        }

        return ResultUtil.success(
                "添加用户组成功",
                MapUtil.newMap()
                        .put("groupId", addedSysGroup.getId())
                        .put("groupName", addedSysGroup.getName())
                        .build()
        );
    }

    @Override
    public SysGroup getGroupByName(String groupName) {
        return sysGroupRepository.findSysGroupByName(groupName);
    }

    @Override
    public String updateGroupInfo(Map<String, Object> param, SysUser operator) {
        if (operator == null) {
            return ResultUtil.error("修改用户组信息失败，请先登陆系统", null);
        }

        if (param == null || param.isEmpty()) {
            return ResultUtil.error("修改用户组信息失败，请填写要修改的内容", null);
        }

        String idStr = (String) param.get("id");
        if (idStr == null) {
            return ResultUtil.error("修改用户组信息失败，请传入用户组唯一主键", null);
        }
        if (!RegexUtil.checkInteger(idStr)) {
            return ResultUtil.error("修改用户组信息失败，请传入正确的用户组唯一主键", null);
        }

        SysGroup sysGroup = sysGroupRepository.findOne(Integer.parseInt(idStr));
        if (sysGroup == null) {
            return ResultUtil.error("修改用户组信息失败，不存在该用户组", null);
        }

        String nameStr = (String) param.get("name");
        if (nameStr != null) {
            sysGroup.setName(nameStr);
        }
        String codeStr = (String) param.get("code");
        if (codeStr != null) {
            sysGroup.setCode(codeStr);
        }
        String permissionStr = (String) param.get("permission");
        if (permissionStr != null) {
            sysGroup.setPermission(permissionStr);
        }

        SysGroup updatedSysGroup = sysGroupRepository.save(sysGroup);
        if (updatedSysGroup == null) {
            return ResultUtil.error("更新用户组信息失败", null);
        }

        return ResultUtil.success("更新用户组信息成功", null);
    }

    @Override
    public List<SysGroup> getAll() {
        return sysGroupRepository.listIdAndName();
    }

}
