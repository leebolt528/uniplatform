package com.bonc.uni.common.service.user.imp;

import com.alibaba.fastjson.JSONObject;

import com.bonc.uni.common.dao.user.*;
import com.bonc.uni.common.entity.user.*;
import com.bonc.uni.common.entity.user.resultset.ISysUserGroupBySysGroup;
import com.bonc.uni.common.entity.user.resultset.ISysUserInfo;
import com.bonc.uni.common.service.user.SysUserService;
import com.bonc.uni.common.util.ConstantPool;
import com.bonc.uni.common.util.MapUtil;
import com.bonc.uni.common.util.ParamExtractorUtil;
import com.bonc.uni.common.util.ResultUtil;
import com.bonc.usdp.odk.common.detector.RegexUtil;
import com.bonc.usdp.odk.common.security.MD5Util;
import com.bonc.usdp.odk.common.string.StringUtil;
import com.bonc.usdp.odk.logmanager.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


@Service
public class SysUserServiceImpl implements SysUserService {

    private final SysUserRepository sysUserRepository;

    private final SysUserGroupRepository sysUserGroupRepository;

    private final SysUserRoleRepository sysUserRoleRepository;

    private final SysGroupRepository sysGroupRepository;

    private final SysRoleRepository sysRoleRepository;

    @Autowired
    public SysUserServiceImpl(
            SysUserRepository sysUserRepository,
            SysUserGroupRepository sysUserGroupRepository,
            SysUserRoleRepository sysUserRoleRepository,
            SysGroupRepository sysGroupRepository, SysRoleRepository sysRoleRepository
    ) {
        this.sysUserRepository = sysUserRepository;
        this.sysUserGroupRepository = sysUserGroupRepository;
        this.sysUserRoleRepository = sysUserRoleRepository;
        this.sysGroupRepository = sysGroupRepository;
        this.sysRoleRepository = sysRoleRepository;
    }

    @Override
    public String login(Map<String, Object> param, HttpSession session) {
        SysUser sysUser = (SysUser) session.getAttribute(ConstantPool.USER_LOGIN_INFO);
        if (sysUser != null) {
            return ResultUtil.error("您已登陆，请勿重复登陆", null);
        }

        if (param == null || param.isEmpty()) {
            return ResultUtil.error("登陆失败，输入登陆信息", null);
        }

        String account = (String) param.get("account");
        String passwd = (String) param.get("passwd");
        if (StringUtil.isEmpty(account) || StringUtil.isEmpty(passwd)) {
            return ResultUtil.error("登陆失败，输入登陆用户名及密码", null);
        }

        String md5Psswd = passwd;
        try {
            md5Psswd = MD5Util.md5(passwd);
        } catch (NoSuchAlgorithmException e) {
            LogManager.Exception(e);
        }
        SysUser loginSysUser = getSysUserByAccount(account);
        if (loginSysUser == null) {
            return ResultUtil.error("用户不存在，请注册", null);
        }

        if (!md5Psswd.equals(loginSysUser.getPasswd())) {
            return ResultUtil.error("登陆失败，用户名或密码不正确，请重新登陆", null);
        }

        switch (loginSysUser.getStatus()) {
            case OPEN:
                // 设置 session 过期时间为 8 小时
                session.setMaxInactiveInterval(8 * 60 * 60);
                session.setAttribute(ConstantPool.USER_LOGIN_INFO, loginSysUser);
                return ResultUtil.success(
                        "登陆成功",
                        MapUtil.newMap()
                                .put("userId", loginSysUser.getId())
                                .put("userName", loginSysUser.getUserName())
                                .build()
                );
            case LOCKED:
                return ResultUtil.error("登陆失败，账户已被锁定，请联系管理员", null);
            case EXPIRED:
                return ResultUtil.error("登陆失败，密码密码已过期，请联系管理员", null);
            case UNDEFINED:
            default:
                return ResultUtil.error("登陆失败，账户异常，请联系管理员", null);
        }
    }

    @Override
    public SysUser getSysUserByAccount(String account) {
        return sysUserRepository.getSysUserByAccountIs(account);
    }

