package com.bonc.uni.nlp.utils;

import java.io.File;

import com.bonc.uni.nlp.constant.ResourcePathConstant;

public class ResourceFileUtil {

	
	public static void clearZipFolder() {
		File zipFolder = new File(ResourcePathConstant.ZIP_PATH);
		if (!zipFolder.exists()) {
			zipFolder.mkdirs();
			return;
		} else {
			deleteDirectory(ResourcePathConstant.ZIP_PATH, false);
		}
	}
	
	public static void clearCorpusFolder() {
		File corpusFolder = new File(ResourcePathConstant.CORPUS_PATH);
		if (!corpusFolder.exists()) {
			corpusFolder.mkdirs();
			return;
		} else {
			deleteDirectory(ResourcePathConstant.CORPUS_PATH, false);
		}

	}
	
	public static boolean deleteFile(String strFile) {
		if (strFile == null) {
			return false;
		}
		return deleteFile(new File(strFile));
	}
	
	public static boolean deleteFile(File file) {
		if (file == null || false == file.isFile()) {
			return false;
		}
		return file.delete();
	}
	
	public static boolean deleteDirectory(String sPath, boolean deleteDirFile) {
		// 如果sPath不以文件分隔符结尾，自动添加文件分隔符
		if (!sPath.endsWith(File.separator)) {
			sPath = sPath + File.separator;
		}
		File dirFile = new File(sPath);
		// 如果dir对应的文件不存在，或者不是一个目录，则退出
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		boolean flag;
		// 删除文件夹下的所有文件(包括子目录)
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			// 删除子文件
			if (files[i].isFile()) {
				flag = deleteFile(files[i].getAbsolutePath());
			} // 删除子目录
			else {
				flag = deleteDirectory(files[i].getAbsolutePath(), true);
			}
			if (!flag)
				return false;
		}
		if (deleteDirFile) {
			dirFile.delete();
		}
		return true;
	}
	
}
