package com.bonc.uni.dcci.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bonc.uni.common.util.ResultUtil;
import com.bonc.uni.dcci.entity.Crawler;
import com.bonc.uni.dcci.entity.Site;
import com.bonc.uni.dcci.service.CrawlerService;
import com.bonc.uni.dcci.service.SiteService;
import com.bonc.uni.dcci.util.ConfiureParseUtil;
import com.bonc.uni.dcci.util.ExcelUtil;
import com.google.common.base.Splitter;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 站点管理控制器
 * @author futao
 * 2017年9月19日
 */
@CrossOrigin
@RestController
@RequestMapping("/dcci/site")
public class SiteController {

	@Autowired
	SiteService siteService;
	
	@Autowired
	CrawlerService crawlerService;
	
	/**
	 * 检索
	 * @param name
	 * @param url
	 * @param industry
	 * @param status
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@RequestMapping("/list")
	public String list(
			@RequestParam(value = "name", required = false, defaultValue = "") String name,
			@RequestParam(value = "url", required = false, defaultValue = "") String url,
			@RequestParam(value = "status", required = false, defaultValue = "1") int status,
			@RequestParam(value = "number", required = false, defaultValue = "1") int pageNum,
			@RequestParam(value = "size", required = false, defaultValue = "10") int pageSize) {
		
		Page<Site> sites = siteService.pageList(name, url, status, pageNum, pageSize);
		return ResultUtil.success("请求成功", sites);
	}
	
	/**
	 * 获取对象
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/get/{id}")
	public String get(@PathVariable int id) {
		Site site = siteService.getById(id);
		
		if(null == site) {
			return ResultUtil.getError();
		}else {
			return ResultUtil.success("获取成功", site);
		}
	}
	
	/**
	 * 删除
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/delete/{id}")
	public String delete(@PathVariable int id) {
		boolean del = true;
		Site site = siteService.getById(id);
		
		Crawler crawler = crawlerService.getById(site.getCrawlerId());
		if(null != crawler) {
			int num = (crawler.getSiteNum() -1) > 0 ? (crawler.getSiteNum() -1) : 0;
			crawler.setSiteNum(num);
			crawlerService.updCrawler(crawler);
		}
		
		
		Site delsite = siteService.getSite(site.getUrlHash(),2);
		if(null != delsite) {
			siteService.delById(delsite.getId());
		}
		site = siteService.getById(id);
		if(null != site) {
			site.setStatus(2);
			del = siteService.updSite(site);
		}
		
		//boolean del = siteService.delById(id);
		if(del) {
			return ResultUtil.delSuccess();
		}else {
			return ResultUtil.delError();
		}
	}
	
	/**
	 * 导出excel
	 * @param ids
	 * @return
	 */
	@RequestMapping(value = "/export/excel")
	public void exportExcelIds(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "ids", required = true) String idstr) {
		try {
			long times = Calendar.getInstance().getTimeInMillis();
			response.setHeader("content-Type", "application/vnd.ms-excel");
			// 下载文件的默认名称
			response.setHeader("Content-Disposition", "attachment;filename=site" + times + ".xls");
			List<String> ids = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(idstr);
			List<Integer> idsInt = new ArrayList<Integer>();
			Iterator<String> its = ids.iterator();
			while(its.hasNext()) {
				idsInt.add(Integer.parseInt(its.next()));
			}
			List<Site> sites = siteService.listAll(idsInt);
			Workbook workbooks = new HSSFWorkbook();
			ExcelUtil excelw = new ExcelUtil(workbooks);
			excelw.writeToSite(sites);
			workbooks.write(response.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 导出全部
	 * @param request
	 * @param response
	 * @param name
	 * @param url
	 * @param status
	 */
	@RequestMapping(value = "/export/excel/list")
	public void exportExcel(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "name", required = false, defaultValue = "") String name,
			@RequestParam(value = "url", required = false, defaultValue = "") String url,
			@RequestParam(value = "status", required = false, defaultValue = "1") int status) {
		try {
			long times = Calendar.getInstance().getTimeInMillis();
			response.setHeader("content-Type", "application/vnd.ms-excel");
			// 下载文件的默认名称
			response.setHeader("Content-Disposition", "attachment;filename=site" + times + ".xls");
			List<Site> sites = siteService.list(name, url, status);
			Workbook workbooks = new HSSFWorkbook();
			ExcelUtil excelw = new ExcelUtil(workbooks);
			excelw.writeToSite(sites);
			workbooks.write(response.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 生成配置
	 * @param request
	 * @param response
	 * @param idstr
	 */
	@RequestMapping(value = "/export/configure")
	public void configureIds(HttpServletRequest request, HttpServletResponse response,@RequestParam(value = "ids", required = true) String idstr) {
		try {
			long times = Calendar.getInstance().getTimeInMillis();
			List<String> ids = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(idstr);
			List<Integer> idsInt = new ArrayList<Integer>();
			Iterator<String> its = ids.iterator();
			while(its.hasNext()) {
				idsInt.add(Integer.parseInt(its.next()));
			}
			List<Site> sites = siteService.listAll(idsInt);
			JSONObject json = new JSONObject();
			//JSONArray jsons = JSONArray.fromObject(sites);
			JSONArray jsons = new JSONArray();
			Iterator<Site> itSites = sites.iterator();
			while(itSites.hasNext()) {
				Site site = itSites.next();
				JSONObject siteJson = JSONObject.fromObject(site.getConfig());
				siteJson.put("group_name", "");
				jsons.add(siteJson);
			}
			json.put("start_list", jsons);
			String config = ConfiureParseUtil.formatStartsJson(json.toString());
			
			response.setContentType("application/octet-stream");
			//response.setContentLength(config.length());
	
			// set headers for the response
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"", "site"+times+".json");
			response.setHeader(headerKey, headerValue);

			InputStream myStream = new ByteArrayInputStream(config.getBytes("GB2312"));
			IOUtils.copy(myStream, response.getOutputStream());
			response.flushBuffer();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 生成全部
	 * @param request
	 * @param response
	 * @param idstr
	 */
	@RequestMapping(value = "/export/configure/list")
	public void configure(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "name", required = false, defaultValue = "") String name,
			@RequestParam(value = "url", required = false, defaultValue = "") String url,
			@RequestParam(value = "status", required = false, defaultValue = "1") int status) {
		try {
			long times = Calendar.getInstance().getTimeInMillis();
			List<Site> sites = siteService.list(name, url, status);
			JSONObject json = new JSONObject();
			//JSONArray jsons = JSONArray.fromObject(sites);
			JSONArray jsons = new JSONArray();
			Iterator<Site> itSites = sites.iterator();
			while(itSites.hasNext()) {
				Site site = itSites.next();
				JSONObject siteJson = JSONObject.fromObject(site.getConfig());
				siteJson.put("group_name", "");
				jsons.add(siteJson);
			}
			json.put("start_list", jsons);
			String config = ConfiureParseUtil.formatStartsJson(json.toString());
			
			response.setContentType("application/octet-stream");
			//response.setContentLength(config.length());
	
			// set headers for the response
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"", "site"+times+".json");
			response.setHeader(headerKey, headerValue);

			InputStream myStream = new ByteArrayInputStream(config.getBytes("GB2312"));
			IOUtils.copy(myStream, response.getOutputStream());
			response.flushBuffer();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
