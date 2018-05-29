package com.bonc.uni.nlp.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bonc.uni.nlp.utils.PathUtil;
import com.bonc.usdp.odk.common.file.FileUtil;

import com.bonc.usdp.odk.logmanager.LogManager;

public class PosConfig {

	public static Map<String, String> nameNatureMap = new HashMap<>();

	public static Map<String, String> natureNameMap = new HashMap<>();

	static {

		try {
		

			String natureNamePath = PathUtil.getResourcesPath() + File.separator + "pos" + File.separator
					+ "nature_name.txt";
			List<String> natureNameList = new ArrayList<>();
			natureNameList =FileUtil.readFileToList(natureNamePath);
			for (String natureNameStr : natureNameList) {
				natureNameStr = natureNameStr.trim();
				String[] natureNames = natureNameStr.split("=");
				nameNatureMap.put(natureNames[1], natureNames[0]);
				natureNameMap.put(natureNames[0], natureNames[1]);
			}
		} catch (Exception e) {
			LogManager.Exception(e);
		}
	}
}