    @Override
    public String addUser(Map<String, Object> param, SysUser operator) {
        LogManager.Method("service addUser begin");
        if (param == null || param.isEmpty()) {
            return ResultUtil.error("添加用户失败，请填写相关信息", null);
        }

        if (operator == null) {
            return ResultUtil.error("添加用户失败，请先登陆", null);
        }

        String account = (String) param.get("account");
        if (StringUtil.isEmpty(account)) {
            return ResultUtil.error("添加用户失败，请填写用户名", null);
        }

        if (getSysUserByAccount(account) != null) {
            return ResultUtil.error(
                    String.format("添加用户失败，该用户名( %s )已存在", account),
                    null
            );
        }
        String passwd = (String) param.get("passwd");
        if (StringUtil.isEmpty(passwd)) {
            return ResultUtil.error("添加用户失败，请填写密码", null);
        }
        String userName = (String) param.get("userName");
        if (userName == null) {
            return ResultUtil.error("添加用户失败，请填写用户姓名", null);
        }
        String gender = ((String) param.get("gender"));
        if (StringUtil.isEmpty(gender)) {
            if (!"男".equals(gender) && !"女".equals(gender)) {
                return ResultUtil.error("添加用户失败，性别请选择 '男' 或 '女'", null);
            }
            return ResultUtil.error("添加用户失败，请填写性别", null);
        }
        String email = (String) param.get("email");
        if (StringUtil.isEmpty(email)) {
            if (!RegexUtil.checkEmail(email)) {
                return ResultUtil.error("添加用户失败，请填写正确的邮箱地址", null);
            }
            return ResultUtil.error("添加用户失败，请填写邮箱", null);
        }
        String birthday = (String) param.get("birthday");
        if (StringUtil.isEmpty(birthday)) {
            if (!RegexUtil.checkDate(birthday)) {
                return ResultUtil.error("添加用户失败，请填写正确的生日", null);
            }
            return ResultUtil.error("添加用户失败，请填写生日", null);
        }
        String householdRegister = (String) param.get("householdRegister");
        if (StringUtil.isEmpty(householdRegister)) {
            return ResultUtil.error("添加用户失败，请填写户籍", null);
        }
        String position = (String) param.get("position");
        if (StringUtil.isEmpty(position)) {
            return ResultUtil.error("添加用户失败，请填写职位", null);
        }
        String phoneNumber = (String) param.get("phoneNumber");
        if (StringUtil.isEmpty(phoneNumber)) {
            return ResultUtil.error("添加用户失败，请填写手机号", null);
        }
        String groupId = (String) param.get("groupId");
        if (StringUtil.isEmpty(groupId)) {
            return ResultUtil.error("添加用户失败，请选择用户组", null);
        }
        if (!RegexUtil.checkInteger(groupId)) {
            return ResultUtil.error("添加用户失败，请提供正确的用户组唯一主键", null);
        }
        if (sysGroupRepository.findOne(Integer.parseInt(groupId)) == null) {
            return ResultUtil.error("添加用户失败，已提供的用户组不存在", null);
        }
        String roleId = (String) param.get("roleId");
        if (StringUtil.isEmpty(roleId)) {
            return ResultUtil.error("添加用户失败，请选择角色", null);
        }
        if (!RegexUtil.checkInteger(roleId)) {
            return ResultUtil.error("添加用户失败，请提供正确的角色唯一主键", null);
        }
        if (sysRoleRepository.findOne(Integer.parseInt(roleId)) == null) {
            return ResultUtil.error("添加用户失败，已提供的角色不存在", null);
        }

        SysUser sysUser = new SysUser();
        String md5Psswd = passwd;
        try {
            md5Psswd = MD5Util.md5(passwd);
        } catch (NoSuchAlgorithmException e) {
            LogManager.Exception(e);
        }
        sysUser.setAccount(account);
        sysUser.setPasswd(md5Psswd);
        sysUser.setUserName(userName);
        sysUser.setGender(gender);
        sysUser.setBirthday(birthday);
        sysUser.setHouseholdRegister(householdRegister);
        sysUser.setPosition(position);
        sysUser.setEmail(email);
        sysUser.setPhoneNumber(phoneNumber);
        sysUser.setCreatorId(operator.getId());
        sysUser.setCreateTime(new Date(new java.util.Date().getTime()));
        SysUser addedUser = sysUserRepository.save(sysUser);

        if (addedUser == null) {
            return ResultUtil.error("添加用户失败", null);
        }

        SysUserGroup sysUserGroup = new SysUserGroup();
        sysUserGroup.setGroupId(Integer.parseInt(groupId));
        sysUserGroup.setUserId(addedUser.getId());
        SysUserGroup addedSysUserGroup = sysUserGroupRepository.save(sysUserGroup);
        if (addedSysUserGroup == null) {
            sysUserRepository.delete(addedUser);
            LogManager.Event("failed to save SysUserGroup, the added SysUser were deleted");
            return ResultUtil.error("添加用户失败", null);
        }

        SysUserRole sysUserRole = new SysUserRole();
        sysUserRole.setRoleId(Integer.parseInt(roleId));
        sysUserRole.setUserId(addedUser.getId());
        SysUserRole addedSysUserRole = sysUserRoleRepository.save(sysUserRole);
        if (addedSysUserRole == null) {
            sysUserRepository.delete(addedUser);
            sysUserGroupRepository.delete(addedSysUserGroup);
            LogManager.Event("failed to save SysUserRole, the added SysUser and SysUserGroup were deleted");
            return ResultUtil.error("添加用户失败", null);
        }

        LogManager.Method("service addUser end");
        return ResultUtil.success(
                "添加用户成功",
                MapUtil.newMap()
                        .put("userId", addedUser.getId())
                        .put("userName", addedUser.getAccount())
                        .build()
        );
    }

