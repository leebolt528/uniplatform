package com.bonc.uni.dcci.controller;

import com.bonc.uni.common.util.ResultUtil;
import com.bonc.uni.dcci.entity.Crawler;
import com.bonc.uni.dcci.entity.Server;
import com.bonc.uni.dcci.service.CrawlerService;
import com.bonc.uni.dcci.service.ServerService;
import com.bonc.uni.dcci.util.CrawlerType;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 采集器 控制器
 * @author futao
 * 2017年9月7日
 */
@RestController
@RequestMapping("/dcci/crawler")
public class CrawlerController {

	@Autowired
	CrawlerService crawlerService;
	@Autowired
	ServerService serverService;

	@RequestMapping("/list/enum")
	public String listEnum() {
		JSONObject json = new JSONObject();
		json.put(CrawlerType.class.getSimpleName().toLowerCase(), CrawlerType.toJSONArray());
		return ResultUtil.success("请求成功", json);
	}
	/**
	 * 保存
	 * @param crawler
	 * @return
	 */
	@RequestMapping(value="/save",method= RequestMethod.POST)
	public String save(@ModelAttribute("crawler") Crawler crawler ) {
		Server serverById = serverService.getServerById(crawler.getServerId());
		if (serverById == null) {
			return ResultUtil.error("保存采集器失败，服务器不存在",
					null);
		}

		List <Crawler> crawlers = crawlerService
				.findCrawlerByServerIdAndPort(crawler);

		if (crawlers.size() > 0) {
			return ResultUtil.error("添加采集器失败，采集器不能占用同一机器的同一端口",
					null);
		}

		boolean bool = crawlerService.saveCrawler(crawler);
		if(bool) {
			return ResultUtil.addSuccess();
		}else {
			return ResultUtil.addError();
		}
	}


	/**
	 * 采集器列表
	 * @param serverId
	 * @param name
	 * @param ip
	 * @param port
	 * @param typestr
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@RequestMapping("/search")
	public String search(@RequestParam(value = "serverId", required = false, defaultValue = "0") int serverId,
						 @RequestParam(value = "name", required = false, defaultValue = "") String name,
						 @RequestParam(value = "ip", required = false, defaultValue = "") String ip,
						 @RequestParam(value = "port", required = false, defaultValue = "") String port,
						 @RequestParam(value = "type", required = false, defaultValue = "ALL") String typestr,
						 @RequestParam(value = "number", required = false, defaultValue = "1") int pageNum,
						 @RequestParam(value = "size", required = false, defaultValue = "10") int pageSize) {
		CrawlerType type = null;
		if(!typestr.equalsIgnoreCase("ALL")) {
			type = CrawlerType.valueOf(typestr);
		}
		Page<Crawler> pages = crawlerService.search(type, serverId, name, ip, port, pageNum, pageSize);
		return ResultUtil.success("请求成功", pages);
	}

	/**
	 * 修改
	 * @param crawler
	 * @return
	 */
	@RequestMapping(value="/update", method= RequestMethod.POST)
	public String update(@ModelAttribute("crawler") Crawler crawler) {

		Server serverById = serverService.getServerById(crawler.getServerId());
		if (serverById == null) {
			return ResultUtil.error("修改采集器失败，服务器不存在",
					null);
		}

		List<Crawler> crawlers = crawlerService.findCrawlerByServerIdAndPort(crawler);
		if (crawlers.size() > 1) {
			return ResultUtil.error("修改采集器失败，采集器不能占用同一机器的同一端口",
					null);
		}

		boolean upd = crawlerService.updCrawler(crawler);
		if (upd) {
			return ResultUtil.updSuccess();
		} else {
			return ResultUtil.updError();
		}
	}

	/**
	 * 删除
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/delete/{id}")
	public String delete(@PathVariable int id) {
		boolean del = crawlerService.delByid(id);
		if(del) {
			return ResultUtil.delSuccess();
		}else {
			return ResultUtil.delError();
		}
	}

	/**
	 * 获取对象
	 * @return
	 */
	@RequestMapping("/get/{id}")
	public String getCrawler(@PathVariable int id) {
		Crawler crawler = crawlerService.getById(id);
		if (null == crawler) {
			return ResultUtil.getError();
		} else {
			return ResultUtil.success("请求成功", crawler);
		}
	}

}
