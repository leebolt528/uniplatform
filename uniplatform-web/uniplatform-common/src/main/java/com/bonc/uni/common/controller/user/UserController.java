package com.bonc.uni.common.controller.user;

import com.alibaba.fastjson.JSONObject;
import com.bonc.uni.common.entity.user.SysGroup;
import com.bonc.uni.common.entity.user.SysRole;
import com.bonc.uni.common.entity.user.SysUser;
import com.bonc.uni.common.service.user.SysGroupService;
import com.bonc.uni.common.service.user.SysRoleService;
import com.bonc.uni.common.service.user.SysUserService;
import com.bonc.uni.common.util.ConstantPool;
import com.bonc.uni.common.util.MapUtil;
import com.bonc.uni.common.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/common/user")
public class UserController {

    private final SysUserService sysUserService;

    private final SysGroupService sysGroupService;

    private final SysRoleService sysRoleService;

    @Autowired
    public UserController(SysUserService sysUserService, SysGroupService sysGroupService, SysRoleService  sysRoleService ) {
        this.sysUserService = sysUserService;
        this.sysGroupService = sysGroupService;
        this.sysRoleService = sysRoleService;
    }

    /**
     * 用户登陆
     * @return 登陆结果信息
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@RequestParam Map<String, Object> param, HttpSession session) {
        return sysUserService.login(param, session);
    }
    
    /**
     * 用户登陆
     * @return 登陆结果信息
     */
    @RequestMapping(value = "/login/test", method = RequestMethod.POST)
    public String login(@RequestParam(value = "account", required = false, defaultValue = "") String account,
    		@RequestParam(value = "passwd", required = false, defaultValue = "") String passwd
    		, HttpSession session) {
    	Map<String, Object> param = new HashMap<String, Object>();
    	param.put("account", account);
    	param.put("passwd", passwd);
        return sysUserService.login(param, session);
    }

    /**
     * 注销登陆
     * @return 注销结果信息
     */
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public String logout(HttpSession session) {
        if (session.getAttribute(ConstantPool.USER_LOGIN_INFO) == null) {
            return ResultUtil.error("您没有登陆系统", null);
        }
        session.invalidate();
        return ResultUtil.success("您已成功退出系统", null);
    }

    /**
     * 检测登陆状态
     * @return 登陆状态信息
     */
    @RequestMapping(value = "/islogined", method = RequestMethod.POST)
    public String isLogined(HttpSession session) {
        SysUser sysUser = (SysUser) session.getAttribute(ConstantPool.USER_LOGIN_INFO);
        if (sysUser == null) {
            return ResultUtil.error("您未登陆系统", null);
        }
        return ResultUtil.success("您已登陆系统",
                MapUtil.newMap()
                        .put("userId", sysUser.getId())
                        .put("userName", sysUser.getAccount())
                        .build()
        );
    }

    /**
     * 添加用户
     * @return 添加结果信息
     */
    @RequestMapping(value = "/adduser", method = RequestMethod.POST)
    public String addUser(@RequestParam Map<String, Object> param, HttpSession session) {
        return sysUserService.addUser(
                param,
                (SysUser) session.getAttribute(ConstantPool.USER_LOGIN_INFO)
        );
    }

    /**
     * 查询用户名是否存在
     * @return 查询结果 true 不存在 / false 存在
     */
    @RequestMapping(value = "/checkusername", method = RequestMethod.POST)
    public String isUserNameExisted(@RequestParam Map<String, Object> param, HttpSession session) {
        if (param == null || param.isEmpty()) {
            return ResultUtil.error("参数为空", null);
        }
        if (session.getAttribute(ConstantPool.USER_LOGIN_INFO) == null) {
            return ResultUtil.error("您未登陆系统", null);
        }

        String account = (String) param.get("account");
        if (account == null) {
            return ResultUtil.error("请填写用户名", null);
        }

        if (sysUserService.getSysUserByAccount(account) != null) {
            return ResultUtil.error("用户名已存在", null);
        }

        return ResultUtil.success("用户名不存在，可使用", null);
    }

    /**
     * 添加用户组
     * @return 添加结果信息
     */
    @RequestMapping(value = "/addgroup", method = RequestMethod.POST)
    public String addSysGroup(@RequestParam Map<String, Object> param, HttpSession session) {
        return sysGroupService.addSysGroup(param, (SysUser) session.getAttribute(ConstantPool.USER_LOGIN_INFO));
    }

    /**
     * 修改用户信息
     * @return 修改结果信息
     */
    @RequestMapping(value = "/updateuser", method = RequestMethod.POST)
    public String updateUserInfo(@RequestParam Map<String, Object> param, HttpSession session) {
        return sysUserService.updateUserInfo(param, (SysUser) session.getAttribute(ConstantPool.USER_LOGIN_INFO));
    }

    /**
     * 修改用户组信息
     * @return 修改结果信息
     */
    @RequestMapping(value = "/updategroup", method = RequestMethod.POST)
    public String updateGroupInfo(@RequestParam Map<String, Object> param, HttpSession session) {
        return sysGroupService.updateGroupInfo(param, (SysUser) session.getAttribute(ConstantPool.USER_LOGIN_INFO));
    }

    /**
     * 添加角色
     * @return 添加结果信息
     */
    @RequestMapping(value = "/addrole", method = RequestMethod.POST)
    public String addSysRole(@RequestParam Map<String, Object> param, HttpSession session) {
        return sysRoleService.addRole(param, (SysUser) session.getAttribute(ConstantPool.USER_LOGIN_INFO));
    }