    @Override
    public String updateUserInfo(Map<String, Object> param, SysUser operator) {
        LogManager.Method("service updateUserInfo begin");
        if (operator == null) {
            return ResultUtil.error("修改用户信息失败，请先登陆系统", null);
        }

        if (param == null || param.isEmpty()) {
            return ResultUtil.error("修改用户信息失败，请填写要修改的内容", null);
        }

        String idStr = (String) param.get("id");
        if (idStr == null) {
            return ResultUtil.error("修改用户信息失败，请传入用户唯一主键", null);
        }
        if (!RegexUtil.checkInteger(idStr)) {
            return ResultUtil.error("修改用户信息失败，请传入正确的用户唯一主键", null);
        }

        SysUser sysUser = sysUserRepository.findOne(Integer.parseInt(idStr));
        if (sysUser == null) {
            return ResultUtil.error("修改用户信息失败，不存在该用户", null);
        }

        String passwdStr = (String) param.get("passwdNew");
        String passwdOldStr = (String) param.get("passwdOld");
        if (passwdStr != null) {
            if (passwdOldStr == null) {
                return ResultUtil.error("修改密码失败，请输入原始密码", null);
            }
            try {
                if (!MD5Util.md5(passwdOldStr).equals(sysUser.getPasswd())) {
                    return ResultUtil.error("修改密码失败，输入的原始密码错误", null);
                }
                if (MD5Util.md5(passwdStr).equals(MD5Util.md5(passwdOldStr))) {
                    return ResultUtil.error(
                            "修改密码失败，新密码与原始密码一样，请输入不同的密码",
                            null
                    );
                }
                sysUser.setPasswd(MD5Util.md5(passwdStr));
            } catch (NoSuchAlgorithmException e) {
                LogManager.Exception(e);
            }
        }

        String emailStr = (String) param.get("email");
        if (emailStr != null) {
            if (!RegexUtil.checkEmail(emailStr)) {
                return ResultUtil.error("更新用户信息失败，请传入正确的邮箱地址", null);
            }
            sysUser.setEmail(emailStr);
        }
        String phoneNumberStr = (String) param.get("phoneNumber");
        if (phoneNumberStr != null) {
            sysUser.setPhoneNumber(phoneNumberStr);
        }
        String genderStr = (String) param.get("gender");
        if (genderStr != null) {
            if (!"男".equals(genderStr) && !"女".equals(genderStr)) {
                return ResultUtil.error("更新用户信息失败，性别请选择 '男' 或 '女'", null);
            }
            sysUser.setGender(genderStr);
        }
        String birthdayStr = (String) param.get("birthday");
        if (birthdayStr != null) {
            if (!RegexUtil.checkDate(birthdayStr)) {
                return ResultUtil.error("更新用户信息失败，请传入正确的生日", null);
            }
            sysUser.setBirthday(birthdayStr);
        }
        String householdRegisterStr = (String) param.get("householdRegister");
        if (householdRegisterStr != null) {
            sysUser.setHouseholdRegister(householdRegisterStr);
        }
        String positionStr = (String) param.get("position");
        if (positionStr != null) {
            sysUser.setPosition(positionStr);
        }

        SysUser updatedUser = sysUserRepository.save(sysUser);
        if (updatedUser == null) {
            return ResultUtil.error("更新用户信息失败", null);
        }

        LogManager.Method("service updateUserInfo end");
        return ResultUtil.success("更新用户信息成功", null);
    }

