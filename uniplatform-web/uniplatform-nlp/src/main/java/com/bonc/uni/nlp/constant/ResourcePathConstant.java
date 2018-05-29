package com.bonc.uni.nlp.constant;

import java.io.File;

import com.bonc.uni.nlp.utils.PathUtil;
import com.bonc.usdp.odk.common.exception.PathNotFoundException;
import com.bonc.usdp.odk.logmanager.LogManager;


public class ResourcePathConstant {
	
	public static String ZIP_PATH;
	
	public static String CORPUS_PATH;
	
	static {
		try {
			ZIP_PATH = PathUtil.getResourcesPath() + File.separator + "zipFile";
			
			CORPUS_PATH = PathUtil.getResourcesPath() + File.separator + "corpus";

		} catch (PathNotFoundException e) {
			LogManager.Exception(e);
		}
		
	}

}
