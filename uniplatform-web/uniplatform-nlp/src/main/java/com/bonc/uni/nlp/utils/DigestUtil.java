/*
 * Created: liushen@Dec 12, 2010 3:12:26 PM
 */
package com.bonc.uni.nlp.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.bonc.usdp.odk.common.string.StringUtil;


/**
 * 生成各常见摘要(md5, sha1等)的工具类. <br>
 * 
 */
public class DigestUtil {

	/**
	 * 生成给定数据的MD5摘要.
	 * 
	 * @param sData
	 *            要摘要的数据
	 * @return md5摘要字符串, 十六进制格式表示
	 * @throws UnsupportedEncodingException 
	 * @throws NoSuchAlgorithmException 
	 * @since liushen @ Dec 12, 2010
	 */
	public static String md5Hex(String sData) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		AssertUtil.notNull(sData, "sData is null.");
		byte[] bytes = sData.getBytes("UTF-8");

		return StringUtil.toString(md5Checksum(bytes));
	}

	
	/**
	 * 
	 * @param data
	 * @return
	 * @throws NoSuchAlgorithmException 
	 * @since liushen @ Dec 12, 2010
	 */
	static byte[] md5Checksum(byte[] data) throws NoSuchAlgorithmException {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.update(data);
		return md5.digest();
	}

	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	static void listSecuProvidersAndServiceTypes() {
		Provider[] providers = Security.getProviders();
		Set result = new HashSet();
		for (int i = 0; i < providers.length; i++) {
			// Get services provided by each provider
			Set keys = providers[i].keySet();
			for (Iterator it = keys.iterator(); it.hasNext();) {
				String key = (String) it.next();
				key = key.split(" ")[0];
    
                if (key.startsWith("Alg.Alias.")) {
					// Strip the alias
					key = key.substring(10);
				}
				int ix = key.indexOf('.');
				result.add(key.substring(0, ix));
			}
		}
    }

}
