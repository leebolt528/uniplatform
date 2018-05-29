package com.bonc.uni.dcci.service.impl;

import com.bonc.uni.common.dao.user.SysUserRepository;
import com.bonc.uni.common.util.ResultUtil;
import com.bonc.uni.dcci.dao.CrawlerRepository;
import com.bonc.uni.dcci.entity.Crawler;
import com.bonc.uni.dcci.entity.Server;
import com.bonc.uni.dcci.entity.ServerPwd;
import com.bonc.uni.dcci.util.ComponentType;
import com.bonc.uni.dcci.util.CrawlerType;
import com.bonc.uni.portals.PortalsApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.criteria.*;
import java.math.BigInteger;
import java.util.List;

/**
 * Created by yedunyao on 2017/9/20.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes =PortalsApplication.class)
public class CrawlerServiceImplTest {

    @Autowired
    CrawlerServiceImpl crawlerService;

    @Autowired
    CrawlerRepository crawlerRepository;

    @Autowired
    SysUserRepository sysUserRepository;

    @Test
    public void testList() {
        Page<Crawler> crawlers = crawlerService.pageList("", 0, 0, 1, 10);
        String success = ResultUtil.success("请求成功", crawlers);
        System.out.println(success);
    }

    @Test
    public void testFindCrawler() {
        String name = "NAME";
        String ip = "172.0.0.4";
        String port = "9200";
        Specification<Crawler> crawlerSpecification = new Specification<Crawler>() {
            public Predicate toPredicate(Root<Crawler> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Join<ServerPwd, Server> join = root.join("server", JoinType.LEFT);
                Predicate nameQuery = cb.like(cb.lower(root.<String>get("name")), "%" + name + "%");
                Predicate serverQuery = cb.like(join.get("ip"), "%" + ip + "%");
                Predicate portQuery = cb.like(root.<String>get("port"), "%" + port + "%");

                return cb.and(nameQuery, serverQuery, portQuery);

                //return null;
            }
        };

        Page<Crawler> crawlers = crawlerRepository.findAll(crawlerSpecification,
                new PageRequest(0, 20));

        String success = ResultUtil.success("请求成功", crawlers);
        System.out.println(success);
    }


    @Test
    public void testSave () {
        Crawler crawler = new Crawler();
        crawler.setComponentType(ComponentType.CRAWLER);
        crawler.setServerId(3);
        crawlerService.saveCrawler(crawler);
    }

    @Test
    public void testUpdate () {
        Crawler crawler = new Crawler();
        crawler.setId(4);
        crawler.setName("123");
        crawler.setServerId(4);
        crawlerService.updCrawler(crawler);
    }

    @Test
    public void testSearch () {
        Page<Crawler> crawlers =  crawlerService.search(CrawlerType.ABROAD_BLOG, 0,
                "", "", "", 1, 10);

        String success = ResultUtil.success("请求成功", crawlers);
        System.out.println(success);
    }

    @Test
    public void testCountCrawlerByServerIp () {
        List <Object[]> objects = crawlerRepository.countCrawlerByServerIp();
        objects.forEach( (Object[] objArr) -> {
            long count = ((BigInteger) objArr[0]).longValue();
            String ip = (String) objArr[1];
            System.out.println( String.format("ip : %s, count : %s",
                    ip, count) );
        });
    }

    @Test
    public void test () {
        sysUserRepository.getSysUserInfoGroupBySysGroup();
    }

    @Test
    public void testFindCrawlerByServerIdAndPortAndName () {
        List <Crawler> name2 = crawlerRepository.findCrawlerByServerIdAndPort(0, "9200");
        System.out.println(name2.size());
    }

}
