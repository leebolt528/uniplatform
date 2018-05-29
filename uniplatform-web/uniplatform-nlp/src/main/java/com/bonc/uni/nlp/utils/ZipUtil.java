package com.bonc.uni.nlp.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import com.bonc.uni.nlp.exception.AdminException;

public class ZipUtil {

	private ZipUtil() {
	}

	public static void doCompress(String srcFile, String zipFile) throws IOException {
		doCompress(new File(srcFile), new File(zipFile));
	}

	/**
	 * 文件压缩
	 * 
	 * @param srcFile
	 *            目录或者单个文件
	 * @param zipFile
	 *            压缩后的ZIP文件
	 */
	public static void doCompress(File srcFile, File zipFile) throws IOException {
		ZipOutputStream out = null;
		try {
			out = new ZipOutputStream(new FileOutputStream(zipFile));
			doCompress(srcFile, out);
		} catch (Exception e) {
			throw e;
		} finally {
			out.close();// 记得关闭资源
		}
	}

	public static void doCompress(String filelName, ZipOutputStream out) throws IOException {
		doCompress(new File(filelName), out);
	}

	public static void doCompress(File file, ZipOutputStream out) throws IOException {
		doCompress(file, out, "");
	}

	public static void doCompress(File file, ZipOutputStream out, List<String> filesName) throws IOException {
		doCompress(file, out, "", filesName);
	}

	public static void doCompress(File inFile, ZipOutputStream out, String dir, List<String> filesName)
			throws IOException {
		int operation = 0;
		if (inFile.isDirectory()) {
			File[] files = inFile.listFiles();
			if (files != null && files.length > 0) {
				for (File file : files) {
					if (filesName.size()-1 >= operation) {
						if (filesName.contains(file.getName())) {
							operation +=1;
							String name = inFile.getName();
							if (!"".equals(dir)) {
								name = dir + "/" + name;
							}
							ZipUtil.doCompress(file, out, "");
						} else {
							String name = inFile.getName();
							if (!"".equals(dir)) {
								name = dir + "/" + name;
							}
							ZipUtil.doCompress(file, out, "");
						}
					}
				}
			}
		} else {
			ZipUtil.doZip(inFile, out, dir);
		}
	}

	public static void doCompress(File inFile, ZipOutputStream out, String dir) throws IOException {
		if (inFile.isDirectory()) {
			File[] files = inFile.listFiles();
			if (files != null && files.length > 0) {
				for (File file : files) {
					String name = inFile.getName();
					if (!"".equals(dir)) {
						name = dir + "/" + name;
					}
					ZipUtil.doCompress(file, out, name);
				}
			}
		} else {
			ZipUtil.doZip(inFile, out, dir);
		}
	}

	public static void doZip(File inFile, ZipOutputStream out, String dir) throws IOException {
		String entryName = null;
		if (!"".equals(dir)) {
			entryName = dir + "/" + inFile.getName();
		} else {
			entryName = inFile.getName();
		}
		ZipEntry entry = new ZipEntry(entryName);
		out.putNextEntry(entry);

		int len = 0;
		byte[] buffer = new byte[1024];
		FileInputStream fis = new FileInputStream(inFile);
		while ((len = fis.read(buffer)) > 0) {
			out.write(buffer, 0, len);
			out.flush();
		}
		out.closeEntry();
		fis.close();
	}

	/**
	 * 解压
	 * 
	 * @param directory
	 *            解压完保存的路径
	 * @param zip
	 *            压缩包文件
	 */
	public static void unZipFile(String directory, File zip) {
		try {
			int count = -1;
			int buffer = 2048;

			File file = null;
			InputStream is = null;
			FileOutputStream fos = null;
			BufferedOutputStream bos = null;

			ZipFile zipFile = new ZipFile(zip, Charset.forName("GBK"));
			Enumeration en = zipFile.entries();
			createFolder(directory);
			while (en.hasMoreElements()) {
				byte buf[] = new byte[buffer];
				ZipEntry entry = (ZipEntry) en.nextElement();
				String fileName = entry.getName();
				String filePath = directory + "/" + fileName;
				if (entry.isDirectory()) {
//					LogManager.info("entry is directory and fileName is " + fileName + ",the filePath is " + filePath);
					createFolder(filePath);
				} else {
//					LogManager.info(
//							"entry is not directory and fileName is " + fileName + ",the filePath is " + filePath);
					file = new File(filePath);
					file.createNewFile();

					is = zipFile.getInputStream(entry);
					fos = new FileOutputStream(file);
					bos = new BufferedOutputStream(fos, buffer);
					while ((count = is.read(buf)) > -1) {
						bos.write(buf, 0, count);
					}
					bos.close();
					fos.close();
					is.close();
				}
			}
			zipFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void createFolder(String path) {
		try {
			File uploadFilePath = new File(path);
			if (uploadFilePath.exists() == false) {
				uploadFilePath.mkdirs();
			}
		} catch (AdminException ex) {
			throw new AdminException("创建文件夹异常");
		}
	}
	
	public static void main(String[] args) {
//		String srcFile = "C:\\Users\\BONC_NLP\\Desktop\\unzip\\kevin测试大批量上传语料集(1)";
		String zipFilePath = "C:\\Users\\BONC_NLP\\Desktop\\zip\\kevin测试大批量上传语料集(1).zip";
		File zipFile = new File(zipFilePath);
		String srcFile = "C:\\Users\\BONC_NLP\\Desktop\\unzip";
		unZipFile(srcFile, zipFile);
//
//		try {
//			doCompress(srcFile, zipFile);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		System.out.println("success");
	}
}