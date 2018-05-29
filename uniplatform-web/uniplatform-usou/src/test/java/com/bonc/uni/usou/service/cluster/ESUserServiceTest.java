package com.bonc.uni.usou.service.cluster;

import com.bonc.usdp.odk.logmanager.Log4jTraceParameters;
import com.bonc.usdp.odk.logmanager.LogManager;
import org.junit.Test;

import java.io.File;

public class ESUserServiceTest {
	static {
		// 配置LogManager
		String logManagerPath = ClassLoader.getSystemResource("logmanager.cfg").getPath();
		String parent = new File(logManagerPath).getParent();
		LogManager.init(new Log4jTraceParameters("uniplatform", parent));
	}

	ESUserService esUserService = new ESUserService();

	@Test
	public void testRoleList() throws Exception {
		String host = "http://localhost:9200";
		String result = esUserService.roleList(host, "");
		System.out.println(result);
	}
}
