package com.bonc.uni.dcci.service.impl;

import com.bonc.uni.common.util.ResultUtil;
import com.bonc.uni.dcci.dao.ServerPwdRepository;
import com.bonc.uni.dcci.dao.ServerRepository;
import com.bonc.uni.dcci.entity.Server;
import com.bonc.uni.dcci.entity.ServerModel;
import com.bonc.uni.dcci.entity.ServerPwd;
import com.bonc.uni.dcci.service.ServerService;
import com.bonc.uni.dcci.util.MachineType;
import com.bonc.uni.dcci.util.SystemType;
import com.bonc.uni.portals.PortalsApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.criteria.*;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes =PortalsApplication.class)
public class ServerServiceImplTest {

	@Autowired
	ServerService serverService;
	@Autowired
    ServerRepository serverRepository;
	@Autowired
    ServerPwdRepository serverPwdRepository;

	@Test
    public void testListServer() {
        List<Server> servers = serverService.listAll();
        String success = ResultUtil.success("请求成功", servers);
        System.out.println(success);
    }

    @Test
    public void  testPageList () {
        Page<Server> servers = serverService.pageList("", SystemType.ALL, MachineType.ALL, 2, 10);
        String success = ResultUtil.success("请求成功", servers);
        System.out.println(success);
    }

    @Test
    public void testPwdPageList () {
        Page<ServerPwd> serverPwds = serverService.pageServerPwds(3, 2, 10);
        String success = ResultUtil.success("请求成功", serverPwds);
        System.out.println(success);
    }

    @Test
    public void testListByServer() {
        List<ServerPwd> serverPwds = serverService.listByServer(2);
        String success = ResultUtil.success("请求成功", serverPwds);
        System.out.println(success);
    }

    @Test
    public void testDelServer() {
        List<ServerModel> serverModels = serverService.findAllServerModel();
        String success = ResultUtil.success("请求成功", serverModels);
        System.out.println(success);
    }

    @Test
    public void testFindByJoin () {
        final String serverId = "2";
        final String user = "USER";
        List<ServerPwd> serverPwdList = serverPwdRepository.findAll(new Specification<ServerPwd>() {
            public Predicate toPredicate(Root<ServerPwd> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
//                ListJoin<Employee, Company> companyJoin = root.join(root.getModel().getList("companyList", Company.class), JoinType.LEFT);
                Join<ServerPwd, Server> join = root.join("server", JoinType.LEFT);
                return cb.equal(join.get("id"), "%");
            }
        });

        String success = ResultUtil.success("请求成功", serverPwdList);
        System.out.println(success);
    }

    @Test
    public void testSaveServer () {
	    Server server = new Server();
	    server.setMachineType(MachineType.ALL);
	    server.setIp("172.17.3.4");
        serverService.addServer(server);

    }

    @Test
    public void testFindByIp () {
        List <Server> byIp = serverRepository.findByIp("2");
        System.out.println(byIp.size());
    }
}
