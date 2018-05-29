package com.bonc.uni.nlp.service.Impl.user;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bonc.uni.common.dao.user.SysUserRepository;
import com.bonc.uni.common.entity.user.SysUser;
import com.bonc.uni.common.util.ConstantPool;
import com.bonc.uni.common.util.MapUtil;
import com.bonc.uni.common.util.ResultUtil;
import com.bonc.uni.nlp.service.user.UserService;
import com.bonc.usdp.odk.logmanager.LogManager;


@Service
public class UserServiceImpl implements UserService {

    private final SysUserRepository sysUserRepository;

    @Autowired
    public UserServiceImpl(
            SysUserRepository sysUserRepository

    ) {
        this.sysUserRepository = sysUserRepository;

    }
	@Override
	public String login(String account, String password, HttpSession session) {
		LogManager.Process("Process in service: the method login of UserServiceImpl");
		long startTime = System.currentTimeMillis();

		
        SysUser sysUser = (SysUser) session.getAttribute(ConstantPool.USER_LOGIN_INFO);
        if (sysUser != null) {
            return ResultUtil.error("您已登陆，请勿重复登陆", null);
        }
        
//        String md5Psswd = passwd;
//        try {
//            md5Psswd = MD5Util.md5(passwd);
//        } catch (NoSuchAlgorithmException e) {
//            LogManager.Exception(e);
//        }
        
        SysUser loginSysUser = getSysUserByAccount(account);
        if (loginSysUser == null) {
    		LogManager.Process("Process out service: the method login of UserServiceImpl");
            return ResultUtil.error("用户不存在，请注册", null);
        }

        if (!password.equals(loginSysUser.getPasswd())) {
    		LogManager.Process("Process out service: the method login of UserServiceImpl");
            return ResultUtil.error("登陆失败，用户名或密码不正确，请重新登陆", null);
        }
        /**
         * 将登陆用户存放session
         */
        session.setMaxInactiveInterval(8 * 60 * 60);
        session.setAttribute(ConstantPool.USER_LOGIN_INFO, loginSysUser);
    	
		long endTime = System.currentTimeMillis();
		LogManager.info("登录 ：" + (endTime - startTime) + "ms");
		
		LogManager.Process("Process out service: the method login of UserServiceImpl");
		return ResultUtil.success("登陆成功", MapUtil.newMap().put("userId", loginSysUser.getId())
				.put("account", loginSysUser.getAccount()).build());
		
//        switch (loginSysUser.getStatus()) {
//            case OPEN:
//                // 设置 session 过期时间为 8 小时
//                session.setMaxInactiveInterval(8 * 60 * 60);
//                session.setAttribute(ConstantPool.USER_LOGIN_INFO, loginSysUser);
//            	LogManager.method("outoutouotuoutouto");
//                return ResultUtil.success(
//                        "登陆成功",
//                        MapUtil.newMap()
//                                .put("userId", loginSysUser.getId())
//                                .put("userName", loginSysUser.getUserName())
//                                .build()
//                );
//            case LOCKED:
//                return ResultUtil.error("登陆失败，账户已被锁定，请联系管理员", null);
//            case EXPIRED:
//                return ResultUtil.error("登陆失败，密码密码已过期，请联系管理员", null);
//            case UNDEFINED:
//            default:
//                return ResultUtil.error("登陆失败，账户异常，请联系管理员", null);
//        }
    }

	@Override
	public SysUser getSysUserByAccount(String account) {
        return sysUserRepository.getSysUserByAccountIs(account);
	}

	@Override
	public String addUser(Map<String, Object> param, SysUser operator) {
		// TODO Auto-generated method stub
		return null;
	}

}
