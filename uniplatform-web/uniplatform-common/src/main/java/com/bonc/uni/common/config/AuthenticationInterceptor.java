package com.bonc.uni.common.config;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.bonc.uni.common.annotation.LoginAuth;
import com.bonc.uni.common.entity.user.SysUser;
import com.bonc.uni.common.util.ConstantPool;

public class AuthenticationInterceptor implements HandlerInterceptor{

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// TODO Auto-generated method stub
		if (!(handler instanceof HandlerMethod)) {
            return true;
        }
		HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
		
        LoginAuth loginAuth = method.getAnnotation(LoginAuth.class);
        if(null != loginAuth) {
            HttpSession session = request.getSession();
            SysUser sysUser = (SysUser) session.getAttribute(ConstantPool.USER_LOGIN_INFO);
            if (null == sysUser) {
                throw new RuntimeException("请重新登录");
            }
            
        }
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

}
