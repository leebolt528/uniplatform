package com.bonc.uni.nlp.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bonc.uni.nlp.utils.PathUtil;
import com.bonc.usdp.odk.common.exception.PathNotFoundException;
import com.bonc.usdp.odk.logmanager.LogManager;

@Controller
@RequestMapping("/nlap/admin/file")
public class FileAdminController {
	/**
	 * 下载模板文件
	 * @return
	 */
	@RequestMapping(value ="/downloadTemplate")
	public void templatesDownload(String fileName, HttpServletResponse response) {
		LogManager.Process("Process in controller: /nlap/admin/file/downloadTemplate");
		try {
			response.setContentType("application/x-msdownload");
			response.addHeader("Content-Disposition",
					"attachment;filename=\"" + new String(
							(fileName).getBytes("GBK"), "ISO8859_1") + "\"");
		} catch (UnsupportedEncodingException e1) {
			LogManager.Exception(e1);
		}
		String fullFileName = null;
		try {
			fullFileName = PathUtil.getResourcesPath() + File.separator + "template" + File.separator + fileName;
		} catch (PathNotFoundException e) {
			LogManager.Exception(e);;
		}
        try(FileInputStream inStream= new FileInputStream(fullFileName);
        		ServletOutputStream outputStream = response.getOutputStream();) {
    		byte[] b = new byte[1024];
            int len;
        	while ((len = inStream.read(b)) > 0){
            	outputStream.write(b, 0, len);
            }
        } catch (IOException e) {
            LogManager.Exception("FileAdminController uploadRule exception : ", e);
        }
        LogManager.Process("Process out controller: /nlap/admin/file/downloadTemplate");
	}
}
