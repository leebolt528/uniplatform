package com.bonc.uni.nlp.controller.menu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.bonc.uni.nlp.service.menu.IMenuMgmtService;
import com.bonc.usdp.odk.logmanager.LogManager;

/** 
* @author : GaoQiuyuer 
* @version: 2017年11月30日 下午7:15:50 
*/
@RestController
@CrossOrigin
@RequestMapping(value = "/nlap/admin/menu/mgmt")
public class MenuMgmtController {
	
	@Autowired
	IMenuMgmtService menuMgmtService;
	
	@RequestMapping(value = "/menu/list", method = {RequestMethod.GET, RequestMethod.POST})
	public String listMenu(){
		LogManager.Process("Process in : /nlap/admin/menu/mgmt/menu/list");
		Map<String, Object> menuMap = new HashMap<>();
		menuMap.put("status", 200);
		menuMap.put("msg", "菜单列表查询成功");
		List<Object> menuInfo = menuMgmtService.listMenu();
		menuMap.put("menu", menuInfo);

		LogManager.Process("Process out : /nlap/admin/menu/mgmt/menu/list");
		return JSON.toJSONString(menuMap, SerializerFeature.DisableCircularReferenceDetect);
	}

}
 