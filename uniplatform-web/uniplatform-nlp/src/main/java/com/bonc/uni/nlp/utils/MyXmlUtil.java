package com.bonc.uni.nlp.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.web.multipart.MultipartFile;

import com.bonc.usdp.odk.common.file.FileUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * 
 * @author zlq
 *
 */
public class MyXmlUtil {
	/**
	 * 加载xml文件
	 * 
	 * @return Document
	 * @throws Exception
	 */
	public static Document load(MultipartFile file) throws Exception {
		Document document = null;
		File convFile = null;
		convFile = new File(file.getOriginalFilename());
		convFile.createNewFile();
		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(file.getBytes());
		fos.close();
		SAXBuilder reader = new SAXBuilder();
		document = reader.build(convFile);
		return document;
	}
	
	public static Document load(File file) throws Exception {
		Document document = null;
		SAXBuilder reader = new SAXBuilder();
		document = reader.build(file);
		return document;
	}

	/**
	 * 将MultipartFile 类型的xml文件转换为String串
	 * 
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> XmlToMap(MultipartFile file) throws Exception {
		Map<String, Object> rsMap = new HashMap<>();
		Document document = null;
		document = load(file);

		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");// 设置编码格式

		StringWriter out = null; // 输出对象
		String textFromFile = ""; // 输出字符串
		XMLOutputter outputter = new XMLOutputter();
		out = new StringWriter();
		outputter.output(document, out);
		textFromFile = out.toString();
		JSONObject xmlJSONObj = XML.toJSONObject(textFromFile);
		String jsonPrettyPrintString = xmlJSONObj.toString();
		ObjectMapper mapper = new ObjectMapper();
		rsMap = mapper.readValue(jsonPrettyPrintString, new TypeReference<Map<String, Object>>() {
		});

		File fileF = null;
		try {
			fileF = File.createTempFile("tmp", null);
			file.transferTo(fileF);
			fileF.deleteOnExit();
		} catch (IOException e) {
			e.printStackTrace();
		}
		FileUtil.deleteFile(fileF);
		return rsMap;
	}

	
	
	public static Map<String, Object> XmlToMap(File file) throws Exception {
		Map<String, Object> rsMap = new HashMap<>();
		Document document = null;
		document = load(file);

		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");// 设置编码格式

		StringWriter out = null; // 输出对象
		String textFromFile = ""; // 输出字符串
		XMLOutputter outputter = new XMLOutputter();
		out = new StringWriter();
		outputter.output(document, out);
		textFromFile = out.toString();
		JSONObject xmlJSONObj = XML.toJSONObject(textFromFile);
		String jsonPrettyPrintString = xmlJSONObj.toString();
		ObjectMapper mapper = new ObjectMapper();
		rsMap = mapper.readValue(jsonPrettyPrintString, new TypeReference<Map<String, Object>>() {
		});

		return rsMap;
	}
}
