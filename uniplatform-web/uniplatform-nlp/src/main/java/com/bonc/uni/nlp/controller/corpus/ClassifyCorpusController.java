package com.bonc.uni.nlp.controller.corpus;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.bonc.uni.nlp.constant.ResourcePathConstant;
import com.bonc.uni.nlp.dao.corpus.CorpusSetRepository;
import com.bonc.uni.nlp.service.corpus.IClassifyCorpusService;
import com.bonc.uni.nlp.service.corpus.IDataSetService;
import com.bonc.uni.nlp.utils.Encoding;
import com.bonc.uni.nlp.utils.NewZipUtil;
import com.bonc.usdp.odk.common.string.StringUtil;
import com.bonc.usdp.odk.logmanager.LogManager;

/**
 * @Title:分类语料
 * @author zlq
 *
 */
@RestController
@RequestMapping(value = "/nlap/admin/corpusMgmt/classify")
public class ClassifyCorpusController {

	@Autowired
	IClassifyCorpusService classifyCorpusService;

	@Autowired
	CorpusSetRepository corpusSetRepository;
	
	@Autowired
	IDataSetService dataSetService;

	/**
	 * 上传语料
	 * 
	 * @param files
	 * @param objectId
	 * @param corpusSetId
	 * @return
	 */
	@RequestMapping(value = "/uploadCorpus", method = { RequestMethod.GET, RequestMethod.POST })
	public String uploadCorpus(@RequestParam(value = "files", required = false) MultipartFile[] files,
			@RequestParam(value = "objectId", required = false) String objectId,
			@RequestParam(value = "corpusSetId", required = false) String corpusSetId) {
		LogManager.Process("Process in controller: /nlap/admin/corpusMgmt/classify/uploadCorpus");

		Map<String, Object> rsMap = new HashMap<>();
		rsMap.put("status", "200");
		rsMap.put("msg", "上传成功！");
		if (files != null && files.length > 0) {
			// 循环获取file数组中得文件
			for (int i = 0; i < files.length; i++) {
				MultipartFile file = files[i];
				if (!file.getOriginalFilename().endsWith("txt") && !file.getOriginalFilename().endsWith("zip")
						&& !file.getOriginalFilename().endsWith("rar")) {
					rsMap.put("status", "400");
					rsMap.put("msg", "请上传  txt 文件或者 zip、rar 格式的压缩文件！");
					return JSON.toJSONString(rsMap, SerializerFeature.DisableCircularReferenceDetect);
				}
			}
			try {
				MultipartFile firstFile = files[0];
				// 上传了一个zip文件
				if (firstFile.getOriginalFilename().endsWith("zip")
						|| firstFile.getOriginalFilename().endsWith("rar")) {
					LogManager.debug("ClassifyCorpusController : upload a zip to corpousSet : " + corpusSetId );
					classifyCorpusService.newUploadCompressedCorpus(firstFile, corpusSetId);
				} else {
					// 上传的是txt
					List<MultipartFile> txtFiles = new ArrayList<>();
					for (int i = 0; i < files.length; i++) {
						MultipartFile file = files[i];
						if (!"UTF-8".equals(Encoding.tryEncoding(file)) || !file.getOriginalFilename().endsWith("txt")) {
							continue;
						}
						txtFiles.add(file);
					}
					classifyCorpusService.uploadCorpusToClassifyObject(objectId, txtFiles, corpusSetId);
				}
	            Thread.currentThread().sleep(1000);
				LogManager.debug("uploadCorpus : 等待1s");

			} catch (Exception e) {
				LogManager.Exception("uploadCorpus : 上传语料发生异常 ： ", e);
				rsMap.put("status", "400");
				rsMap.put("msg", e.getMessage());
			}


		}
		
		LogManager.Process("Process out controller: /nlap/admin/corpusMgmt/classify/uploadCorpus");
		return JSON.toJSONString(rsMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 获取全部语料类型
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getCorpusType", method = { RequestMethod.GET, RequestMethod.POST })
	public String getCorpusType(@RequestParam(value = "tag") String tag) {
		LogManager.Process("Process in controller : the method getCorpusType of ClassifyCorpusServiceImpl");

		long startTime = System.currentTimeMillis();

		Map<String, Object> rsMap = new HashMap<>();
		rsMap.put("status", "200");
		rsMap.put("msg", "语料类型初始化成功！");
		rsMap.put("corpusTypes", classifyCorpusService.getCorpusTypes(tag));
		
		long endTime = System.currentTimeMillis();
		LogManager.info("获取语料类型的总时间 ：" + (endTime - startTime) + "ms");
		
		LogManager.Process("Process out controller : the method getCorpusType of ClassifyCorpusServiceImpl");
		return JSON.toJSONString(rsMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 获取对应语料类型下的全部功能
	 * 
	 * @param corpusTypeId
	 * @return
	 */
	@RequestMapping(value = "/getFunctions", method = { RequestMethod.GET, RequestMethod.POST })
	public String getFunctions(@RequestParam(value = "corpusTypeId", required = true) String corpusTypeId) {
		Map<String, Object> rsMap = new HashMap<>();
		rsMap.put("status", "200");
		rsMap.put("msg", "功能列表初始化成功！");
		if (null == corpusTypeId || StringUtil.isEmpty(corpusTypeId)) {
			rsMap.put("status", "400");
			rsMap.put("msg", "请选择语料类型！");
			return JSON.toJSONString(rsMap, SerializerFeature.DisableCircularReferenceDetect);
		}
		rsMap.put("functions", classifyCorpusService.getFunctionsByCorpusType(corpusTypeId));
		return JSON.toJSONString(rsMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 获取语料集对应分类体系下的对象及语料数量 
	 * TODO:增加参数
	 * 
	 * @param classifyId
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value = "/getClassifyObject", method = { RequestMethod.GET, RequestMethod.POST })
	public String getClassifyObject(@RequestParam(value = "classifyId", required = true) String classifyId,
			@RequestParam(value = "corpusSetId", required = true) String corpusSetId,
			@RequestParam(value = "pageIndex", required = false) Integer pageIndex,
			@RequestParam(value = "pageSize", required = false) Integer pageSize) {
		Map<String, Object> rsMap = new HashMap<>();
		rsMap.put("status", "200");
		rsMap.put("msg", "功能具体列表初始化成功！");
		if (null == classifyId || StringUtil.isEmpty(classifyId)) {
			rsMap.put("status", "400");
			rsMap.put("msg", "请选择分类体系！");
			return JSON.toJSONString(rsMap, SerializerFeature.DisableCircularReferenceDetect);
		}
		List<Map<String, Object>> objects = classifyCorpusService.getClassifyObject(classifyId, corpusSetId);
		List<Map<String, Object>> pagedObjects = new ArrayList<>();
		if (pageIndex == null || pageIndex < 1) {
			pageIndex = 1;
		}
		int totalNumber = objects.size();
		int totalPage;
		if (0 == totalNumber) {
			totalPage = 0;
			pagedObjects = objects;
		} else {
			totalPage = totalNumber % pageSize == 0 ? totalNumber / pageSize : (totalNumber / pageSize) + 1;
			pagedObjects = objects.subList(pageSize * (pageIndex - 1),
					((pageSize * pageIndex) > totalNumber ? totalNumber : (pageSize * pageIndex)));
		}
		rsMap.put("totalPage", totalPage);
		rsMap.put("totalNumber", totalNumber);
		rsMap.put("prePage", pageIndex - 1);
		rsMap.put("curPage", pageIndex);
		rsMap.put("nextPage", pageIndex + 1);
		rsMap.put("firstPage", "1");
		rsMap.put("lastPage", totalPage);
		rsMap.put("msg", "过滤成功!");
		rsMap.put("status", "200");
		rsMap.put("objects", pagedObjects);

		return JSON.toJSONString(rsMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 获取语料集下对象对应的语料 TODO： 参数修改
	 * 
	 * @param objectId
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value = "/listCorpusBySetAndObject", method = { RequestMethod.GET, RequestMethod.POST })
	public String listCorpusBySetAndObject(@RequestParam(value = "objectId", required = false) String objectId,
			@RequestParam(value = "corpusSetId", required = true) String corpusSetId,
			@RequestParam(value = "needText", required = true) boolean needText,
			@RequestParam(value = "pageIndex", required = false) Integer pageIndex,
			@RequestParam(value = "pageSize", required = false) Integer pageSize) {
		LogManager.Process("Process in controller : the method listCorpusBySetAndObject of ClassifyCorpusServiceImpl");

		Map<String, Object> rsMap = new HashMap<>();
		List<Map<String, Object>> corpus = classifyCorpusService.getCorpusBySetAndObject(corpusSetId, objectId,
				needText, pageIndex, pageSize);
		rsMap.put("totalNumber", classifyCorpusService.countCorpusBySetAndObject(corpusSetId, objectId));
		rsMap.put("msg", "过滤成功!");
		rsMap.put("status", "200");
		rsMap.put("corpus", corpus);

		LogManager.Process("Process out controller : the method listCorpusBySetAndObject of ClassifyCorpusServiceImpl");
		return JSON.toJSONString(rsMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 获取语料集下的所有语料 TODO:参数更改
	 * 
	 * @param classifyId
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value = "/listCorpusByCorpusSet", method = { RequestMethod.GET, RequestMethod.POST })
	public String listCorpusByCorpusSet(@RequestParam(value = "corpusSetId", required = true) String corpusSetId,
			@RequestParam(value = "needText", required = true) boolean needText,
			@RequestParam(value = "pageIndex", required = false) Integer pageIndex,
			@RequestParam(value = "pageSize", required = false) Integer pageSize) {
		LogManager.Process("Process in controller : the method listCorpusByCorpusSet of ClassifyCorpusServiceImpl");

		long startTime = System.currentTimeMillis();
		Map<String, Object> rsMap = new HashMap<>();
		List<Map<String, Object>> corpus = classifyCorpusService.getCorpusByCorpusSet(corpusSetId, needText, pageIndex,
				pageSize);
		rsMap.put("totalNumber", classifyCorpusService.countCorpusByCorpusSet(corpusSetId));
		rsMap.put("msg", "过滤成功!");
		rsMap.put("status", "200");
		rsMap.put("corpus", corpus);

		long endTime = System.currentTimeMillis();
		LogManager.info("listCorpusByCorpusSet 方法运行总时间：" + (endTime - startTime) + "ms"); // 输出程序运行时间
		
		LogManager.Process("Process out controller : the method listCorpusByCorpusSet of ClassifyCorpusServiceImpl");
		return JSON.toJSONString(rsMap, SerializerFeature.DisableCircularReferenceDetect);
	}
	/**
	 * 删除语料
	 * @param higherLevelAndCorpusIds
	 * @return
	 */
	@RequestMapping(value = "/deleteCorpus", method = { RequestMethod.GET, RequestMethod.POST })
	public String deleteCorpus(
			@RequestParam(value = "higherLevelAndCorpusIds", required = true) String higherLevelAndCorpusIds) {
		Map<String, Object> returnMap = new HashMap<>();
		returnMap.put("status", 200);
		returnMap.put("msg", "删除语料成功！");
        try {
    		classifyCorpusService.deleteCorpus(higherLevelAndCorpusIds);
			Thread.currentThread().sleep(1000);
			LogManager.debug("deleteCorpus: 等待1s");

		} catch (InterruptedException e) {
			LogManager.debug("deleteCorpus: 语料集删除语料发生异常 ： ", e);
		}
		return JSON.toJSONString(returnMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	@RequestMapping(value = "/searchCorpus", method = { RequestMethod.GET, RequestMethod.POST })
	public String searchCorpus(@RequestParam(value = "keyWord", required = true) String keyWord,
			@RequestParam(value = "corpusSetId", required = true) String corpusSetId,
			@RequestParam(value = "objectId", required = false) String objectId,
			@RequestParam(value = "needText", required = true) boolean needText,
			@RequestParam(value = "pageIndex", required = true) Integer pageIndex,
			@RequestParam(value = "pageSize", required = true) Integer pageSize) {
		long startTime = System.currentTimeMillis();

		Map<String, Object> returnMap = new HashMap<>();
		returnMap.put("satus", "200");
		returnMap.put("msg", "检索成功！");
		List<Map<String, Object>> corpus = new ArrayList<>();

		int totalNumber;

		if (StringUtil.isEmpty(keyWord)) {
			if (StringUtil.isEmpty(objectId) || null == objectId) {
				corpus = classifyCorpusService.getCorpusByCorpusSet(corpusSetId, needText, pageIndex, pageSize);
				totalNumber = classifyCorpusService.countCorpusByCorpusSet(corpusSetId);

			} else {
				corpus = classifyCorpusService.getCorpusBySetAndObject(corpusSetId, objectId, needText, pageIndex,
						pageSize);
				totalNumber = classifyCorpusService.countCorpusBySetAndObject(corpusSetId, objectId);
			}
		} else {
			Map<String, Object> corpusMap = new HashMap<>();
			if (StringUtil.isEmpty(objectId) || null == objectId) {
				corpusMap = classifyCorpusService.searchCorpusUnderCorpusSet(keyWord, corpusSetId, needText, pageIndex,
						pageSize);
				corpus = (List<Map<String, Object>>) corpusMap.get("corpus");
				totalNumber = (int) corpusMap.get("totalNum");
			} else {
				corpusMap = classifyCorpusService.searchCorpusUnderCorpusSetAndObject(keyWord, corpusSetId, objectId,
						needText, pageIndex, pageSize);
				corpus = (List<Map<String, Object>>) corpusMap.get("corpus");
				totalNumber = (int) corpusMap.get("totalNum");
			}
		}

		returnMap.put("corpus", corpus);
		returnMap.put("totalNum", totalNumber);
		long endTime = System.currentTimeMillis();
		LogManager.info("searchCorpus方法运行总时间：" + (endTime - startTime) + "ms"); // 输出程序运行时间
		return JSON.toJSONString(returnMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 将选中的语料压缩导出
	 * @param higherLevelAndCorpusIds
	 * @param corpusSetId
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/exportToZip", method = { RequestMethod.GET, RequestMethod.POST })
	public String exportToZip(
			@RequestParam(value = "higherLevelAndCorpusIds", required = false) String higherLevelAndCorpusIds,
			@RequestParam(value = "corpusSetId", required = true) String corpusSetId, HttpServletResponse response) {
		Map<String, Object> returnMap = new HashMap<>();
		returnMap.put("status", 200);
		returnMap.put("msg", "导出成功！");
		
		// 将选中的语料根据对象进行分类
		Map<String, List<String>> classifiedCorpus = classifyCorpusService.classifyCorpus(higherLevelAndCorpusIds);
		String classifyId = corpusSetRepository.getOne(corpusSetId).getClassifyId();
		try {
			// 返回的是生成文件保存目录的上层路径     ../corpus
			String corpusPath = classifyCorpusService.generateCorpusFolder(classifiedCorpus, classifyId);  
			String zipName = corpusSetRepository.getOne(corpusSetId).getName() + ".zip";
			
			//  生成的压缩文件的保存路径    ../zip/语料集.zip
			String savePath = ResourcePathConstant.ZIP_PATH + File.separator + zipName;
			LogManager.info("SavePath of zip : " + savePath);
			
			// 将生成的语料文件进行压缩
			NewZipUtil.zipFiles(corpusPath, savePath);
			
			File zipFile = new File(savePath);
	        response.setContentLength((int) zipFile.length());  
			response.setContentType("application/zip");
			response.setHeader("Content-Disposition",
					"attachment; filename=" + new String(zipName.getBytes("GBK"), "ISO8859_1"));
	        FileInputStream fis = new FileInputStream(zipFile);  			
	        BufferedInputStream buff = new BufferedInputStream(fis);  
	        OutputStream myout = response.getOutputStream();// 从response对象中得到输出流,准备下载  
	        byte[] b = new byte[1024];// 相当于我们的缓存  
	        long k = 0;// 该值用于计算当前实际下载了多少字节  
	        while (k < zipFile.length()) {  
	            int j = buff.read(b, 0, 1024);  
	            k += j;  
	            myout.write(b, 0, j);  
	        }  
	        myout.flush();  
	        buff.close();  
		} catch (Exception e) {
			LogManager.Exception(e);
		}
		return JSON.toJSONString(returnMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	@RequestMapping(value = "/listClassifyName", method = { RequestMethod.GET, RequestMethod.POST })
	public String listClassifyName() {
		Map<String, Object> returnMap = new HashMap<>();
		returnMap.put("status", "200");
		returnMap.put("msg", "初始化成功！");
		returnMap.put("classify", classifyCorpusService.getAllClassify());
		return JSON.toJSONString(returnMap, SerializerFeature.DisableCircularReferenceDetect);

	}

	/**
	 * 根据语料id获取语料内容
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/getCorpusText", method = { RequestMethod.GET, RequestMethod.POST })
	public String getCorpusText(@RequestParam(value = "corpusId", required = true) String corpusId) {
		Map<String, Object> returnMap = new HashMap<>();
		returnMap.put("status", 200);
		returnMap.put("msg", "查询成功！");
		returnMap.put("text", classifyCorpusService.getCorpusText(corpusId));
		return JSON.toJSONString(returnMap, SerializerFeature.DisableCircularReferenceDetect);

	}
	/**
	 * 编辑语料的对象
	 * @param corpusId
	 * @param corpusSetId
	 * @param objectId
	 * @return
	 */
	@RequestMapping(value = "/editCorpusObject", method = { RequestMethod.GET, RequestMethod.POST })
	public String editCorpusObject(@RequestParam(value = "corpusId") String corpusId,
			@RequestParam(value = "corpusSetId") String corpusSetId, @RequestParam(value = "objectId") String objectId,
			@RequestParam(value = "oldObjectId") String oldObjectId) {
		LogManager.Process("Process in controller : the method editCorpusObject of ClassifyCorpusServiceImpl");

		Map<String, Object> returnMap = new HashMap<>();
		returnMap.put("status", 200);
		returnMap.put("msg", "编辑成功！");
		
		List<Map<String, String>> removeList = new ArrayList<>();
		Map<String, String> temp = new HashMap<>();
		temp.put("corpusSetId", corpusSetId);
		temp.put("objectId", oldObjectId);
		removeList.add(temp);
		classifyCorpusService.updateSetArray(removeList, corpusId, false);  // 先删除原来的数组
		temp.put("corpusSetId", corpusSetId);
		temp.put("objectId", objectId);
		classifyCorpusService.updateSetArray(removeList, corpusId, true);   // 添加新的
        try {
			Thread.currentThread().sleep(1000);
			LogManager.debug("editCorpusObject : 等待1s...");

		} catch (InterruptedException e) {
			LogManager.debug("editCorpusObject : " + e);
		}

		LogManager.Process("Process out controller : the method editCorpusObject of ClassifyCorpusServiceImpl");
		return JSON.toJSONString(returnMap, SerializerFeature.DisableCircularReferenceDetect);
	}
	
	
}
