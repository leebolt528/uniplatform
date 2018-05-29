package com.bonc.uni.common.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;  
import java.io.InputStream;  
import java.io.OutputStream;  
import java.util.ArrayList;  
import java.util.List;  
  
import org.apache.commons.compress.archivers.ArchiveEntry;  
import org.apache.commons.compress.archivers.zip.Zip64Mode;  
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;  
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;  
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;  

import org.apache.commons.lang3.StringUtils;

/**
 * @author futao 2017年9月14日
 */
public class ZipUtil {

	/**
	 * 递归压缩文件夹
	 * 
	 * @param srcRootDir
	 *            压缩文件夹根目录的子路径
	 * @param file
	 *            当前递归压缩的文件或目录对象
	 * @param zos
	 *            压缩文件存储对象
	 * @throws Exception
	 */
	private static void zip(String srcRootDir, File file, ZipOutputStream zos) throws Exception {
		if (file == null) {
			return;
		}

		// 如果是文件，则直接压缩该文件
		if (file.isFile()) {
			int count, bufferLen = 1024;
			byte data[] = new byte[bufferLen];

			// 获取文件相对于压缩文件夹根目录的子路径
			String subPath = file.getAbsolutePath();
			int index = subPath.indexOf(srcRootDir);
			if (index != -1) {
				subPath = subPath.substring(srcRootDir.length() + File.separator.length());
			}
			ZipEntry entry = new ZipEntry(subPath);
			zos.putNextEntry(entry);
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
			while ((count = bis.read(data, 0, bufferLen)) != -1) {
				zos.write(data, 0, count);
			}
			bis.close();
			zos.closeEntry();
		}
		// 如果是目录，则压缩整个目录
		else {
			// 压缩目录中的文件或子目录
			File[] childFileList = file.listFiles();
			for (int n = 0; n < childFileList.length; n++) {
				childFileList[n].getAbsolutePath().indexOf(file.getAbsolutePath());
				zip(srcRootDir, childFileList[n], zos);
			}
		}
	}

