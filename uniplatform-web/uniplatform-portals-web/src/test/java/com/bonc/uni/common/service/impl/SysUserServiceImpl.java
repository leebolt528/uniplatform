package com.bonc.uni.common.service.impl;

import com.bonc.uni.common.entity.user.SysUser;
import com.bonc.uni.common.service.user.SysUserService;
import com.bonc.uni.common.util.MapUtil;
import com.bonc.uni.portals.PortalsApplication;
import com.bonc.usdp.odk.logmanager.Log4jTraceParameters;
import com.bonc.usdp.odk.logmanager.LogManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.Map;

/**
 * Created by yedunyao on 2017/9/27.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes =PortalsApplication.class)
public class SysUserServiceImpl {

    static {
        //配置LogManager
        String logManagerPath = ClassLoader.getSystemResource("logmanager.cfg").getPath();
        String parent = new File(logManagerPath).getParent();
        LogManager.init(new Log4jTraceParameters("uniplatform", parent));
    }

    @Autowired
    SysUserService sysUserService;

    @Test
    public void testAddUser () {
        SysUser sysUser = new SysUser();
        sysUser.setAccount("account");
        sysUser.setPasswd("md5");
        sysUser.setUserName("username");
        sysUser.setGender("gender");
        sysUser.setBirthday("2000/01/02");
        sysUser.setHouseholdRegister("HouseholdRegister");
        sysUser.setPosition("position");
        sysUser.setEmail("email");
        sysUser.setPhoneNumber("phoneNumber");
        SysUser root = sysUserService.getSysUserByAccount("root");
        Map <String, Object> objectMap = MapUtil.convertObjectToMap(sysUser);

        objectMap.put("groupId", "1");
        objectMap.put("roleId", "2");

        System.out.println("===========Add user==============");

        String addUser = sysUserService.addUser(objectMap, root);
        System.out.println(addUser);

    }

}
