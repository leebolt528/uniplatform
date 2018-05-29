package com.bonc.uni.nlp.controller.user;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bonc.uni.common.entity.user.SysUser;
import com.bonc.uni.common.util.MapUtil;
import com.bonc.uni.common.util.ResultUtil;
import com.bonc.uni.nlp.service.user.UserService;
import com.bonc.uni.nlp.utils.CurrentUserUtils;
import com.bonc.usdp.odk.common.string.StringUtil;
import com.bonc.usdp.odk.logmanager.LogManager;

@RestController
@CrossOrigin
@RequestMapping(value = "/nlap/admin/user")
public class UserLogController {

    private final UserService userService;

    @Autowired
    public UserLogController(UserService userService) {
        this.userService = userService;
    }
    
    /**
     * 用户登陆
     * @return 登陆结果信息
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@RequestParam(value = "account" ) String account,
    		@RequestParam(value = "passwd") String passwd,
    		HttpSession session) {
    	
        if (StringUtil.isEmpty(account) || StringUtil.isEmpty(passwd)) {
    		LogManager.Process("Process out service: the method login of UserServiceImpl");
            return ResultUtil.error("登陆失败，输入登陆用户名及密码", null);
        }
        
        return userService.login(account, passwd, session);
    }
    
    /**
     * 注销登陆
     * @return 注销结果信息
     */
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public String logout() {
        CurrentUserUtils.getInstance().logoutUser();
        return ResultUtil.success("您已成功退出系统", null);
    }
    
    /**
     * 登录状态
     * @return 登录信息
     */
    @RequestMapping(value = "/loginfo", method = RequestMethod.POST)
    public String loginfo() {
        if (null == CurrentUserUtils.getInstance().getUser()) {
    		return ResultUtil.error("非登录状态", null);  
		} else {
			SysUser loginSysUser = CurrentUserUtils.getInstance().getUser();
			return ResultUtil.success("登录状态", MapUtil.newMap().put("userId", loginSysUser.getId())
					.put("userName", loginSysUser.getAccount()).build());
		}
    }

}
