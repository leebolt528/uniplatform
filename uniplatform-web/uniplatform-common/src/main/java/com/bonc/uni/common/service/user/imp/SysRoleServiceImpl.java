package com.bonc.uni.common.service.user.imp;

import com.bonc.uni.common.dao.user.SysRoleRepository;
import com.bonc.uni.common.entity.user.SysRole;
import com.bonc.uni.common.entity.user.SysUser;
import com.bonc.uni.common.service.user.SysRoleService;
import com.bonc.uni.common.util.MapUtil;
import com.bonc.uni.common.util.ResultUtil;
import com.bonc.usdp.odk.common.detector.RegexUtil;
import com.bonc.usdp.odk.common.string.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
import java.util.Map;


@Service
public class SysRoleServiceImpl implements SysRoleService {

    private final SysRoleRepository sysRoleRepository;

    @Autowired
    public SysRoleServiceImpl(SysRoleRepository sysRoleRepository) {
        this.sysRoleRepository = sysRoleRepository;
    }

    @Override
    public String addRole(Map<String, Object> param, SysUser operator) {
        if (operator == null) {
            return ResultUtil.error("添加角色失败，请先登陆", null);
        }

        String name = (String) param.get("name");
        if (StringUtil.isEmpty(name)) {
            return ResultUtil.error("添加角色失败，请先输入角色名", null);
        }

        if (getRoleByName(name) != null) {
            return ResultUtil.error(
                    String.format("添加角色失败，该角色名(%s)已存在", name),
                    null
            );
        }

        SysRole sysRole = new SysRole();
        sysRole.setName(name);
        sysRole.setCreateTime(new Date(new java.util.Date().getTime()));
        sysRole.setCreatorId(operator.getId());
        SysRole addedSysRole = sysRoleRepository.save(sysRole);
        if (addedSysRole == null) {
            return ResultUtil.error("添加角色失败，保存用户信息时出错", null);
        }

        return ResultUtil.success(
                "添加角色成功",
                MapUtil.newMap()
                        .put("id", addedSysRole.getId())
                        .put("name", addedSysRole.getName())
                        .build()
        );
    }

    @Override
    public SysRole getRoleByName(String roleName) {
        return sysRoleRepository.findSysRoleByName(roleName);
    }

    @Override
    public List<SysRole> getAll() {
        return sysRoleRepository.listIdAndName();
    }

    @Override
    public String updateRoleInfo(Map<String, Object> param, SysUser operator) {
        if (operator == null) {
            return ResultUtil.error("修改角色信息失败，请先登陆系统", null);
        }

        if (param == null) {
            return ResultUtil.error("修改角色信息失败，请填写要修改的内容", null);
        }

        String idStr = (String) param.get("id");
        if (idStr == null) {
            return ResultUtil.error("修改角色组信息失败，请传入角色唯一主键", null);
        }
        if (!RegexUtil.checkInteger(idStr)) {
            return ResultUtil.error("修改角色信息失败，请传入正确的角色唯一主键", null);
        }

        SysRole sysRole = sysRoleRepository.findOne(Integer.parseInt(idStr));
        if (sysRole == null) {
            return ResultUtil.error("修改角色信息失败，该角色不存在", null);
        }

        String additionalInfoStr = (String) param.get("additional_infoStr");
        if(additionalInfoStr != null) {
            sysRole.setAdditionalInfo(additionalInfoStr);
        }
        String nameStr = (String) param.get("nameStr");
        if(nameStr != null) {
            sysRole.setName(nameStr);
        }
        String permissionStr = (String) param.get("permissionStr");
        if(permissionStr != null) {
            sysRole.setPermission(permissionStr);
        }
        String reservedfileld1Str = (String) param.get("reservedfileld1Str");
        if(reservedfileld1Str != null) {
            sysRole.setReservedfileld1(reservedfileld1Str);
        }
        String reservedfileld2Str = (String) param.get("reservedfileld2Str");
        if(reservedfileld2Str != null) {
            sysRole.setReservedfileld2(reservedfileld2Str);
        }
        String reservedfileld3Str = (String) param.get("reservedfileld3Str");
        if(reservedfileld3Str != null) {
            sysRole.setReservedfileld3(reservedfileld3Str);
        }

        SysRole updatedSysRole = sysRoleRepository.save(sysRole);
        if (updatedSysRole == null) {
            return ResultUtil.error("更新角色信息失败", null);
        }

        return ResultUtil.error("更新角色信息成功", null);
    }

}
