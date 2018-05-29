package com.bonc.uni.nlp.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import com.bonc.usdp.odk.logmanager.LogManager;

/** 
* @author : GaoQiuyuer 
* @version: 2018年1月15日 上午11:28:35 
*/
public class FileUtil {
	public static void copyFolder(String oldPath, String newPath) {
		try {
			(new File(newPath)).mkdirs(); // 如果文件夹不存在 则建立新文件夹
			File a = new File(oldPath);
			String[] file = a.list();
			File temp = null;
			for (int i = 0; i < file.length; i++) {
				if (oldPath.endsWith(File.separator)) {
					temp = new File(oldPath + file[i]);
				} else {
					temp = new File(oldPath + File.separator + file[i]);
				}
				if (temp.isFile()) {
					FileInputStream input = new FileInputStream(temp);
					FileOutputStream output = new FileOutputStream(newPath + "/" + (temp.getName()).toString());
					byte[] b = new byte[1024 * 5];
					int len;
					while ((len = input.read(b)) != -1) {
						output.write(b, 0, len);
					}
					output.flush();
					output.close();
					input.close();
				}
				if (temp.isDirectory()) {// 如果是子文件夹
					copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
				}
			}
		} catch (Exception e) {
			LogManager.Exception(e);
		}
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
		boolean flag = false;
		// 删除文件夹下的所有文件(包括子目录)
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			// 删除子文件
			if (!files[i].isDirectory()) {
				flag = deleteFile(files[i]);
			} // 删除子目录
			else {
				flag = deleteDirectory(files[i].getAbsolutePath(), true);
			}
		}
		if (deleteDirFile) {
			dirFile.delete();
		}
		return flag;
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

}
 