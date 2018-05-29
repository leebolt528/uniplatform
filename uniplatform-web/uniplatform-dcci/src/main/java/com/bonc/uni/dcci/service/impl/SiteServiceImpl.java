package com.bonc.uni.dcci.service.impl;

import com.bonc.uni.common.jpa.Specification.SpecificationUtil;
import com.bonc.uni.common.util.MD5Util;
import com.bonc.uni.common.util.MapUtil;
import com.bonc.uni.dcci.dao.SiteRepository;
import com.bonc.uni.dcci.entity.Site;
import com.bonc.uni.dcci.service.SiteService;
import com.bonc.uni.dcci.util.ConfiureParseUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author futao 2017年9月5日
 */
@Service("siteService")
public class SiteServiceImpl implements SiteService {

	@Autowired
	SiteRepository siteRepository;
	
	@PersistenceContext
    private EntityManager em;

	@Override
	public boolean saveSite(Site site) {
		try {
			String md5 = MD5Util.MD5Url(site.getUrl());
			if (null == md5) {
				return false;
			}
			site.setUrlHash(md5);
			siteRepository.save(site);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean saveSites(List<Site> sites) {
		try {
			siteRepository.save(sites);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean updSite(Site site) {
		try {
			Site upd = getById(site.getId());
			if (null != upd) {
				String md5 = MD5Util.MD5Url(site.getUrl());
				if (null == md5) {
					return false;
				}
				upd.setUrlHash(md5);
				upd.setName(site.getName());
				upd.setBoard(site.getBoard());
				upd.setCategory(site.getCategory());
				upd.setConfig(site.getConfig());
				upd.setCrawlerId(site.getCrawlerId());
				upd.setIndustry(site.getIndustry());
				upd.setMediaPro(site.getMediaPro());
				upd.setMediaType(site.getMediaType());
				upd.setStatus(site.getStatus());
				upd.setUrl(site.getUrl());
				siteRepository.save(upd);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	@Transactional
	public boolean updsSite(List<Site> sites) {
		try {
			for(int i = 0; i < sites.size(); i++) { 
				em.merge(sites.get(i));
				if(i%30==0)
	            {
	                em.flush();
	                em.clear();

	            }
			}
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public Site getById(Integer id) {
		return siteRepository.findOne(id);
	}

	@Override
	public boolean delById(Integer id) {
		try {
			siteRepository.delete(id);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean delByConfigId(Integer crawlerId) {
		try {
			List<Site> sites = listAll(1, crawlerId);
			siteRepository.delete(sites);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean dels(List<Site> sites) {
		try {
			siteRepository.delete(sites);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public List<Site> listAll(int status, int crawlerId) {
		Specification<Site> specification = SpecificationUtil.<Site>and().ep("status", status)
				.ep("crawlerId", crawlerId).build();
		return siteRepository.findAll(specification);
	}

	@Override
	public List<Site> listAll(List<Integer> ids) {
		return (List<Site>) siteRepository.findAll(ids);
	}

	@Override
	public Page<Site> pageList(String name, String url, int status, int pageNum, int pageSize) {
		Specification<Site> specification = SpecificationUtil.<Site>and().ep(status==1, "status", 1)
				.like(StringUtils.isNotBlank(name), "name", "%" + name + "%")
				.like(StringUtils.isNotBlank(url), "url", "%" + url + "%").build();
		return siteRepository.findAll(specification, new PageRequest(pageNum - 1, pageSize));
	}
	
	@Override
	public List<Site> list(String name, String url, int status) {
		Specification<Site> specification = SpecificationUtil.<Site>and().ep(status==1, "status", 1)
				.like(StringUtils.isNotBlank(name), "name", "%" + name + "%")
				.like(StringUtils.isNotBlank(url), "url", "%" + url + "%").build();
		return siteRepository.findAll(specification);
	}

	@Override
	public Site getSiteByHash(String urlHash) {
		Specification<Site> spec = SpecificationUtil.<Site>and().ep("urlHash", urlHash).build();
		return siteRepository.findOne(spec);
	}
	@Override
	public Site getSite(String urlHash, int status) {
		Specification<Site> spec = SpecificationUtil.<Site>and().ep("status", status).ep("urlHash", urlHash).build();
		return siteRepository.findOne(spec);
	}
	
	@Override
	public Site getSiteByHash(String urlHash,int crawlerId) {
		Specification<Site> spec = SpecificationUtil.<Site>and().ep("crawlerId", crawlerId).ep("urlHash", urlHash).build();
		return siteRepository.findOne(spec);
	}

	/**
	 * 解析start JSONArray
	 * 
	 * @param configure
	 */
	@Override
	public JSONArray parseStartsArray(int crawlerId, String configure) {
		JSONObject jsonConfig = JSONObject.fromObject(configure);
		JSONArray jsonArray = jsonConfig.getJSONArray(ConfiureParseUtil.START_LIST);
		return jsonArray;
	}

	/**
	 * 解析 采集器 starts 成集合
	 * 
	 * @param crawlerId
	 * @param configure
	 * @return
	 */
	@Override
	public List<Site> parseStartsList(int crawlerId, String configure) {
		List<Site> sites = new LinkedList<Site>();
		JSONArray jsonArray = parseStartsArray(crawlerId, configure);
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			Site site = parseStartsObject(crawlerId, jsonObject);
			if (null != site) {
				sites.add(site);
			}
		}
		return sites;
	}

	/**
	 * 将 site json封装成对象
	 * 
	 * @param crawlerId
	 * @param json
	 * @return
	 */
	@Override
	public Site parseStartsObject(int crawlerId, JSONObject json) {
		Site site = null;
		if (null != json) {
			site = new Site();
			site.setCrawlerId(crawlerId);
			site.setName(json.getString(ConfiureParseUtil.SITE_NAME));
			site.setBoard(json.getString(ConfiureParseUtil.BOARD_NAME));
			site.setCategory(json.getString(ConfiureParseUtil.CATEGORY));
			site.setUrl(json.getString(ConfiureParseUtil.ENTRY_URL));
			site.setStatus(1);
			site.setConfig(json.toString());
			String md5 = MD5Util.MD5Url(site.getUrl());
			if (null == md5) {
				return null;
			}
			site.setUrlHash(md5);
		}
		return site;
	}

	@Override
	public Iterable<Site> saveParseSites(int crawlerId, String configure) {
		List<Site> sites = parseStartsList(crawlerId, configure);
		return siteRepository.save(sites);
	}
	
	@Override
	public int delByCrawlerId(int crawlerId) {
		return siteRepository.delete(crawlerId);
	}

	@Override
	public long count () {
		return siteRepository.count();
	}

	@Override
	public List<Map<String, Object>> countSiteByCrawlerName () {
		List <Object[]> objects = siteRepository.countSiteByCrawlerName();
		List<Map<String, Object>> mapList = new ArrayList<>(objects.size());
		objects.forEach( (Object[] objArr) -> {
			long site_count = ((BigInteger) objArr[0]).longValue();
			String ip = (String) objArr[1];
			String port = (String) objArr[2];
			String crawler_name = (String) objArr[3];
			String crawler = ip + ":" + port + " " + crawler_name;
			Map<String, Object> map = MapUtil.newMap()
					.put("crawler", crawler)
					.put("site_count", site_count)
					.build();
			mapList.add(map);
		});
		return mapList;
	}

	@Override
	public List<Map<String, Object>> countSiteByCreatedTime (String startTime, String endTime) {
		List <Object[]> objects = siteRepository.countSiteByCreatedTime(startTime, endTime);
		List<Map<String, Object>> mapList = new ArrayList<>(objects.size());
		objects.forEach( (Object[] objArr) -> {
			long site_count = ((BigInteger) objArr[0]).longValue();
			String date = (String) objArr[1];
			Map<String, Object> map = MapUtil.newMap()
					.put("date", date)
					.put("site_count", site_count)
					.build();
			mapList.add(map);
		});
		return mapList;
	}

}
