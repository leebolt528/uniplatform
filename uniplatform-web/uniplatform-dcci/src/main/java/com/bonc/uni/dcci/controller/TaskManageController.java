package com.bonc.uni.dcci.controller;

import com.bonc.uni.common.config.UploadConfiguration;
import com.bonc.uni.common.service.SendService;
import com.bonc.uni.common.util.MD5Util;
import com.bonc.uni.common.util.ResultUtil;
import com.bonc.uni.common.util.ZipUtil;
import com.bonc.uni.dcci.entity.*;
import com.bonc.uni.dcci.service.CrawlerService;
import com.bonc.uni.dcci.service.SiteService;
import com.bonc.uni.dcci.service.TaskManageService;
import com.bonc.uni.dcci.util.*;
import com.google.common.base.Splitter;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 任务管理控制器
 * 
 * @author futao 2017年9月19日
 */
@CrossOrigin
@RestController
@RequestMapping("/dcci/task")
public class TaskManageController {

	@Autowired
	TaskManageService taskManageService;

	@Autowired
	SiteService siteService;

	@Autowired
	UploadConfiguration uploadConfiguration;
	
	@Autowired
	SendService emailService;
	
	@Autowired
	CrawlerService crawlerService;
	
	int userId = 1;
	
	public static String ALL = "ALL";

	/**
	 * 返回enum 用于前台支持checkbox
	 * 
	 * @return
	 */
	@RequestMapping("/manage/list/enum")
	public String listEnum() {
		JSONObject json = new JSONObject();
		json.put(CrawlerType.class.getSimpleName().toLowerCase(), EnumUtil.crawlerType);
		json.put(LevelType.class.getSimpleName().toLowerCase(), EnumUtil.levelType);
		json.put(StatusType.class.getSimpleName().toLowerCase(), EnumUtil.statusType);
		json.put(StatusType.TaskAssignType.class.getSimpleName().toLowerCase(),EnumUtil.statusType_TaskAssignType);
		return ResultUtil.success("请求成功", json);
	}

	/**
	 * 返回enum 用于前台支持checkbox
	 * 
	 * @return
	 */
	@RequestMapping("/site/list/enum/url")
	public String listUrlSiteTypeEnum() {
		JSONObject json = new JSONObject();
		json.put(StatusType.UrlSiteType.class.getSimpleName().toLowerCase(), EnumUtil.statusType_UrlSiteType);
		return ResultUtil.success("请求成功", json);
	}

	@RequestMapping("/manage/assign/list/enum")
	public String listTaskAssignType() {
		JSONObject json = new JSONObject();
		json.put(StatusType.TaskAssignType.class.getSimpleName().toLowerCase(),EnumUtil.statusType_TaskAssignType);
		return ResultUtil.success("请求成功", json);
	}

