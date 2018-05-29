package com.bonc.uni.nlp.controller.corpus;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.bonc.uni.nlp.constant.ResourcePathConstant;
import com.bonc.uni.nlp.dao.classify.ClassifyRepository;
import com.bonc.uni.nlp.dao.corpus.CorpusSetRepository;
import com.bonc.uni.nlp.exception.AdminException;
import com.bonc.uni.nlp.service.corpus.IClassifyCorpusService;
import com.bonc.uni.nlp.service.corpus.ICorpusSetService;
import com.bonc.uni.nlp.service.rule.IClassifyManagementService;
import com.bonc.uni.nlp.utils.NewZipUtil;
import com.bonc.usdp.odk.common.file.FileUtil;
import com.bonc.usdp.odk.common.string.StringUtil;
import com.bonc.usdp.odk.logmanager.LogManager;

@RestController
@RequestMapping(value = "/nlap/admin/corpusMgmt/corpusSet")
public class CorpusSetController {

	@Autowired
	ICorpusSetService corpusSetService;

	@Autowired
	IClassifyManagementService classifyService;

	@Autowired
	IClassifyCorpusService classifyCorpusService;

	@Autowired
	CorpusSetRepository corpusSetRepository;

	@Autowired
	ClassifyRepository classifyRepository;

