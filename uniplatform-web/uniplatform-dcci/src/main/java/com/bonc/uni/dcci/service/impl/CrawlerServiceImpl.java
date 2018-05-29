package com.bonc.uni.dcci.service.impl;

import com.bonc.uni.common.jpa.Specification.SpecificationUtil;
import com.bonc.uni.common.util.MapUtil;
import com.bonc.uni.dcci.dao.CrawlerRepository;
import com.bonc.uni.dcci.entity.Crawler;
import com.bonc.uni.dcci.service.CrawlerService;
import com.bonc.uni.dcci.util.CrawlerType;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("crawlerService")
public class CrawlerServiceImpl implements CrawlerService {

	@Autowired
	CrawlerRepository crawlerRepository;
	
	@Override
	public boolean saveCrawler(Crawler crawler) {
		try {
			crawlerRepository.save(crawler);
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@Override
	public boolean updCrawler(Crawler crawler) {
		try {
			Crawler upd = getById(crawler.getId());
			if(null != upd) {
				upd.setName(crawler.getName());
				upd.setPort(crawler.getPort());
				upd.setPath(crawler.getPath());
				upd.setStoragePath(crawler.getStoragePath());
				upd.setType(crawler.getType());
				upd.setSiteNum(crawler.getSiteNum());
				upd.setMaxSiteNum(crawler.getMaxSiteNum());
				crawlerRepository.save(upd);
				return true;
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public long count() {
		return crawlerRepository.count();
	}

	@Override
	public Crawler getById(Integer id) {
		return crawlerRepository.findOne(id);
	}
	
	@Override
	public boolean delByid(Integer id) {
		try {
			crawlerRepository.delete(id);
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public List<Crawler> listByServer(int serverId) {
		Specification<Crawler> specification = SpecificationUtil.<Crawler>and()
				.ep("serverId", serverId).build();
		List<Crawler> crawlers = crawlerRepository.findAll(specification);
		return crawlers;
	}

	@Override
	public Page<Crawler> listByServer(int serverId, int pageNum, int pageSize) {
		Specification<Crawler> specification = SpecificationUtil.<Crawler>and()
				.ep("serverId", serverId).build();
		Page<Crawler> crawlers = crawlerRepository.findAll(specification, new PageRequest(pageNum - 1, pageSize));
		return crawlers;
	}


	@Override
	public Page<Crawler> search(CrawlerType type, int serverId, String name,
								String ip, String port, int pageNum, int pageSize) {

		String trimName = name.trim();
		String trimIp = ip.trim();
		String trimPort = port.trim();

		Specification<Crawler> crawlerSpecification = new Specification<Crawler>() {
			public Predicate toPredicate(Root<Crawler> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate idQuery = cb.equal(root.<Integer>get("serverId"), serverId);
				Predicate nameQuery = cb.like(cb.lower(root.<String>get("name")), "%" + trimName + "%");
				Predicate serverQuery = cb.like(root.<String>get("ip"), "%" + trimIp + "%");
				Predicate portQuery = cb.like(root.<String>get("port"), "%" + trimPort + "%");

				if (0 != serverId) {
					return cb.and(idQuery);
				}
				if(type != null) {
					Predicate typeQuery = cb.equal(root.<CrawlerType>get("type"), type);

					return cb.and(typeQuery, nameQuery, serverQuery, portQuery);
				}else {
					return cb.and(nameQuery, serverQuery, portQuery);
				}
				
			}
		};
		Page<Crawler> crawlers = crawlerRepository.findAll(crawlerSpecification,
				new PageRequest(pageNum - 1, pageSize));
		return crawlers;
	}


	@Override
	public boolean delCrawlerByServerId(int serverId) {
		try {
			List<Crawler> crawlers = listByServer(serverId);
			crawlerRepository.delete(crawlers);
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@Override
	public Page<Crawler> pageList(String name,int serverId,int port,int pageNum, int pageSize){
		Specification<Crawler> spec = SpecificationUtil.<Crawler>and().ep(serverId!=0,"serverId", serverId).ep(port!=0,"port", port).
				like(StringUtils.isNotBlank(name),"name", "%"+name+"%").build();
		return crawlerRepository.findAll(spec, new PageRequest(pageNum - 1, pageSize));
	}

	@Override
	public List<Map<String, Object>> countCrawlerByServerIp () {
		List <Object[]> objects = crawlerRepository.countCrawlerByServerIp();
		List<Map<String, Object>> mapList = new ArrayList <>(objects.size());
		objects.forEach( (Object[] objArr) -> {
			long crawler_count = ((BigInteger) objArr[0]).longValue();
			String ip = (String) objArr[1];
			Map<String, Object> map = MapUtil.newMap()
					.put("ip", ip)
					.put("crawler_count", crawler_count)
					.build();
			mapList.add(map);
		});
		return mapList;
	}

	@Override
	public List<Crawler> findByServerId(int serverId) {
		return crawlerRepository.findByServerId(serverId);
	}

	@Override
	public List<Crawler> findCrawlerByServerIdAndPort (int serverId, String port) {
		return crawlerRepository.findCrawlerByServerIdAndPort(serverId, port);
	}

	@Override
	public List<Crawler> findCrawlerByServerIdAndPort (Crawler crawler) {
		int serverId = crawler.getServerId();
		String port = crawler.getPort();
		return crawlerRepository.findCrawlerByServerIdAndPort(serverId, port);
	}

}
