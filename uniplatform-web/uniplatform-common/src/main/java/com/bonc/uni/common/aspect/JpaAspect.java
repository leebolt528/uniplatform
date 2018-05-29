package com.bonc.uni.common.aspect;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.bonc.uni.common.entity.EntityCommon;
import com.bonc.uni.common.entity.user.SysUser;
import com.bonc.uni.common.util.ConstantPool;

/**
 * jpa操作的切面
 * @author futao
 * 2017年8月28日
 */
@Component
@Aspect
public class JpaAspect {

	
	/*@Pointcut("execution(* com.bonc.uni.dcci..*(..)) and @annotation(org.springframework.web.bind.annotation.RequestMapping)")  
    public void controllerMethodPointcut(){}  
	
	@Around("controllerMethodPointcut()")
	public Object test(ProceedingJoinPoint joinPoint) throws Throwable {
		
        SysUser sysUser = getUser();
        System.out.println(sysUser);
		return joinPoint.proceed();
	}*/
	
	/**
	 * 添加save
	 * @param joinPoint
	 * @return
	 * @throws Throwable
	 */
	@Around("execution(* javax.persistence.EntityManager.persist(..))")
	public Object persist(ProceedingJoinPoint joinPoint) throws Throwable {
		Object[] args = joinPoint.getArgs();
		Object entity = args[0];
		if (entity instanceof EntityCommon) {
			EntityCommon entityObject = (EntityCommon) entity;
			/*SysUser sysUser = getUser();
			entityObject.setCreatedUser(sysUser.getAccount());*/
			entityObject.setCreatedUser("test-user");
			entityObject.setCreatedTime(System.currentTimeMillis());
		}
		return joinPoint.proceed(args);
	}
	
	
	/**
	 * 修改 update
	 * @param joinPoint
	 * @return
	 * @throws Throwable
	 */
	@Around("execution(* javax.persistence.EntityManager.merge(..))")
	public Object merge(ProceedingJoinPoint joinPoint) throws Throwable {
		Object[] args = joinPoint.getArgs();
		Object entity = args[0];
		if (entity instanceof EntityCommon) {
			/*SysUser sysUser = getUser();
			entityObject.setCreatedUser(sysUser.getAccount());*/
			EntityCommon entityObject = (EntityCommon) entity;
			entityObject.setLastModifiedUser("test-user");
			entityObject.setLastModifiedTime(System.currentTimeMillis());
		}
		return joinPoint.proceed(args);
	}
	
	public SysUser getUser() {
		RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        HttpServletRequest request = sra.getRequest();
        HttpSession session = request.getSession();
        SysUser sysUser = (SysUser) session.getAttribute(ConstantPool.USER_LOGIN_INFO);
        return sysUser;
	}
}