	/**
	 * 对文件或文件目录进行压缩
	 * 
	 * @param srcPath
	 *            要压缩的源文件路径。如果压缩一个文件，则为该文件的全路径；如果压缩一个目录，则为该目录的顶层目录路径
	 * @param zipPath
	 *            压缩文件保存的路径。注意：zipPath不能是srcPath路径下的子文件夹
	 * @param zipFileName
	 *            压缩文件名
	 * @throws Exception
	 */
	public static void zip(String srcPath, String zipPath, String zipFileName) throws Exception {
		if (StringUtils.isEmpty(srcPath) || StringUtils.isEmpty(zipPath) || StringUtils.isEmpty(zipFileName)) {
			throw new Exception("");
		}
		CheckedOutputStream cos = null;
		ZipOutputStream zos = null;
		try {
			File srcFile = new File(srcPath);

			// 判断压缩文件保存的路径是否为源文件路径的子文件夹，如果是，则抛出异常（防止无限递归压缩的发生）
			if (srcFile.isDirectory() && zipPath.indexOf(srcPath) != -1) {
				throw new Exception("zipPath must not be the child directory of srcPath.");
			}

			// 判断压缩文件保存的路径是否存在，如果不存在，则创建目录
			File zipDir = new File(zipPath);
			if (!zipDir.exists() || !zipDir.isDirectory()) {
				zipDir.mkdirs();
			}

			// 创建压缩文件保存的文件对象
			String zipFilePath = zipPath + File.separator + zipFileName;
			File zipFile = new File(zipFilePath);
			if (zipFile.exists()) {
				// 检测文件是否允许删除，如果不允许删除，将会抛出SecurityException
				SecurityManager securityManager = new SecurityManager();
				securityManager.checkDelete(zipFilePath);
				// 删除已存在的目标文件
				zipFile.delete();
			}

			cos = new CheckedOutputStream(new FileOutputStream(zipFile), new CRC32());
			zos = new ZipOutputStream(cos);

			// 如果只是压缩一个文件，则需要截取该文件的父目录
			String srcRootDir = srcPath;
			if (srcFile.isFile()) {
				int index = srcPath.lastIndexOf(File.separator);
				if (index != -1) {
					srcRootDir = srcPath.substring(0, index);
				}
			}
			// 调用递归压缩方法进行目录或文件压缩
			zip(srcRootDir, srcFile, zos);
			zos.flush();
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (zos != null) {
					zos.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 解压缩zip包
	 * 
	 * @param zipFilePath
	 *            zip文件的全路径
	 * @param unzipFilePath
	 *            解压后的文件保存的路径
	 * @param includeZipFileName
	 *            解压后的文件保存的路径是否包含压缩文件的文件名。true-包含；false-不包含
	 */
	@SuppressWarnings("unchecked")
	public static void unzip(String zipFilePath, String unzipFilePath, boolean includeZipFileName) throws Exception {
		if (StringUtils.isEmpty(zipFilePath) || StringUtils.isEmpty(unzipFilePath)) {
			throw new Exception("");
		}
		File zipFile = new File(zipFilePath);
		// 如果解压后的文件保存路径包含压缩文件的文件名，则追加该文件名到解压路径
		if (includeZipFileName) {
			String fileName = zipFile.getName();
			if (StringUtils.isNotEmpty(fileName)) {
				fileName = fileName.substring(0, fileName.lastIndexOf("."));
			}
			unzipFilePath = unzipFilePath + File.separator + fileName;
		}
		// 创建解压缩文件保存的路径
		File unzipFileDir = new File(unzipFilePath);
		if (!unzipFileDir.exists() || !unzipFileDir.isDirectory()) {
			unzipFileDir.mkdirs();
		}

		// 开始解压
		ZipEntry entry = null;
		String entryFilePath = null, entryDirPath = null;
		File entryFile = null, entryDir = null;
		int index = 0, count = 0, bufferSize = 1024;
		byte[] buffer = new byte[bufferSize];
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		@SuppressWarnings("resource")
		ZipFile zip = new ZipFile(zipFile);
		Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zip.entries();
		// 循环对压缩包里的每一个文件进行解压
		while (entries.hasMoreElements()) {
			entry = entries.nextElement();
			// 构建压缩包中一个文件解压后保存的文件全路径
			entryFilePath = unzipFilePath + File.separator + entry.getName();
			// 构建解压后保存的文件夹路径
			index = entryFilePath.lastIndexOf(File.separator);
			if (index != -1) {
				entryDirPath = entryFilePath.substring(0, index);
			} else {
				entryDirPath = "";
			}
			entryDir = new File(entryDirPath);
			// 如果文件夹路径不存在，则创建文件夹
			if (!entryDir.exists() || !entryDir.isDirectory()) {
				entryDir.mkdirs();
			}

			// 创建解压文件
			entryFile = new File(entryFilePath);
			if (entryFile.exists()) {
				// 检测文件是否允许删除，如果不允许删除，将会抛出SecurityException
				SecurityManager securityManager = new SecurityManager();
				securityManager.checkDelete(entryFilePath);
				// 删除已存在的目标文件
				entryFile.delete();
			}

			// 写入文件
			bos = new BufferedOutputStream(new FileOutputStream(entryFile));
			bis = new BufferedInputStream(zip.getInputStream(entry));
			while ((count = bis.read(buffer, 0, bufferSize)) != -1) {
				bos.write(buffer, 0, count);
			}
			bos.flush();
			bos.close();
		}
	}

	/**
	 * zip压缩文件
	 * 
	 * @param dir
	 * @param zippath
	 */
	public static void zip(String dir, String zippath) {
		List<String> paths = getFiles(dir);
		compressFilesZip(paths.toArray(new String[paths.size()]), zippath, dir);
	}

	/**
	 * 递归取到当前目录所有文件
	 * 
	 * @param dir
	 * @return
	 */
	public static List<String> getFiles(String dir) {
		List<String> lstFiles = null;
		if (lstFiles == null) {
			lstFiles = new ArrayList<String>();
		}
		File file = new File(dir);
		File[] files = file.listFiles();
		for (File f : files) {
			if (f.isDirectory()) {
				lstFiles.add(f.getAbsolutePath());
				lstFiles.addAll(getFiles(f.getAbsolutePath()));
			} else {
				String str = f.getAbsolutePath();
				lstFiles.add(str);
			}
		}
		return lstFiles;
	}

	/**
	 * 文件名处理
	 * 
	 * @param dir
	 * @param path
	 * @return
	 */
	public static String getFilePathName(String dir, String path) {
		String p = path.replace(dir + File.separator, "");
		p = p.replace("\\", "/");
		return p;
	}

	/**
	 * 把文件压缩成zip格式
	 * 
	 * @param files
	 *            需要压缩的文件
	 * @param zipFilePath
	 *            压缩后的zip文件路径 ,如"D:/test/aa.zip";
	 */
	public static void compressFilesZip(String[] files, String zipFilePath, String dir) {
		if (files == null || files.length <= 0) {
			return;
		}
		ZipArchiveOutputStream zaos = null;
		try {
			File zipFile = new File(zipFilePath);
			zaos = new ZipArchiveOutputStream(zipFile);
			zaos.setUseZip64(Zip64Mode.AsNeeded);
			// 将每个文件用ZipArchiveEntry封装
			// 再用ZipArchiveOutputStream写到压缩文件中
			for (String strfile : files) {
				File file = new File(strfile);
				if (file != null) {
					String name = getFilePathName(dir, strfile);
					ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(file, name);
					zaos.putArchiveEntry(zipArchiveEntry);
					if (file.isDirectory()) {
						continue;
					}
					InputStream is = null;
					try {
						is = new BufferedInputStream(new FileInputStream(file));
						byte[] buffer = new byte[1024];
						int len = -1;
						while ((len = is.read(buffer)) != -1) {
							// 把缓冲区的字节写入到ZipArchiveEntry
							zaos.write(buffer, 0, len);
						}
						zaos.closeArchiveEntry();
					} catch (Exception e) {
						throw new RuntimeException(e);
					} finally {
						if (is != null)
							is.close();
					}

				}
			}
			zaos.finish();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (zaos != null) {
					zaos.close();
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

	}

	/**
	 * 把zip文件解压到指定的文件夹
	 * 
	 * @param zipFilePath
	 *            zip文件路径, 如 "D:/test/aa.zip"
	 * @param saveFileDir
	 *            解压后的文件存放路径, 如"D:/test/" ()
	 */
	public static void unzip(String zipFilePath, String saveFileDir) {
		if (!saveFileDir.endsWith("\\") && !saveFileDir.endsWith("/")) {
			saveFileDir += File.separator;
		}
		File dir = new File(saveFileDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File file = new File(zipFilePath);
		if (file.exists()) {
			InputStream is = null;
			ZipArchiveInputStream zais = null;
			try {
				is = new FileInputStream(file);
				zais = new ZipArchiveInputStream(is, "GBK");
				ArchiveEntry archiveEntry = null;
				while ((archiveEntry = zais.getNextEntry()) != null) {
					// 获取文件名
					String entryFileName = archiveEntry.getName();
					if (entryFileName.contains(".json")) {
						ByteArrayOutputStream bos = new ByteArrayOutputStream();
						// 构造解压出来的文件存放路径
						String entryFilePath = saveFileDir + entryFileName;
						OutputStream os = null;
						try {
							// 把解压出来的文件写到指定路径
							File entryFile = new File(entryFilePath);
							if (entryFileName.endsWith("/")) {
								entryFile.mkdirs();
							} else {
								os = new BufferedOutputStream(new FileOutputStream(entryFile));
								byte[] buffer = new byte[1024];
								int len = -1;
								while ((len = zais.read(buffer)) != -1) {
									os.write(buffer, 0, len);
									bos.write(buffer, 0, len);
								}
							}
						} catch (IOException e) {
							throw new IOException(e);
						} finally {
							if (os != null) {
								os.flush();
								os.close();
							}
						}
						bos.close();
					} else {
						// 构造解压出来的文件存放路径
						String entryFilePath = saveFileDir + entryFileName;
						OutputStream os = null;
						try {
							// 把解压出来的文件写到指定路径
							File entryFile = new File(entryFilePath);
							if (entryFileName.endsWith("/")) {
								entryFile.mkdirs();
							} else {
								os = new BufferedOutputStream(new FileOutputStream(entryFile));
								byte[] buffer = new byte[1024];
								int len = -1;
								while ((len = zais.read(buffer)) != -1) {
									os.write(buffer, 0, len);
								}
							}
						} catch (IOException e) {
							throw new IOException(e);
						} finally {
							if (os != null) {
								os.flush();
								os.close();
							}
						}
					}
					/*
					 * // 构造解压出来的文件存放路径 String entryFilePath = saveFileDir + entryFileName;
					 * OutputStream os = null; try { // 把解压出来的文件写到指定路径 File entryFile = new
					 * File(entryFilePath); if(entryFileName.endsWith("/")){ entryFile.mkdirs();
					 * }else{ os = new BufferedOutputStream(new FileOutputStream( entryFile));
					 * byte[] buffer = new byte[1024 ]; int len = -1; while((len =
					 * zais.read(buffer)) != -1) { os.write(buffer, 0, len); } } } catch
					 * (IOException e) { throw new IOException(e); } finally { if (os != null) {
					 * os.flush(); os.close(); } }
					 */

				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			} finally {
				try {
					if (zais != null) {
						zais.close();
					}
					if (is != null) {
						is.close();
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	public static void main(String[] args) {
		/*
		 * String zipPath = "D:\\testaaaaaaa"; String dir = "D:\\ziptest"; String
		 * zipFileName = "test.zip"; try { zip(dir, zipPath, zipFileName); } catch
		 * (Exception e) { e.printStackTrace(); }
		 */

		/*
		 * String zipFilePath = "D:\\testaaaaaaa\\test.zip"; String unzipFilePath =
		 * "D:\\ziptest\\zipPath"; try { unzip(zipFilePath, unzipFilePath, true); }
		 * catch (Exception e) { e.printStackTrace(); }
		 */

		/*
		 * String dir = "D:\\ziptest"; String zippath = "D:\\testaaaaaaa\\test1.zip";
		 * ZipUtil.zip(dir, zippath);
		 */

		String unzipdir = "D:\\ziptest\\zipPath";
		String unzipfile = "D:\\testaaaaaaa\\test1.zip";
		ZipUtil.unzip(unzipfile, unzipdir);

		System.out.println("success!");
	}
}