package com.bonc.uni.dcci.service.impl;

import com.bonc.uni.dcci.entity.ApiDetail;
import com.bonc.uni.dcci.service.DataApiService;
import com.bonc.uni.portals.PortalsApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes =PortalsApplication.class)
public class DataApiServiceImplTest {

	@Autowired
	DataApiService dataApiService;
	
	@Test
    public void testAddApiDetail() {
		ApiDetail add = new ApiDetail();
		add.setApiId(1111);
		add.setEmail("email");
		add.setName("name");
		add.setPhone(123654);
		add.setUrl("url");
		add.setUser("user");
		dataApiService.updApiDetail(add);
	}
}