	/**
	 * 需求管理列表
	 * 
	 * @param name
	 * @param status
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@RequestMapping("/manage/list")
	public String list(@RequestParam(value = "name", required = false, defaultValue = "") String name,
			@RequestParam(value = "status", required = false, defaultValue = "ALL") String status,
			@RequestParam(value = "type", required = false, defaultValue = "ALL") String type,
			@RequestParam(value = "number", required = false, defaultValue = "1") int pageNum,
			@RequestParam(value = "size", required = false, defaultValue = "10") int pageSize) {
		StatusType statusType = null;
		CrawlerType crawlerType = null;
		if(!status.equalsIgnoreCase(ALL)) {
			statusType = StatusType.valueOf(status);
		}
		if(!type.equalsIgnoreCase(ALL)) {
			crawlerType = CrawlerType.valueOf(type);
		}
		Page<TaskManage> pages = taskManageService.list(name, statusType, crawlerType, pageNum, pageSize);
		return ResultUtil.success("请求成功", pages);
	}

	/**
	 * 上传需求
	 * 
	 * @param taskManage
	 * @return
	 */
	/*
	 * @RequestMapping(value = "/manage/save", method = RequestMethod.POST) public
	 * String save(@ModelAttribute("taskManage") TaskManage taskManage) {
	 */
	@Transactional
	@RequestMapping(value = "/manage/save", method = RequestMethod.POST)
	public String save(@ModelAttribute("taskManage") TaskManage taskManage,
			@RequestParam("file") MultipartFile uploadfile) {
		TaskRelation taskRelation = null;
		TaskManage taskManageAdd = null;
		//List<TaskSite> sites = null;
		try {
			taskRelation = taskManageService.saveTaskRelation(new TaskRelation());

			String fileName = uploadfile.getOriginalFilename();
			String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
			String version = "2003";
			if ("xlsx".equals(ext)) {
				version = "2007";
			}
			// String filename = fileName.substring(0, fileName.indexOf("."));
			long times = System.currentTimeMillis();

			fileName = times + "-" + userId + "-" + fileName;
			String path = uploadConfiguration.getDcci() + "/excel/" + fileName;
			File file = new File(path);
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdir();
			}
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
			out.write(uploadfile.getBytes());
			out.flush();
			out.close();
			ExcelUtil excel = new ExcelUtil(uploadfile.getInputStream(), version);
			Map<StatusType.UrlSiteType2, List<TaskSite>> siteMaps = excel.readToSite(taskRelation);
			List<TaskSite> repeat = siteMaps.get(StatusType.UrlSiteType2.REPEAT);
			List<TaskSite> list = siteMaps.get(StatusType.UrlSiteType2.ALL);
			if (list.size() == repeat.size()) {
				taskManageService.delTaskRelationById(taskRelation.getId());
				return ResultUtil.error("提交失败,excel文件中重复站点", null);
			}
			taskManage.setStatus(StatusType.UNDISTRIBUTED);
			taskManage.setTaskRelation(taskRelation.getId());

			taskManage.setSiteRepeat(repeat.size());
			taskManage.setSiteTotal(list.size());
			taskManageAdd = taskManageService.saveTaskManage(taskManage);
			if (null == taskManageAdd) {
				return ResultUtil.addError();
			}
			//sites = taskManageService.savesTaskSite(list);
			taskManageService.savesTaskSite(list);
		} catch (Exception e) {
/*
			if (null != taskRelation) {
				taskManageService.delTaskRelationById(taskRelation.getId());
			}
			if (null != taskManageAdd) {
				taskManageService.delById(taskManageAdd.getId());
			}
			if (null != sites && sites.size() > 0) {
				taskManageService.delTaskSite(sites);
			}*/
			e.printStackTrace();
			return ResultUtil.error("分配失败", e.getMessage());
		}
		return ResultUtil.success("分配成功", null);
	}
	
	/**下载上传配置的样例excel
	 * @param request
	 * @param response
	 */
	@RequestMapping("/manage/example/download")
	public void manageDownload(HttpServletRequest request, HttpServletResponse response) {
		String rootpath = ClassUtils.getDefaultClassLoader().getResource("").getPath();
		String path = rootpath+"/dcci/download/example.xlsx";
		File downloadFile = new File(path);

		ServletContext context = request.getServletContext();
		String mimeType = context.getMimeType(path);
		if (mimeType == null) {
			mimeType = "application/octet-stream";
		}
		response.setContentType(mimeType);
		response.setContentLength((int) downloadFile.length());
		String headerKey = "Content-Disposition";
		String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
		response.setHeader(headerKey, headerValue);
		try {
			InputStream myStream = new FileInputStream(path);
			IOUtils.copy(myStream, response.getOutputStream());
			response.flushBuffer();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 修改
	 * 
	 * @param taskManage
	 * @return
	 */
	@RequestMapping(value = "/manage/update", method = RequestMethod.POST)
	public String update(@ModelAttribute("taskManage") TaskManage taskManage) {

		boolean upd = taskManageService.update(taskManage);
		if (upd) {
			return ResultUtil.updSuccess();
		} else {
			return ResultUtil.updError();
		}

	}

	/**
	 * 删除，删除上传的采集点，上传分配的相关数据
	 * 
	 * @param id
	 * @return
	 */
	@Transactional
	@RequestMapping(value = "/manage/delete/{id}")
	public String delete(@PathVariable int id) {
		TaskManage taskManage = taskManageService.getById(id);
		if (taskManage.getStatus() == StatusType.COMPLETE) {
			return ResultUtil.error("任务已完成，不能删除", null);
		}
		if (taskManage.getStatus() == StatusType.EXECUTE) {
			return ResultUtil.error("任务正在执行，不能删除", null);
		}
		int relationId = taskManage.getTaskRelation();

		taskManageService.delTaskRelationById(relationId);
		taskManageService.delTaskSite(relationId);
		taskManageService.delTaskAssign(taskManage.getId());

		boolean del = taskManageService.delById(taskManage.getId());

		if (del) {
			return ResultUtil.delSuccess();
		} else {
			return ResultUtil.delError();
		}
	}

	/**
	 * 获取对象
	 * 
	 * @return
	 */
	@RequestMapping("/manage/get/{id}")
	public String get(@PathVariable int id) {
		TaskManage taskManage = taskManageService.getById(id);
		if (null == taskManage) {
			return ResultUtil.getError();
		} else {
			return ResultUtil.getSuccess(taskManage);
		}
	}

	/**
	 * 管理员查看所有采集
	 * 
	 * @param taskRelation
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@RequestMapping("/site/list")
	public String listTaskSite(@RequestParam(value = "name", required = false, defaultValue = "") String name,
			@RequestParam(value = "status", required = false, defaultValue = "ALL") String status,
			@RequestParam(value = "taskRelation", required = true) int taskRelation,
			@RequestParam(value = "number", required = false, defaultValue = "1") int pageNum,
			@RequestParam(value = "size", required = false, defaultValue = "10") int pageSize) {
		StatusType.UrlSiteType type = null;
		if(!status.equalsIgnoreCase(ALL)) {
			type = StatusType.UrlSiteType.valueOf(status);
		}
		Page<TaskSite> pages = taskManageService.listTaskSitePage(taskRelation, name, type, pageNum, pageSize);
		return ResultUtil.success("请求成功", pages);
	}

	/**
	 * 查看分配的采集任务
	 * 
	 * @param name
	 * @param status
	 * @param taskRelation
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@RequestMapping("/site/task/list")
	public String sitelistTask(@RequestParam(value = "taskRelation", required = true) int taskRelation,
			@RequestParam(value = "number", required = false, defaultValue = "1") int pageNum,
			@RequestParam(value = "size", required = false, defaultValue = "10") int pageSize) {
		Page<TaskSite> pages = taskManageService.listTaskSitePage(taskRelation, userId, "", pageNum, pageSize);
		return ResultUtil.success("请求成功", pages);
	}

	/**
	 * 采集点下载配置
	 * 
	 * @param request
	 * @param response
	 * @param taskRelation
	 */
	@RequestMapping("/site/download")
	public void download(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "name", required = false, defaultValue = "") String name,
			@RequestParam(value = "status", required = false, defaultValue = "ALL") String status,
			@RequestParam(value = "taskRelation", required = true) int taskRelation) {
		try {
			StatusType.UrlSiteType type = null;
			if(!status.equalsIgnoreCase(ALL)) {
				type = StatusType.UrlSiteType.valueOf(status);
			}
			TaskManage taskManage = taskManageService.getByRelation(taskRelation);
			String taskName = taskManage.getName();
			String fileName = new String((taskName).getBytes("GBK"), "ISO-8859-1");
			response.setHeader("content-Type", "application/vnd.ms-excel");
			// 下载文件的默认名称
			response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xls");
			List<TaskSite> sites = taskManageService.listTaskSite(taskRelation, name, type);
			Workbook workbooks = new HSSFWorkbook();
			ExcelUtil excelw = new ExcelUtil(workbooks);
			excelw.writeToTaskSite(sites);
			workbooks.write(response.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 查看分配采集任务的下载
	 * 
	 * @param request
	 * @param response
	 * @param taskRelation
	 */
	@RequestMapping("/site/task/download")
	public void downloadTaskSite(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "taskRelation", required = true) int taskRelation) {
		try {
			TaskManage taskManage = taskManageService.getByRelation(taskRelation);
			String taskName = taskManage.getName();
			String fileName = new String((taskName).getBytes("GBK"), "ISO-8859-1");
			response.setHeader("content-Type", "application/vnd.ms-excel");
			// 下载文件的默认名称
			response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xls");
			List<TaskSite> sites = taskManageService.listTaskSite(taskRelation, userId, "");
			Workbook workbooks = new HSSFWorkbook();
			ExcelUtil excelw = new ExcelUtil(workbooks);
			excelw.writeToTaskSite(sites);
			workbooks.write(response.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 分配任务
	 * 
	 * @param manageId
	 * @param crawlerId
	 * @param userIdstr
	 *            1,3,5,6,7 (用户id)
	 * @param assignTypestr
	 * @param deadlinestr
	 * @return
	 */
	@Transactional
	@RequestMapping(value = "/assign/save", method = RequestMethod.POST)
	public String saveTaskAssign(@RequestParam(value = "manageId", required = true) int manageId,
			@RequestParam(value = "crawlerIds", required = true) String crawlerstr,
			@RequestParam(value = "userIds", required = true) String userIdstr,
			@RequestParam(value = "assignType", required = false, defaultValue = "TOTAL") String assignTypestr,
			@RequestParam(value = "deadline", required = false) long deadline) {
		try {
			StatusType.TaskAssignType assignType = StatusType.TaskAssignType.valueOf(assignTypestr);
			List<String> userIds = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(userIdstr);
			List<String> crawlerIds = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(crawlerstr);
			if (assignType == StatusType.TaskAssignType.AVERAGE && (userIds.size() < 2 || crawlerIds.size() < 2)) {
				return ResultUtil.error("平均分配选择用户或采集器大于1", null);
			}
			if (assignType == StatusType.TaskAssignType.AVERAGE && (userIds.size() != crawlerIds.size())) {
				return ResultUtil.error("平均分配选择采集器和用户数量相等", null);
			}
			if (assignType == StatusType.TaskAssignType.TOTAL && (userIds.size() > 1 || crawlerIds.size() > 1)) {
				return ResultUtil.error("单一分配只能选择一个用户或一个采集器", null);
			}
			// long deadline = DateUtil.dateToStamp(deadlinestr);
			// long deadline = 0;
			TaskManage taskManage = taskManageService.getById(manageId);
			taskManage.setAssignType(assignType);
			taskManage.setStatus(StatusType.EXECUTE);
			taskManageService.update(taskManage);

			int relationId = taskManage.getTaskRelation();
			List<TaskAssign> taskAssigns = new ArrayList<TaskAssign>();
			List<TaskSite> siteAll = new ArrayList<TaskSite>();
			List<TaskSite> sites = taskManageService.listTaskSite(relationId, "", StatusType.UrlSiteType.UNDISTRIBUTED);
			if(sites.size() > 0) {
				Map<String, List<TaskSite>> mapSites = averageSite(userIds, sites);
				Iterator<Entry<String, List<TaskSite>>> its = mapSites.entrySet().iterator();
				int i = 0;
				while (its.hasNext()) {
					Entry<String, List<TaskSite>> entry = its.next();
					String user = entry.getKey();
					List<TaskSite> siteUsers = entry.getValue();
					TaskAssign taskAssign = new TaskAssign();
					taskAssign.setUserId(Integer.parseInt(user));
					//taskAssign.setUserName("张三");
					taskAssign.setTaskManage(taskManage.getId());
					taskAssign.setDeadline(deadline);
					taskAssign.setSiteAssign(siteUsers.size());
					taskAssign.setCrawlerId(Integer.parseInt(crawlerIds.get(i)));

					/*taskAssign.setManageLevelType(taskManage.getLevelType());
					taskAssign.setManageName(taskManage.getName());
					taskAssign.setManageType(taskManage.getType());
					taskAssign.setTaskRelation(taskManage.getTaskRelation());*/
					taskAssigns.add(taskAssign);
					siteAll.addAll(siteUsers);
					i++;
				}
				taskManageService.savesTaskAssign(taskAssigns);
				taskManageService.updsTaskSite(siteAll);
			}else {
				return ResultUtil.error("没有需要分配的站点", null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultUtil.error("分配失败", null);
		}
		return ResultUtil.success("分配成功", null);
	}

	/**
	 * 采集点分配方式
	 * 
	 * @param users
	 * @param sites
	 * @return
	 */
	public Map<String, List<TaskSite>> averageSite(List<String> users, List<TaskSite> sites) {
		Map<String, List<TaskSite>> allot = new ConcurrentHashMap<String, List<TaskSite>>(); // 保存分配的信息
		if (users != null && users.size() > 0 && sites != null && sites.size() > 0) {
			for (int i = 0; i < sites.size(); i++) {
				int j = i % users.size();
				String userId = users.get(j).trim();
				if (allot.containsKey(userId)) {
					List<TaskSite> list = allot.get(userId);
					TaskSite site = sites.get(i);
					site.setUserId(Integer.parseInt(userId));
					site.setStatus(StatusType.UrlSiteType.CONFIGURING);
					list.add(site);
					allot.put(userId, list);
				} else {
					List<TaskSite> list = new ArrayList<TaskSite>();
					TaskSite site = sites.get(i);
					site.setStatus(StatusType.UrlSiteType.CONFIGURING);
					site.setUserId(Integer.parseInt(userId));
					list.add(site);
					allot.put(userId, list);
				}

			}
		}
		return allot;
	}

	/**
	 * 分配任务撤回,删除分配相关数据
	 * 
	 * @param id
	 * @return
	 */
	@Transactional
	@RequestMapping("/manage/recall/{id}")
	public String recallTaskAssign(@PathVariable int id) {

		TaskManage taskManage = taskManageService.getById(id);
		if (taskManage.getStatus() == StatusType.COMPLETE) {
			return ResultUtil.error("任务已完成，不能撤回", null);
		}
		int relationId = taskManage.getTaskRelation();
		taskManageService.delTaskAssign(taskManage.getId());
		taskManageService.updateSiteRecall(relationId, userId, StatusType.UrlSiteType.UNDISTRIBUTED);
		taskManage.setStatus(StatusType.RECALL);
		boolean recall = taskManageService.update(taskManage);
		if (recall) {
			return ResultUtil.success("撤回成功", null);
		} else {
			return ResultUtil.error("撤回失败", null);
		}
	}

	/**
	 * 查看任务
	 * 
	 * @param id
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value = "/manage/view/{id}", method = RequestMethod.POST)
	public String viewTask(@PathVariable int id,
			@RequestParam(value = "number", required = false, defaultValue = "1") int pageNum,
			@RequestParam(value = "size", required = false, defaultValue = "10") int pageSize) {
		Page<TaskAssign> pages = taskManageService.listTaskAssign(id, pageNum, pageSize);
		return ResultUtil.success("请求成功", pages);
	}

	/**
	 * 查看分配当前用户的任务
	 * 
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value = "/assign/view" , method = RequestMethod.POST)
	//@RequestMapping(value = "/assign/view")
	public String viewTask(@RequestParam(value = "name", required = false, defaultValue = "") String manageName,
			@RequestParam(value = "levelType", required = false, defaultValue = "ALL") String manageLevelType,
			@RequestParam(value = "type", required = false, defaultValue = "ALL") String type,
			@RequestParam(value = "success", required = false, defaultValue = "-1") int success,
			@RequestParam(value = "number", required = false, defaultValue = "1") int pageNum,
			@RequestParam(value = "size", required = false, defaultValue = "10") int pageSize) {
		CrawlerType crawlerType = null;
		LevelType levelType = null;
		if(!type.equalsIgnoreCase(ALL)) {
			crawlerType = CrawlerType.valueOf(type);
		}
		if(!manageLevelType.equalsIgnoreCase(ALL)) {
			levelType = LevelType.valueOf(manageLevelType);
		}
		Page<TaskAssign> pages = taskManageService.pageTaskAssignUser(userId, manageName, crawlerType, levelType,success,
				pageNum, pageSize);
		return ResultUtil.success("请求成功", pages);
	}

	/**
	 * 开始配置
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/assign/begin/{id}")
	public String beginConfig(@PathVariable int id) {
		TaskAssign taskAssign = taskManageService.getTaskAssign(id);
		taskAssign.setSuccess(1);
		boolean upd = taskManageService.updTaskAssign(taskAssign);
		if (upd) {
			return ResultUtil.success("执行成功", null);
		} else {
			return ResultUtil.error("执行失败", null);
		}
	}

	/**
	 * 上传配置
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	@RequestMapping(value = "/assign/upload", method = RequestMethod.POST)
	public String uploadConfig(@RequestParam("file") MultipartFile uploadfile,
			@RequestParam(value = "manageId", required = true) int manageId) {

		try {
			String fileName = uploadfile.getOriginalFilename();
			
			String filename = fileName.substring(0, fileName.indexOf("."));
			if (!fileName.contains(".zip")) {
				return ResultUtil.error("请上传.zip文件", null);
			}
			TaskManage taskManage = taskManageService.getById(manageId);
			TaskAssign taskAssign = taskManageService.getTaskAssignUser(userId, taskManage.getId());
			if(taskAssign.getSuccess() == 2) {
				return ResultUtil.error("任务已经上传完成", null);
			}
			
			long times = System.currentTimeMillis();
			fileName = times + "-" + userId + "-" + fileName;
			String strPrefix = fileName.substring(0, fileName.indexOf("."));
			//strPrefix = times + "-" + userId + "-" + strPrefix;
			String path = uploadConfiguration.getDcci() + "/zip/" + fileName;
			String unzippath = uploadConfiguration.getDcci() + "/unzip/" + strPrefix;
			File filePath = new File(path);
			if (!filePath.getParentFile().exists()) {
				filePath.getParentFile().mkdir();
			}
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(filePath));
			out.write(uploadfile.getBytes());
			out.flush();
			out.close();
			ZipUtil.unzip(path, unzippath);//解压文件
			File file = new File(unzippath+"/"+filename);
			File[] files = file.listFiles(new JsonFilter());//获取解压文件中的json
			if (files.length > 1) {
				return ResultUtil.error("上传失败,只能上传一个导出的json文件", null);
			}
			if(files.length == 0) {
				return ResultUtil.error("上传失败,需要上传导出的json文件", null);
			}
			File[] fileExcels = file.listFiles(new ExcelFilter());//获取解压文件中的excel
			if (fileExcels.length > 1) {
				return ResultUtil.error("上传失败,只能上传一个导出的excel文件", null);
			}
			if(fileExcels.length == 0) {
				return ResultUtil.error("上传失败,需要上传导出的excel文件", null);
			}
			String ext = fileName.substring(fileExcels[0].getName().lastIndexOf(".") + 1);
			String version = "2003";
			if ("xlsx".equals(ext)) {
				version = "2007";
			}

			String configure = FileUtils.readFileToString(files[0], ConfiureParseUtil.GB2312);//读取json文件中的配置
			JSONObject jsonConfig = JSONObject.fromObject(configure);
			
			List<Site> sites =siteService.parseStartsList(taskAssign.getCrawlerId(),jsonConfig.toString());//解析json文件内容为site集合
			if (sites.size() > taskAssign.getSiteAssign()) {
				return ResultUtil.error("上传失败，上传采集点数量大于分配采集点", null);
			}
			//List<Site> assigns = new LinkedList<Site>();//分配的采集点
			//List<Site> noassigns = new LinkedList<Site>();//未分配的采集点
			List<Site> delsite = new LinkedList<Site>();
			Iterator<Site> its = sites.iterator();
			List<TaskSite> taskSites = new LinkedList<TaskSite>();
			while (its.hasNext()) {
				Site site = its.next();
				TaskSite taskSite = taskManageService.getSiteByHash(taskManage.getTaskRelation(),site.getUrlHash());
				if (null != taskSite) {
					taskSite.setStatus(StatusType.UrlSiteType.COMPLETE);
					taskSites.add(taskSite);
					//assigns.add(site);//分配的采集点
				}
				
				Site getsite = siteService.getSite(site.getUrlHash(),1);
				if(null != getsite) {
					delsite.add(getsite);
				}
			}
			//taskAssign.setSiteComplete(assigns.size());//完成采集点个数，必须是分配的
			//上传的未分配采集点
		/*	Iterator<Site> noits = noassigns.iterator();
			while(noits.hasNext()) {
				Site site = noits.next();
				Site getsite = siteService.getSiteByHash(site.getUrlHash());
				if(null == getsite) {//site库里不存在，添加入库  , 如果存在，舍弃
					assigns.add(site);
				}
			}*/
			siteService.dels(delsite);//删除已经存在的采集点，然后再进行插入
			siteService.saveSites(sites);
			taskManageService.updsTaskSite(taskSites);// 修改分配采集任务的状态
			
			
			
			//从excel中读取不可配置的采集点
			ExcelUtil excels = new ExcelUtil(new FileInputStream(fileExcels[0]), version);
			Map<String, Object> excelMaps = excels.readUploadExcel();
			List<String> failUrl = (List<String>) excelMaps.get("fails");
			Map<String, String> urls = (Map<String, String>) excelMaps.get("urls");
			//List<String> failUrl = excels.readUploadExcel();//读取excel中标记为不可配置的采集点urlhash
			List<TaskSite> failtaskSites = new ArrayList<TaskSite>();
			Iterator<String> itsfailUrl = failUrl.iterator();
			while(itsfailUrl.hasNext()) {
				TaskSite taskSite = taskManageService.getSiteByHash(taskManage.getTaskRelation(),itsfailUrl.next());
				if (null != taskSite) {
					taskSite.setStatus(StatusType.UrlSiteType.FAIL);
					failtaskSites.add(taskSite);
				}
			}
			taskManageService.updsTaskSite(failtaskSites);
			
			//判断正则的url  并对上面的批量添加site进行响应修改
			List<Site> patternSites = new LinkedList<Site>();
			Iterator<String> itsUrls = urls.keySet().iterator();
			while(itsUrls.hasNext()) {
				String urlHash = itsUrls.next();
				String patternUrl = urls.get(urlHash);
				String patternHash = MD5Util.MD5Url(patternUrl);
				Site site = siteService.getSite(urlHash, 1);
				if(null != site) {
					site.setUrl(patternUrl);
					site.setUrlHash(patternHash);
					patternSites.add(site);
				}
			}
			siteService.updsSite(patternSites);//修改有正则的url
			
			taskAssign.setSiteComplete(taskSites.size());//完成采集点个数，必须是分配的
			taskAssign.setSiteFail(failtaskSites.size());
			int siteNum = taskAssign.getSiteComplete() + taskAssign.getSiteFail();
			if (taskAssign.getSiteAssign() <= siteNum) {
				taskAssign.setSuccess(2);// 标记该任务已完成
			}
			taskManageService.updTaskAssign(taskAssign);
			if (taskAssign.getSuccess() == 2) {
				List<TaskAssign> taskAssigns = taskManageService.listTaskAssignManage(taskManage.getId());
				List<TaskAssign> taskAssignSucess = taskManageService.listTaskAssignManage(taskManage.getId(), 2);
				if (null != taskAssigns && null != taskAssignSucess && taskAssignSucess.size() == taskAssigns.size()) {// 任务完成
					taskManage.setStatus(StatusType.COMPLETE);
					taskManageService.update(taskManage);
				}
			}
			
			Crawler crawler = crawlerService.getById(taskAssign.getCrawlerId());
			if(null != crawler) {
				crawler.setSiteNum(crawler.getSiteNum() + sites.size());
				crawlerService.updCrawler(crawler);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultUtil.error("上传失败", e.getMessage());
		}
		return ResultUtil.success("上传成功", null);
	}
	
	
	/**下载上传配置的样例zip
	 * @param request
	 * @param response
	 */
	@RequestMapping("/assign/upload/example/download")
	public void uploadDownload(HttpServletRequest request, HttpServletResponse response) {
		/*String rootpath = Thread.currentThread().getContextClassLoader()
				.getResource("").toString();*/
		String rootpath = ClassUtils.getDefaultClassLoader().getResource("").getPath();
		String path = rootpath+"/dcci/download/example.zip";
		File downloadFile = new File(path);

		ServletContext context = request.getServletContext();
		String mimeType = context.getMimeType(path);
		if (mimeType == null) {
			mimeType = "application/octet-stream";
		}
		response.setContentType(mimeType);
		response.setContentLength((int) downloadFile.length());
		String headerKey = "Content-Disposition";
		String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
		response.setHeader(headerKey, headerValue);
		try {
			InputStream myStream = new FileInputStream(path);
			IOUtils.copy(myStream, response.getOutputStream());
			response.flushBuffer();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 上传成功 上传的配置文件校验   用于判断站点的删除
	 * 
	 * 
	 * 
	 * @return
	 */
	@Transactional
	@RequestMapping(value = "/assign/check", method = RequestMethod.POST)
	public String taskSucess(@RequestParam("file") MultipartFile uploadfile,
			@RequestParam(value = "manageId", required = true) int manageId) {
		try {
			TaskManage taskManage = taskManageService.getById(manageId);
			TaskAssign taskAssign = taskManageService.getTaskAssignUser(userId, taskManage.getId());
			//int crawlerId = taskAssign.getCrawlerId();
			String fileName = uploadfile.getOriginalFilename();
			//String filename = fileName.substring(0, fileName.indexOf("."));
			if (!fileName.contains(".json")) {
				return ResultUtil.error("请上传.json文件", null);
			}
			long times = System.currentTimeMillis();
			fileName = times + "-" + userId + "-" + fileName;
			String path = uploadConfiguration.getDcci() + "/check/" + fileName;
			File filePath = new File(path);
			if (!filePath.getParentFile().exists()) {
				filePath.getParentFile().mkdir();
			}
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(filePath));
			out.write(uploadfile.getBytes());
			out.flush();
			out.close();
			
			String configurestr = FileUtils.readFileToString(filePath, ConfiureParseUtil.GB2312);
			JSONObject jsonConfig = JSONObject.fromObject(configurestr);
			JSONArray jsonArray = jsonConfig.getJSONArray(ConfiureParseUtil.START_LIST);
			List<Site> delsites = new LinkedList<Site>();
			List<Site> updsites = new LinkedList<Site>();
			List<Site> allsites = new LinkedList<Site>();
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				String jsonStr = jsonObject.toString();
				JSONObject json = JSONObject.fromObject(jsonStr);
				String url = json.getString(ConfiureParseUtil.ENTRY_URL);
				String md5 = MD5Util.MD5Url(url);
				Site site = siteService.getSiteByHash(md5);
				/*if (null != site) {
					delsites.add(site);
				}*/
				if (null != site) {
					if(site.getStatus() == 2) {
						delsites.add(site);
					}
					site.setStatus(2);
					updsites.add(site);
				}
				allsites.add(site);
			}
			siteService.dels(delsites);
			siteService.updsSite(updsites);
			taskAssign.setCheck(1);
			taskManageService.updTaskAssign(taskAssign);
			
			Crawler crawler = crawlerService.getById(taskAssign.getCrawlerId());
			if(null != crawler) {
				int num = crawler.getSiteNum() - allsites.size() > 0 ? crawler.getSiteNum() - allsites.size() : 0;
				crawler.setSiteNum(num);
				crawlerService.updCrawler(crawler);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return ResultUtil.success("上传失败", e.getMessage());
		}
		return ResultUtil.success("上传成功", null);
	}
	
	/**下载检查配置的样例excel
	 * @param request
	 * @param response
	 */
	@RequestMapping("/assign/check/example/download")
	public void checkDownload(HttpServletRequest request, HttpServletResponse response) {
		String rootpath = ClassUtils.getDefaultClassLoader().getResource("").getPath();
		String path = rootpath+"/dcci/download/example.json";
		File downloadFile = new File(path);

		ServletContext context = request.getServletContext();
		String mimeType = context.getMimeType(path);
		if (mimeType == null) {
			mimeType = "application/octet-stream";
		}
		response.setContentType(mimeType);
		response.setContentLength((int) downloadFile.length());
		String headerKey = "Content-Disposition";
		String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
		response.setHeader(headerKey, headerValue);
		try {
			InputStream myStream = new FileInputStream(path);
			IOUtils.copy(myStream, response.getOutputStream());
			response.flushBuffer();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public class JsonFilter implements FileFilter {

		@Override
		public boolean accept(File pathname) {
			String filename = pathname.getName().toLowerCase();
			if (filename.contains(".json")) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	public class ExcelFilter implements FileFilter {

		@Override
		public boolean accept(File pathname) {
			String filename = pathname.getName().toLowerCase();
			if (filename.contains(".xls")) {
				return true;
			} else {
				return false;
			}
		}
	}

}