	/**
	 * 增加语料集
	 * 
	 * @param corpusSetName
	 * @param classifyId
	 * @param functionId
	 * @return
	 */
	@RequestMapping(value = "/addCorpusSet", method = { RequestMethod.GET, RequestMethod.POST })
	public String addCorpusSet(@RequestParam(value = "name", required = true) String corpusSetName,
			@RequestParam(value = "classifyId", required = true) String classifyId,
			@RequestParam(value = "functionId", required = true) String functionId) {
		Map<String, Object> returnMap = new HashMap<>();
		returnMap.put("status", "200");
		returnMap.put("msg", "添加成功！");
		String treatedCorpusSetName = corpusSetName.trim();
		try {
			corpusSetService.addCorpusSet(treatedCorpusSetName, functionId, classifyId);
		} catch (Exception e) {
			returnMap.put("status", "400");
			returnMap.put("msg", e.getMessage());
		}
		return JSON.toJSONString(returnMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 删除语料集
	 * TODO : 是否删除ES中对应语料集下的所有语料
	 * @param corpusSetId
	 * @return
	 */
	@RequestMapping(value = "/deleteCorpusSet", method = { RequestMethod.GET, RequestMethod.POST })
	public String deleteCorpusSet(@RequestParam(value = "corpusSetId", required = true) String corpusSetId) {
		LogManager.method("Process in controller: /nlap/admin/corpusMgmt/corpusSet/listCorpusSet");

		Map<String, Object> returnMap = new HashMap<>();
		returnMap.put("status", "200");
		returnMap.put("msg", "删除成功！");
		try {
			corpusSetService.deleteCorpusSet(corpusSetId);
			// 判断还有没有相同分类体系的语料集
		} catch (AdminException e) {
			returnMap.put("status", "400");
			returnMap.put("msg", e.getMessage());
		}
		return JSON.toJSONString(returnMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 修改语料集
	 * 
	 * @param corpusSetId
	 * @return
	 */
	@RequestMapping(value = "/updateCorpusSet", method = { RequestMethod.GET, RequestMethod.POST })
	public String updateCorpusSet(@RequestParam(value = "corpusSetId", required = true) String corpusSetId,
			@RequestParam(value = "corpusSetName", required = true) String corpusSetName,
			@RequestParam(value = "functionId", required = true) String functionId,
			@RequestParam(value = "classifyId", required = true) String classifyId) {
		LogManager.method("Process in controller: /nlap/admin/corpusMgmt/corpusSet/listCorpusSet");

		Map<String, Object> returnMap = new HashMap<>();
		returnMap.put("status", "200");
		returnMap.put("msg", "编辑成功！");
		String treatedCorpusSetName = corpusSetName.trim();
		try {
			corpusSetService.updateCorpusSet(functionId, corpusSetId, treatedCorpusSetName, classifyId);
		} catch (Exception e) {
			e.printStackTrace();
			returnMap.put("msg", e.getMessage());
		}
		return JSON.toJSONString(returnMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 列出对应功能下的所有语料集
	 * 
	 * @param functionId
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value = "/listCorpusSet", method = { RequestMethod.GET, RequestMethod.POST })
	public String listCorpuseSet(@RequestParam(value = "functionId", required = true) String functionId,
			@RequestParam(value = "pageIndex", required = true) int pageIndex,
			@RequestParam(value = "pageSize", required = true) int pageSize) {
		LogManager.method("Process in controller: /nlap/admin/corpusMgmt/corpusSet/listCorpusSet");

		long listStartTime = System.currentTimeMillis();
		
		Map<String, Object> returnMap = new HashMap<>();
		returnMap.put("status", "200");
		returnMap.put("msg", "初始化成功！");
		returnMap.put("corpusSet", corpusSetService.getCorpusSetByFunction(functionId, pageIndex, pageSize));
		returnMap.put("totalNum", corpusSetService.getCorpusSetNumByFunction(functionId));
		
		long listEndTime = System.currentTimeMillis();
		LogManager.info("过滤语料集的总时间 ：" + (listEndTime - listStartTime) + "ms");
		
		LogManager.method("Process out controller: /nlap/admin/corpusMgmt/corpusSet/listCorpusSet");
		return JSON.toJSONString(returnMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 导出整个语料集或者某个对象的全部语料
	 * 
	 * @param corpusSetId
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/downloadCorpusSet", method = { RequestMethod.GET, RequestMethod.POST })
	public String downloadCorpusSet(@RequestParam(value = "corpusSetId", required = true) String corpusSetId,
			@RequestParam(value = "objectId", required = false) String objectId, HttpServletResponse response) {
		LogManager.method("Process in controller: /nlap/admin/corpusMgmt/corpusSet/downloadCorpusSet");

		Map<String, Object> returnMap = new HashMap<>();
		returnMap.put("status", "200");
		returnMap.put("msg", "下载成功！");
		Map<String, List<String>> classifiedCorpus = new HashMap<>();
		
		if (null == objectId || "".equals(objectId)) {
			LogManager.debug("Download the corpusSet : " + corpusSetId);
			classifiedCorpus = corpusSetService.classifyCorpusByCorpusSet(corpusSetId);
		} else {
			LogManager.debug("Down the object : " + objectId);
			classifiedCorpus = corpusSetService.classifyCorpusByObject(corpusSetId, objectId);
		}
		
		
		try {
			String classifyId = corpusSetRepository.getOne(corpusSetId).getClassifyId();
			// 返回的是生成文件保存目录的上层路径 ../corpus
			String corpusPath = classifyCorpusService.generateCorpusFolder(classifiedCorpus, classifyId);
			LogManager.info("corpusPath :  " + corpusPath);
			String zipName = corpusSetRepository.getOne(corpusSetId).getName() + ".zip";
			// 生成的压缩文件的保存路径 ../zip/语料集.zip
			String savePath = ResourcePathConstant.ZIP_PATH + File.separator + zipName;
			LogManager.info("savePath : " + savePath);
			// 将生成的语料进行压缩
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
			zipFile.delete();
			FileUtil.deleteDirectory(corpusPath);
		} catch (Exception e) {
			LogManager.Exception(e);
		}

		LogManager.method("Process out controller: /nlap/admin/corpusMgmt/corpusSet/downloadCorpusSet");
		return JSON.toJSONString(returnMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 初始化穿梭框语料内容
	 * 
	 * @param dataSetId
	 * @param corpusSetId
	 * @param objectId
	 * @param needText
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value = "/listShuttleBoxObject", method = { RequestMethod.GET, RequestMethod.POST })
	public String listShuttleBoxObject(@RequestParam(value = "dataSetId", required = true) String dataSetId,
			@RequestParam(value = "corpusSetId", required = true) String corpusSetId,
			@RequestParam(value = "objectId", required = false) String objectId,
			@RequestParam(value = "needText", required = true) boolean needText,
			@RequestParam(value = "pageIndex", required = true) Integer pageIndex,
			@RequestParam(value = "pageSize", required = true) Integer pageSize) {
		LogManager.method("Process in controller: /nlap/admin/corpusMgmt/corpusSet/listCorpusSet");

		long initStartTime = System.currentTimeMillis(); // 穿梭框初始化开始时间
		Map<String, Object> returnMap = new HashMap<>();
		returnMap.put("status", "200");
		returnMap.put("msg", "初始化成功！");
		Map<String, Object> corpusMap = classifyCorpusService.getShuttleBoxObject(dataSetId, corpusSetId, objectId,
				needText, pageIndex, pageSize);
		returnMap.put("corpus", corpusMap.get("corpus"));
		returnMap.put("totalNum", corpusMap.get("totalNum"));
		long initEndTime = System.currentTimeMillis(); // 穿梭框初始化结束时间
		LogManager.info("初始化穿梭框的总时间 : " + (initEndTime - initStartTime) + "ms");
		
		LogManager.method("Process out controller: /nlap/admin/corpusMgmt/corpusSet/listCorpusSet");
		return JSON.toJSONString(returnMap, SerializerFeature.DisableCircularReferenceDetect);
	}

	/**
	 * 检索语料集
	 * 
	 * @param functionId
	 * @param keyWord
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value = "/searchCorpusSet", method = { RequestMethod.GET, RequestMethod.POST })
	public String searchCorpusSet(@RequestParam(value = "functionId", required = true) String functionId,
			@RequestParam(value = "keyWord", required = true) String keyWord,
			@RequestParam(value = "pageIndex", required = true) Integer pageIndex,
			@RequestParam(value = "pageSize", required = true) Integer pageSize) {
		LogManager.method("Process in controller: /nlap/admin/corpusMgmt/corpusSet/searchCorpusSet");

		Map<String, Object> returnMap = new HashMap<>();
		returnMap.put("status", "200");
		returnMap.put("msg", "检索成功！");

		List<Map<String, Object>> corpus = new ArrayList<>();
		int totalNum = 0;
		if (null == keyWord || StringUtil.isEmpty(keyWord)) {
			// 获取全部语料集
			corpus = corpusSetService.getCorpusSetByFunction(functionId, pageIndex, pageSize);
			totalNum = corpusSetService.getCorpusSetNumByFunction(functionId);
		} else {
			// 进行检索
			Map<String, Object> map = corpusSetService.searchCorpusSet(functionId, keyWord, pageIndex, pageSize);
			corpus = (List<Map<String, Object>>) map.get("corpusSet");
			totalNum = (int) map.get("totalNum");
		}
		returnMap.put("corpus", corpus);
		returnMap.put("totalNum", totalNum);

		LogManager.method("Process out controller: /nlap/admin/corpusMgmt/corpusSet/searchCorpusSet");
		return JSON.toJSONString(returnMap, SerializerFeature.DisableCircularReferenceDetect);
	}
	
	/**
	 * 导出单个语料
	 * 
	 * @param corpusId
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/exportOneCorpus", method = { RequestMethod.GET, RequestMethod.POST })
	public String exportOneCorpus(@RequestParam(value = "corpusId", required = true) String corpusId,
			HttpServletResponse response) {
		LogManager.method("Process in controller: /nlap/admin/corpusMgmt/corpusSet/exportOneCorpus");

		Map<String, Object> returnMap = new HashMap<>();
		returnMap.put("status", "200");
		returnMap.put("msg", "下载成功！");

		Map<String, String> corpus = corpusSetService.getCorpusByCorpusId(corpusId);
		String text = corpus.get("text");
		String title = corpus.get("title");
		OutputStream out = null;
		try {
			response.reset();
			response.setContentType("application/x-msdownload");
			response.addHeader("Content-Disposition", "attachment;filename=\""
					+ new String(title.getBytes("GBK"), "ISO8859_1") + "\"");
			out = response.getOutputStream();
			out.write(text.getBytes());	
		} catch (Exception e) {
			LogManager.Exception(e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					LogManager.Exception("DicDownloadController dicWordsDownload exception : ", e);
				}
			}
		}

		LogManager.method("Process out controller: /nlap/admin/corpusMgmt/corpusSet/exportOneCorpus");
		return JSON.toJSONString(returnMap, SerializerFeature.DisableCircularReferenceDetect);
	}
}
