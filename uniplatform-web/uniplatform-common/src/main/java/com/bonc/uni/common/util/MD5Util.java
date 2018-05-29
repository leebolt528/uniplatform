package com.bonc.uni.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * md5工具类
 * 
 * @author futao 2017年9月8日
 */
public class MD5Util {

	/**
	 * 生成md5
	 * 
	 * @param sourceStr
	 * @return
	 */
	public static String MD5(String sourceStr) {
		try {
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			mdInst.update(sourceStr.getBytes());
			byte[] md = mdInst.digest();
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < md.length; i++) {
				int tmp = md[i];
				if (tmp < 0)
					tmp += 256;
				if (tmp < 16)
					buf.append("0");
				buf.append(Integer.toHexString(tmp));
			}
			return buf.toString();// 32位加密
			// return buf.toString().substring(8, 24);// 16位加密
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 生成16位md5
	 * 
	 * @param sourceStr
	 * @return
	 */
	public static String MD516(String sourceStr) {
		String result = MD5(sourceStr);
		if (null == result) {
			return null;
		} else {
			return result.substring(8, 24);
		}
	}

	/**
	 * 生成urlmd5
	 * 
	 * @param url
	 * @return
	 */
	public static String MD5Url(String url) {
		if (StringUtils.isNotBlank(url)) {
			String result = MD5(replaceBlank(url));
			if (null != result) {
				return result.substring(8, 24);
			}
		}
		return null;
	}

	/**
	 * 去除空格 换位符
	 * 
	 * @param str
	 * @return
	 */
	public static String replaceBlank(String str) {
		String dest = "";
		if (str != null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}

	public static String getMd5ByFile(File file) {
		String value = null;
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
			value = getMd5ByFile(in);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return value;
	}

	public static String getMd5ByFile(InputStream in) {
		String value = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] buffer = new byte[1024];
			int length = -1;
			while ((length = in.read(buffer, 0, 1024)) != -1) {
				md.update(buffer, 0, length);
			}
			BigInteger bigInt = new BigInteger(1, md.digest());
			value = bigInt.toString(16);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return value;
	}

	public static String getMd5ByFile16(InputStream in) {
		String result = getMd5ByFile(in);
		if (null == result) {
			return null;
		} else {
			return result.substring(8, 24);
		}
	}
	
	public static String getMd5ByFile16(File file) {
		String result = getMd5ByFile(file);
		if (null == result) {
			return null;
		} else {
			return result.substring(8, 24);
		}
	}

	public static void main(String[] args) {
		String str = "http://blog.csdn.net/l akers_kobebryant/article/details/25502169";
		String encryptStr = MD5(replaceBlank(str));
		System.out.println("加密前：" + MD5("http://blog.csdn.net/lakers_kobebryant/article/details/25502169"));
		System.out.println("加密后：" + encryptStr);
		System.out.println(replaceBlank(str));

		System.out.println(getMd5ByFile16(new File("D:\\test\\dcci\\image\\1.jpg")));
	}
}