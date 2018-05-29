package com.bonc.uni.nlp.utils;

import java.io.File;

import com.bonc.uni.nlp.constant.SystemConstant;
import com.bonc.usdp.odk.common.exception.PathNotFoundException;
import com.bonc.usdp.odk.logmanager.LogManager;

/**
 * @ClassName:ProcessorUtil
 * @Package:com.bonc.uni.nlp.utils
 * @Description:TODO
 */
public class ProcessorUtil {

	public static String getProcessorDomain() {
		String domain = "localhost:9187";
		try {
			Resources resources = new Resources(PathUtil.getConfigPath() + File.separator + SystemConstant.APPLICATION);
			domain = resources.getProperty("ip") + ":" + resources.getProperty("port");
			String processor = resources.getProperty("processor");
			if (null != processor && !processor.isEmpty()) {
				domain = domain + "/" + processor;
			}
		} catch (PathNotFoundException e) {
			LogManager.Exception(e);
		}
		return domain;
	}
}
