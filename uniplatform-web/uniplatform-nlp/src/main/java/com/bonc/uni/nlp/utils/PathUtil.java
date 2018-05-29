package com.bonc.uni.nlp.utils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.bonc.usdp.odk.common.exception.PathNotFoundException;
import com.bonc.usdp.odk.logmanager.LogManager;
/**
 * @ClassName: PathUtil
 * @Package: com.bonc.uni.nlp.utils
 * @Description: 系统路径类
 * @author: Chris
 * @date: 2017年11月1日 下午9:12:42
 */
public class PathUtil {
	
	private final static String NLAP_RESOURCE_HOME = "BONC_UNI_NLAP_RESOURCE";
	
	private final static String NLAP_CONFIG_HOME = "BONC_UNI_NLAP_CONFIG";

	private PathUtil() {
	}

	/**
	 * <p>Title: getResourcesPath</p>
	 * <p>Description: 获取系统资源路径</p>
	 * @return
	 * @throws PathNotFoundException 
	 */
	public static String getResourcesPath() throws PathNotFoundException {
		String basePath;
		/**
		 * 获取环境变量指定的路径
		 */
		String boncHome = System.getenv(NLAP_RESOURCE_HOME);
		LogManager.info("nlap resource home " + boncHome);
		if (null == boncHome || boncHome.isEmpty()) {
			basePath = PathUtil.class.getClassLoader().getResource("")
					.getPath();
		} else {
			basePath = boncHome;
		}
		LogManager.info("nlap resource path " + basePath);
		try {
			basePath = URLDecoder.decode(basePath, "utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new PathNotFoundException("Can't decode url using utf-8", e);
		}
		File f = new File(basePath);
		basePath = f.getAbsolutePath(); // 得到windows下的正确路径
		
		return basePath;
	}
	
	/**
	 * <p>Title: getConfigPath</p>
	 * <p>Description: 获取配置文件存放目录</p>
	 * @return
	 * @throws PathNotFoundException
	 */
	public static String getConfigPath() throws PathNotFoundException {
		String configPath;
		/**
		 * 获取环境变量指定的路径
		 */
		String boncHome = System.getenv(NLAP_CONFIG_HOME);
		if (null == boncHome || boncHome.isEmpty()) {
			configPath = PathUtil.class.getClassLoader().getResource("")
					.getPath();
		} else {
			configPath = boncHome;
		}
		try {
			configPath = URLDecoder.decode(configPath, "utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new PathNotFoundException("Can't decode url using utf-8", e);
		}
		File f = new File(configPath);
		configPath = f.getAbsolutePath(); // 得到windows下的正确路径
		
		return configPath;
	}
}