    @Override
    public String getAllSysUserInfo(Map<String, Object> param) {
        LogManager.Method("service getAllSysUserInfo begin");
        List<ISysUserInfo> iSysUserInfoList = null;
        if (param == null || param.isEmpty()) {
            iSysUserInfoList = sysUserRepository.getAllSysUserInfo();
        } else {
            String groupId = (String) param.get("groupId");
            if (groupId != null && RegexUtil.checkInteger(groupId)) {
                iSysUserInfoList = sysUserRepository.getSysUserInfoWithSysGroupId(Integer.parseInt(groupId));
            }
        }

        List<Map<String, Object>> sysUSerInfoList = new LinkedList<>();
        if (iSysUserInfoList != null) {
            for (ISysUserInfo iSysUserInfo : iSysUserInfoList) {
                Map<String, Object> sysUserInfoMap = new HashMap<>();
                sysUserInfoMap.put("sysUserId", iSysUserInfo.getSysUserId());
                sysUserInfoMap.put("sysUserAccount", iSysUserInfo.getSysUserAccount());
                sysUserInfoMap.put("sysUserEmail", iSysUserInfo.getSysUserEmail());
                sysUserInfoMap.put("sysUserPhoneNumber", iSysUserInfo.getSysUserPhoneNumber());
                sysUserInfoMap.put("sysUserCreatorId", iSysUserInfo.getSysUserCreatorId());
                sysUserInfoMap.put("sysGroupId", iSysUserInfo.getSysGroupId());
                sysUserInfoMap.put("sysGroupName", iSysUserInfo.getSysGroupName());
                sysUserInfoMap.put("sysRoleId", iSysUserInfo.getSysRoleId());
                sysUserInfoMap.put("sysRoleName", iSysUserInfo.getSysRoleName());
                sysUSerInfoList.add(sysUserInfoMap);
            }
        }

        LogManager.Method("service getAllSysUserInfo end");
        return JSONObject.toJSONString(sysUSerInfoList);
    }

    @Override
    public String updateGroupAndRole(Map<String, Object> param, SysUser operator) {
        LogManager.Method("service updateGroupAndRole begin");
        if (operator == null) {
            return ResultUtil.error("修改用户信息失败，请先登陆系统", null);
        }

        if (param == null || param.isEmpty()) {
            return ResultUtil.error("修改用户信息失败，请填写要修改的内容", null);
        }

        String userId = (String) param.get("userId");
        if (userId == null) {
            return ResultUtil.error("修改用户信息失败，请选择需要修改信息的用户", null);
        }
        String [] ids = userId.split(",");
        List<Integer> userIdList = new LinkedList<>();
        for (String id : ids) {
            if (id != null && RegexUtil.checkInteger(id)) {
                userIdList.add(Integer.parseInt(id));
            }
        }

        String groupId = (String) param.get("groupId");
        if (groupId != null && RegexUtil.checkInteger(groupId)) {
            if (sysGroupRepository.findOne(Integer.parseInt(groupId)) == null) {
                return ResultUtil.error("修改用户用户组信息失败，不存在该用户组", null);
            }
            List<SysUserGroup> sysUserGroupList = sysUserGroupRepository.findSysUserGroupsByUserIdIn(userIdList);
            for (SysUserGroup sysUserGroup : sysUserGroupList) {
                sysUserGroup.setGroupId(Integer.parseInt(groupId));
                sysUserGroupRepository.save(sysUserGroup);
            }
        }

        String roleId = (String) param.get("roleId");
        if (roleId != null && RegexUtil.checkInteger(roleId)) {
            if (sysRoleRepository.findOne(Integer.parseInt(roleId)) == null) {
                return ResultUtil.error("修改用户角色信息失败，不存在该角色", null);
            }
            List<SysUserRole> sysUserRolesList = sysUserRoleRepository.findSysUserRolesByUserIdIn(userIdList);
            for (SysUserRole sysUserRole : sysUserRolesList) {
                sysUserRole.setRoleId(Integer.parseInt(roleId));
                sysUserRoleRepository.save(sysUserRole);
            }
        }

        LogManager.Method("service updateGroupAndRole end");
        return ResultUtil.success("修改用户信息成功", null);
    }

