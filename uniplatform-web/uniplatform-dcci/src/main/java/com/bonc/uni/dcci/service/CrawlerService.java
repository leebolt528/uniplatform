package com.bonc.uni.dcci.service;

import com.bonc.uni.dcci.entity.Crawler;
import com.bonc.uni.dcci.util.CrawlerType;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * 采集器接口
 * @author futao
 * 2017年9月6日
 */
public interface CrawlerService {

	/**
	 * 添加
	 * @param crawler
	 * @return
	 */
	boolean saveCrawler(Crawler crawler);
	
	/**
	 * 修改
	 * @param crawler
	 * @return
	 */
	boolean updCrawler(Crawler crawler);
	
	/**
	 * 通过id获取
	 * @param id
	 * @return
	 */
	Crawler getById(Integer id);
	
	/**
	 * 通过id删除
	 * @param id
	 * @return
	 */
	boolean delByid(Integer id);

	/**
	 * 统计采集器个数
	 * @return
	 */
	public long count();

	/**
	 * 通过serverId获取Crawler列表
	 * @param serverId
	 * @return
	 */
	public List<Crawler> listByServer(int serverId);

	/**
	 * 通过serverId获取带分页的Crawler列表
	 * @param serverId
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	public Page<Crawler> listByServer(int serverId, int pageNum, int pageSize);

	/**
	 * 组件查询
	 * @param name
	 * @param ip
	 * @param port
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	public Page<Crawler> search(CrawlerType type, int serverId, String name, String ip, String port, int pageNum, int pageSize);

	/**
	 * 通过serverId删除
	 * @param serverId
	 * @return
	 */
	public boolean delCrawlerByServerId(int serverId);
	
	/**
	 * page检索
	 * @param name
	 * @param serverId
	 * @param port
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	Page<Crawler> pageList(String name,int serverId,int port,int pageNum, int pageSize);

	/**
	 * 统计机器中采集器数量
	 * @return
	 */
	public List<Map<String, Object>> countCrawlerByServerIp ();

	public List<Crawler> findByServerId(int serverId);

	public List<Crawler> findCrawlerByServerIdAndPort (int serverId, String port);

	public List<Crawler> findCrawlerByServerIdAndPort (Crawler crawler);
}
