package com.bonc.uni.dcci.service;

import com.bonc.uni.dcci.entity.Site;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**站点服务
 * @author futao
 * 2017年9月5日
 */
public interface SiteService {

	/**保存
	 * @param site
	 * @return
	 */
	boolean saveSite(Site site);
	
	/**修改
	 * @param site
	 * @return
	 */
	boolean updSite(Site site);
	
	/**批量修改
	 * @param sites
	 * @return
	 */
	boolean updsSite(List<Site> sites);
	
	/**
	 * get
	 * @param id
	 * @return
	 */
	Site getById(Integer id);
	
	/**删除
	 * @param id
	 * @return
	 */
	boolean delById(Integer id);
	
	/**
	 * 列表
	 * @param name
	 * @param url
	 * @param industry
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	Page<Site> pageList(String name,String url,int status,int pageNum, int pageSize);
	
	/**批量添加
	 * 需先设置urlmd5
	 * @param sites
	 * @return
	 */
	boolean saveSites(List<Site> sites);
	
	/**
	 * 根据配置id删除
	 * @param configId
	 * @return
	 */
	boolean delByConfigId(Integer configId);
	
	/**
	 * 批量删除
	 * @param sites
	 * @return
	 */
	boolean dels(List<Site> sites);
	
	/**
	 * 根据id 获取集合
	 * @param ids
	 * @return
	 */
	List<Site> listAll(List<Integer> ids);
	
	/**根据状态和采集器id 获取
	 * @param status
	 * @param crawlerId
	 * @return
	 */
	List<Site> listAll(int status,int crawlerId);
	
	/**
	 * 解析start JSONArray
	 * @param configure
	 */
	JSONArray parseStartsArray(int crawlerId,String configure);
	
	/**
	 * 解析 采集器 starts 成集合
	 * @param crawlerId
	 * @param configure
	 * @return
	 */
	List<Site> parseStartsList(int crawlerId,String configure);
	
	/**
	 * 将 site json封装成对象
	 * @param crawlerId
	 * @param json
	 * @return
	 */
	Site parseStartsObject(int crawlerId,JSONObject json);
	
	/**
	 * 解析并添加
	 * @param crawlerId
	 * @param configure
	 * @return
	 */
	Iterable<Site> saveParseSites(int crawlerId,String configure);
	
	/**根据urlHash判断是否重复
	 * @param urlHash
	 * @return
	 */
	Site getSiteByHash(String urlHash);
	
	Site getSite(String urlHash, int status);
	
	/**
	 * 根据采集器id删除
	 * @param crawlerId
	 * @return
	 */
	int delByCrawlerId(int crawlerId);
	
	/**
	 * @param urlHash
	 * @param crawlerId
	 * @return
	 */
	Site getSiteByHash(String urlHash,int crawlerId);
	
	/**
	 * @param name
	 * @param url
	 * @param status
	 * @return
	 */
	List<Site> list(String name, String url, int status);

	/**
	 * 统计站点数量
	 * @return
	 */
	public long count ();

	/**
	 * 统计采集器中站点的数量
	 * @return
	 */
	public List<Map<String, Object>> countSiteByCrawlerName ();

	/**
	 * 根据时间统计总站点数
	 * @return
	 */
	public List<Map<String, Object>> countSiteByCreatedTime (String startTime, String endTime);
}
