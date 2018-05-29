package com.bonc.uni.common.mail;

import com.bonc.uni.common.service.TaskService;
import com.bonc.uni.common.util.PlatformType;
import com.bonc.uni.portals.PortalsApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = PortalsApplication.class)
public class TaskServiceTest {

	@Autowired
	TaskService taskService;

	@Test
    public void test() {
		System.out.println(taskService.delTaskInfoByBeanId(1, PlatformType.USOU));
	}
}