    @Override
    public String deleteSysUser(Map<String, Object> param, SysUser operator) {
        LogManager.Method("service deleteSysUser begin");
        if (operator == null) {
            return ResultUtil.error("删除用户失败，请先登陆系统", null);
        }

        if (param == null || param.isEmpty()) {
            return ResultUtil.error("删除用户失败，请选择需要删除的用户", null);
        }

        String userId = (String) param.get("userId");
        if (userId == null || userId.length() == 0) {
            return ResultUtil.error("删除用户失败，请选择需要删除的用户", null);
        }

        List<String> userIdList = ParamExtractorUtil.extractParamToList(userId);
        try{
            for (String id : userIdList) {
                if (!RegexUtil.checkInteger(id)) {
                    continue;
                }
                sysUserRepository.delete(Integer.parseInt(id));
                sysUserGroupRepository.deleteSysUserGroupsByUserId(Integer.parseInt(id));
                sysUserRoleRepository.deleteSysUserRolesByUserId(Integer.parseInt(id));
            }
        } catch (Exception e) {
            LogManager.Exception(e);
            // 回滚事务
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }

        LogManager.Method("service deleteSysUser end");
        return ResultUtil.success("删除用户成功", null);
    }

    @Override
    public String deleteSysGroup(Map<String, Object> param, SysUser operator) {
        LogManager.Method("service deleteSysGroup begin");
        if (operator == null) {
            return ResultUtil.error("删除用户组失败，请先登陆系统", null);
        }

        if (param == null || param.isEmpty()) {
            return ResultUtil.error("删除用户组失败，请选择需要删除的用户组", null);
        }

        String groupId = (String) param.get("groupId");
        if (groupId == null || groupId.length() == 0) {
            return ResultUtil.error("删除用户组失败，请选择需要删除的用户组", null);
        }

        List<Integer> groupIdList = ParamExtractorUtil.convertStringListToIntegerList(
                ParamExtractorUtil.extractParamToList(groupId)
        );
        List<SysUserGroup> sysUserGroupList = sysUserGroupRepository.findSysUserGroupsByGroupIdIn(groupIdList);

        List<Integer> usedGroupIdList = new LinkedList<>();
        if (sysUserGroupList != null && !sysUserGroupList.isEmpty()) {
            for (SysUserGroup sysUserGroup : sysUserGroupList) {
                if (!usedGroupIdList.contains(sysUserGroup.getGroupId())) {
                    usedGroupIdList.add(sysUserGroup.getGroupId());
                }
            }
            groupIdList.removeAll(usedGroupIdList);
        }

        try{
            sysGroupRepository.deleteSysGroupsByIdIn(groupIdList);
        } catch (Exception e) {
            LogManager.Exception(e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultUtil.error("删除用户组失败", null);
        }
        if (usedGroupIdList.isEmpty()) {
            return ResultUtil.success("删除用户组成功", null);
        }

        Iterable<SysGroup> usedSysGroup = sysGroupRepository.findAll(usedGroupIdList);
        StringBuilder sb = new StringBuilder();
        sb.append("以下用户组由于正在使用中，未被删除: ");
        for (SysGroup sysGroup : usedSysGroup) {
            sb.append(sysGroup.getName()).append("、");
        }
        sb.deleteCharAt(sb.length() - 1);

        LogManager.Method("service deleteSysGroup end");
        return ResultUtil.success(sb.toString(), null);
    }

    @Override
    public String deleteSysRole(Map<String, Object> param, SysUser operator) {
        LogManager.Method("service deleteSysRole start");
        if (operator == null) {
            return ResultUtil.error("删除用户角色失败，请先登陆系统", null);
        }

        if (param == null || param.isEmpty()) {
            return ResultUtil.error("删除用户角色失败，请选择需要删除的用户角色", null);
        }

        String roleIds = (String) param.get("roleId");
        if (roleIds == null || roleIds.length() == 0) {
            return ResultUtil.error("删除用户角色失败，请选择需要删除的用户角色", null);
        }

        List<Integer> roleIdList = ParamExtractorUtil.convertStringListToIntegerList(
                ParamExtractorUtil.extractParamToList(roleIds)
        );
        List<SysUserRole> sysUserRolesList = sysUserRoleRepository.findSysUserRolesByRoleIdIn(roleIdList);
        List<Integer> usedSysRoleIdList = new LinkedList<>();
        if (sysUserRolesList != null && !sysUserRolesList.isEmpty()) {
            for (SysUserRole sysUserRole : sysUserRolesList) {
                if (!usedSysRoleIdList.contains(sysUserRole.getRoleId())) {
                    usedSysRoleIdList.add(sysUserRole.getRoleId());
                }
            }
            roleIdList.removeAll(usedSysRoleIdList);
        }

        try{
            sysRoleRepository.deleteSysRolesByIdIn(roleIdList);
        } catch (Exception e) {
//            LogManager.Exception(e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultUtil.error("删除用户角色失败", null);
        }

        if (usedSysRoleIdList.isEmpty()) {
            return ResultUtil.success("删除用户角色成功", null);
        }

        Iterable<SysRole> usedSysRoleList = sysRoleRepository.findAll(usedSysRoleIdList);
        StringBuilder sb = new StringBuilder();
        sb.append("以下用户角色由于正在使用中，未被删除: ");
        for (SysRole sysRole : usedSysRoleList) {
            sb.append(sysRole.getName()).append("、");
        }
        sb.deleteCharAt(sb.length() - 1);

        LogManager.Method("service deleteSysRole end");
        return ResultUtil.success(sb.toString(), null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public String getSysUserInfoGroupBySysGroup(SysUser operator) {
        LogManager.Method("service getSysUserInfoGroupBySysGroup begin");
        if (operator == null) {
            return ResultUtil.error("请先登陆系统", null);
        }
        int userId = operator.getId();
        Map<String, Map<String, Object>> helper = new HashMap<>();
        List<ISysUserGroupBySysGroup> sysUserInfoGroupBySysGroupList = sysUserRepository.getSysUserInfoGroupBySysGroup();
        for (ISysUserGroupBySysGroup iSysUserGroupBySysGroup : sysUserInfoGroupBySysGroupList) {
            if (iSysUserGroupBySysGroup.getSysUserId() == userId) {
                continue;
            }
            String groupName = iSysUserGroupBySysGroup.getSysGroupName();
            if (helper.containsKey(groupName)) {
                Map<String, Object> infoMap = helper.get(groupName);
                List<Map<String, String>> userList = (List<Map<String, String>>) infoMap.get("userList");
                Map<String, String> userInfo = new HashMap<>();
                userInfo.put("userName", iSysUserGroupBySysGroup.getSysUserName());
                userInfo.put("userId", String.valueOf(iSysUserGroupBySysGroup.getSysUserId()));
                userList.add(userInfo);
            } else {
                Map<String, Object> infoMap = new HashMap<>();
                infoMap.put("groupName", iSysUserGroupBySysGroup.getSysGroupName());
                infoMap.put("groupId", iSysUserGroupBySysGroup.getSysGroupId());
                List<Map<String, String>> users = new LinkedList<>();
                Map<String, String> userInfo = new HashMap<>();
                userInfo.put("userName", iSysUserGroupBySysGroup.getSysUserName());
                userInfo.put("userId", String.valueOf(iSysUserGroupBySysGroup.getSysUserId()));
                users.add(userInfo);
                infoMap.put("userList", users);
                helper.put(iSysUserGroupBySysGroup.getSysGroupName(), infoMap);
            }
        }

        LogManager.Method("service getSysUserInfoGroupBySysGroup end");
        return JSONObject.toJSONString(helper.values());
    }

    @Override
    public String getSysUserInfo(SysUser onlineUser) {
        LogManager.Method("service getSysUserInfo begin");
        if (onlineUser == null) {
            return ResultUtil.error("获取用户信息失败，请先登陆系统", null);
        }
        ISysUserInfo sysUserInfo = sysUserRepository.getSysUserInfoById(onlineUser.getId());

        Map<String, Object> userInfo = new HashMap<>();

        if (sysUserInfo != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date sysUserBirthday = sysUserInfo.getSysUserBirthday();
            if (sysUserBirthday == null) {
                sysUserBirthday = new Date(new java.util.Date().getTime());
            }

            userInfo.put("sysUserAccount", sysUserInfo.getSysUserAccount());
            userInfo.put("sysUserName", sysUserInfo.getSysUserName());
            userInfo.put("sysUserGender", sysUserInfo.getSysUserGender());
            userInfo.put("sysUserBirthday", sdf.format(sysUserBirthday));
            userInfo.put("sysUserHouseholdRegister", sysUserInfo.getSysUserHouseholdRegister());
            userInfo.put("sysUserPosition", sysUserInfo.getSysUserPosition());
            userInfo.put("sysUserEmail", sysUserInfo.getSysUserEmail());
            userInfo.put("sysUserPhoneNumber", sysUserInfo.getSysUserPhoneNumber());
            userInfo.put("sysGroupName", sysUserInfo.getSysGroupName());
            userInfo.put("sysRoleName", sysUserInfo.getSysRoleName());
        }

        LogManager.Method("service getSysUserInfo end");
        return JSONObject.toJSONString(userInfo);
    }

}
