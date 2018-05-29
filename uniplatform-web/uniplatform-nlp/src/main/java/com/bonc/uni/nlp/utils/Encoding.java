package com.bonc.uni.nlp.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;

/** 
* @author : GaoQiuyuer 
* @return 返回上传文件的编码格式
* @version: 2017年12月18日 下午7:57:36 
*/
public class Encoding {
	public static String tryEncoding(MultipartFile file)
            throws IOException {
		InputStream inputStream = file.getInputStream();
		BufferedInputStream bis = new BufferedInputStream(inputStream );
		CharsetDetector detector = new CharsetDetector();
		detector.setText(bis);
        CharsetMatch match = detector.detect();
        String encoding = match.getName();
        
        return encoding;
    }

	public static String tryEncoding(File file)
            throws IOException {
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));	
        CharsetDetector detector = new CharsetDetector();
		detector.setText(bis);
        CharsetMatch match = detector.detect();
        String encoding = match.getName();
        
        return encoding;
    }
}
 