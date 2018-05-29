package com.bonc.uni.nlp.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.bonc.uni.common.entity.user.SysUser;
import com.bonc.uni.common.util.ConstantPool;


/**
 * 管理当前当前登录对象
 * @author zlq
 *
 */
public class CurrentUserUtils {
	
	private static CurrentUserUtils INSTANCE= null;
	
	private CurrentUserUtils(){
		
	}
	
	/**
	 * 获取实例
	 * @return
	 */
	public synchronized static CurrentUserUtils getInstance(){
		if(INSTANCE == null){
			synchronized (CurrentUserUtils.class) {
				if(INSTANCE == null) {
					INSTANCE = new CurrentUserUtils();
				}
			}
		}
		return INSTANCE;
	}
	
	/**
	 * 获取当前Request
	 * @return
	 */
	private HttpServletRequest getRequest(){
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();  
        return requestAttributes.getRequest();  
	}
	
	/**
	 * 获取当前Session
	 * @return
	 */
	private HttpSession getSession(){
		return getRequest().getSession(true);
	}
	
	/**
	 * 获取当前Session中的User
	 * @return
	 */
	public SysUser getUser(){
		return (SysUser) getSession().getAttribute(ConstantPool.USER_LOGIN_INFO);
	}
	
	public void setUser(SysUser user){
		getSession().setAttribute(ConstantPool.USER_LOGIN_INFO, user);
	}
	
	public void logoutUser(){
//		Enumeration<String> e = getSession().getAttributeNames();
//		while (e.hasMoreElements()) {
//			getSession().removeAttribute(e.nextElement().toString());
//		}
		
		getSession().removeAttribute(ConstantPool.USER_LOGIN_INFO);
		getSession().invalidate();
	}
	
}



