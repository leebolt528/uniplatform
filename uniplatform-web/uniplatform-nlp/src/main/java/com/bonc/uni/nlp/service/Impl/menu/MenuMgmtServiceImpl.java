package com.bonc.uni.nlp.service.Impl.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bonc.uni.nlp.dao.menu.FirstMenuRepository;
import com.bonc.uni.nlp.dao.menu.SecondMenuRepository;
import com.bonc.uni.nlp.entity.menu.FirstMenu;
import com.bonc.uni.nlp.entity.menu.SecondMenu;
import com.bonc.uni.nlp.service.menu.IMenuMgmtService;

/** 
* @author : GaoQiuyuer 
* @version: 2017年11月30日 下午7:18:12 
*/
@Service
public class MenuMgmtServiceImpl implements IMenuMgmtService{

	@Autowired
	FirstMenuRepository firstMenuRepository;
	@Autowired
	SecondMenuRepository secondMenuRepository;
	@Override
	public List<Object> listMenu() {
		List<FirstMenu> firstMenus = firstMenuRepository.findAll();
		List<Object> menuInfoList = new ArrayList<>();
		Map<String, Object> menuMap = new HashMap<>();
		for (FirstMenu firstMenu : firstMenus) {
 			List<Map<String, String>> secondMenuInfos = new ArrayList<>();
			Map<String, String> secondMenuInfoMap = new HashMap<>();
			String firstMenuId = firstMenu.getId();
			List<SecondMenu> secondMenus = secondMenuRepository.findAllByFirstMenuId(firstMenuId);
			for (SecondMenu secondMenu : secondMenus) {
				secondMenuInfoMap = new HashMap<>();
				secondMenuInfoMap.put("title", secondMenu.getName());
				secondMenuInfoMap.put("link", secondMenu.getUrl());
				secondMenuInfos.add(secondMenuInfoMap);
			}
			menuMap = new HashMap<>();
			menuMap.put("children", secondMenuInfos);
			menuMap.put("title", firstMenu.getName());
			menuInfoList.add(menuMap);
		}
		
		return menuInfoList;
	}

}
 