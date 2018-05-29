package com.bonc.uni.nlp.service.Impl.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bonc.uni.nlp.dao.menu.FunctionDisplayRepository;
import com.bonc.uni.nlp.dao.menu.FunctionSecondMenuRepository;
import com.bonc.uni.nlp.entity.dic.FunctionDisplay;
import com.bonc.uni.nlp.entity.menu.FunctionSecondMenu;
import com.bonc.uni.nlp.service.menu.IFunctionDisplayMenuService;

/** 
* @author : GaoQiuyuer 
* @version: 2017年12月29日 下午2:34:29 
*/
@Service
public class FunctionDisplayMenuServiceImpl implements IFunctionDisplayMenuService{

	@Autowired
	FunctionSecondMenuRepository secondMenuRepository;
	@Autowired
	FunctionDisplayRepository functionDisplayRepository;
	
	@Override
	public List<Map<String, Object>> listMenu() {
		List<FunctionDisplay> functions = functionDisplayRepository.findAllIndex();
		List<Map<String, Object>> functionDisplay = new ArrayList<>();
		Map<String, Object> functionMap = new HashMap<>();
		for (FunctionDisplay function : functions) {
			functionMap = new HashMap<>();
			functionMap.put("functionName", function.getDisplayName());
			functionMap.put("url", function.getUrl());
			functionMap.put("imgPath", function.getImgPath());
			List<Object> secondMemuNames = new ArrayList<>();
			Map<String, String> secondMemuNamesMap = new HashMap<>();
			if (1 == function.getHasNext()) {
				List<FunctionSecondMenu> secondMenus = secondMenuRepository.findAllByFunctionId(function.getId());
				for (FunctionSecondMenu functionSecondMenu : secondMenus) {
					secondMemuNamesMap = new HashMap<>();
					secondMemuNamesMap.put("secondMenuName", functionSecondMenu.getName());
					secondMemuNamesMap.put("secondMenuUrl", functionSecondMenu.getUrl());
					secondMemuNames.add(secondMemuNamesMap);
				}
			}
			functionMap.put("secondMenu", secondMemuNames);
			functionDisplay.add(functionMap);
		}
		
		return functionDisplay;
	}
	

}
 