    /**
     * 修改角色信息
     * @return 修改结果信息
     */
    @RequestMapping(value = "/updaterole", method = RequestMethod.POST)
    public String updateRoleInfo(@RequestParam Map<String, Object> param, HttpSession session) {
        return sysRoleService.updateRoleInfo(param, (SysUser) session.getAttribute(ConstantPool.USER_LOGIN_INFO));
    }

    /**
     * 检查用户组名是否已存在
     * @return 查询结果 true 不存在 / false 存在
     */
    @RequestMapping(value = "/checkgroupname", method = RequestMethod.POST)
    public String isGroupNameExisted(@RequestParam("name") String name, HttpSession session) {
        if (session == null) {
            return ResultUtil.error("您未登陆系统", null);
        }

        if (name == null) {
            return ResultUtil.error("请填写用户组名", null);
        }

        if (sysGroupService.getGroupByName(name) != null) {
            return ResultUtil.error("用户组名已存在", null);
        }

        return ResultUtil.success("用户组名不存在，可使用", null);
    }

    /**
     * 检查角色名是否已存在
     * @return 查询结果 true 不存在 / false 存在
     */
    @RequestMapping(value = "/checkrolename", method = RequestMethod.POST)
    public String isRoleNameExisted(@RequestParam("name") String name, HttpSession session) {
        if (session == null) {
            return ResultUtil.error("您未登陆系统", null);
        }

        if (name == null) {
            return ResultUtil.error("请填写角色名", null);
        }

        if (sysRoleService.getRoleByName(name) != null) {
            return ResultUtil.error("角色名已存在", null);
        }

        return ResultUtil.success("角色名不存在，可使用", null);
    }

    /**
     * 获取所有角色信息
     * @return 所有角色信息
     */
    @RequestMapping(value = "/rolelist", method = RequestMethod.GET)
    public String getAllSysRoleInfo() {
        List<Map<String, Object>> result = new LinkedList<>();
        for (SysRole sysRole : sysRoleService.getAll()) {
            result.add(
                    MapUtil.newMap()
                            .put("displayName", sysRole.getName())
                            .put("systemName", sysRole.getId())
                            .build()
            );
        }
        return JSONObject.toJSONString(result);
    }

    /**
     * 获取所有用户组信息
     * @return 所有用户组信息
     */
    @RequestMapping(value = "/grouplist", method = RequestMethod.GET)
    public String getAllSysGroupInfo() {
        List<Map<String, Object>> result = new LinkedList<>();
        for (SysGroup sysGroup : sysGroupService.getAll()) {
            result.add(
                    MapUtil.newMap()
                            .put("displayName", sysGroup.getName())
                            .put("systemName", sysGroup.getId())
                            .build()
            );
        }
        return JSONObject.toJSONString(result);
    }

    /**
     * 获取所有用户信息
     * @return 所有用户信息
     */
    @RequestMapping(value = "/userlist", method = RequestMethod.POST)
    public String getAllSysUserInfo(@RequestParam Map<String, Object> param) {
        return sysUserService.getAllSysUserInfo(param);
    }
    
    /**获取采集组用户
     * @return
     */
    @RequestMapping(value = "/userlist/crawler", method = RequestMethod.POST)
    public String getAllSysUserInfoCrawler() {
    	Map<String, Object> param = new HashMap<String,Object>();
    	SysGroup sysGroup = sysGroupService.getGroupByName("crawler");
    	if(null != sysGroup) {
    		param.put("groupId", sysGroup.getId().toString());
    		return sysUserService.getAllSysUserInfo(param);
    	}
    	return null;
    }

    /**
     * 更新用户的用户组及角色信息
     * @return 更新结果信息
     */
    @RequestMapping(value = "/updategrouprole", method = RequestMethod.POST)
    public String updateGroupAndRole(@RequestParam Map<String, Object> param, HttpSession session) {
        return sysUserService.updateGroupAndRole(param, (SysUser) session.getAttribute(ConstantPool.USER_LOGIN_INFO));
    }

    /**
     * 删除用户
     * @return 删除结果信息
     */
    @RequestMapping(value = "/userdelete", method = RequestMethod.POST)
    public String deteleUser(@RequestParam Map<String, Object> param, HttpSession session) {
        return sysUserService.deleteSysUser(param, (SysUser) session.getAttribute(ConstantPool.USER_LOGIN_INFO));
    }

    /**
     * 删除用户组
     * @return 删除结果信息
     */
    @RequestMapping(value = "/group/delete", method = RequestMethod.POST)
    public String deleteSysGroup(@RequestParam Map<String, Object> param, HttpSession session) {
        return sysUserService.deleteSysGroup(param, (SysUser) session.getAttribute(ConstantPool.USER_LOGIN_INFO));
    }

    @RequestMapping(value = "/role/delete", method = RequestMethod.POST)
    public String deleteSysRole(@RequestParam Map<String, Object> param, HttpSession session) {
        return sysUserService.deleteSysRole(param, (SysUser) session.getAttribute(ConstantPool.USER_LOGIN_INFO));
    }

    @RequestMapping(value = "/group/user", method = RequestMethod.GET)
    public String getUsersByGroup(HttpSession session) {
        return sysUserService.getSysUserInfoGroupBySysGroup((SysUser) session.getAttribute(ConstantPool.USER_LOGIN_INFO));
    }

    @RequestMapping(value = "/user/info", method = RequestMethod.GET)
    public String getSysUserInfo(HttpSession session) {
        return sysUserService.getSysUserInfo((SysUser) session.getAttribute(ConstantPool.USER_LOGIN_INFO));
    }

}
