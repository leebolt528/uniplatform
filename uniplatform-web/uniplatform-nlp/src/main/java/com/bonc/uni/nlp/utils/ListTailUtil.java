package com.bonc.uni.nlp.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.bonc.uni.nlp.entity.dic.DicSubType;

public class ListTailUtil {

	public static List<Map<String, Object>> tailList(List<Map<String, Object>> subTypesAll, List<Map<String, Object>> subTypeBytype) {
	
		List<String> names=new ArrayList<>();
		for (Map<String, Object> allMap : subTypesAll) {
			DicSubType dicAll = (DicSubType)allMap.get("subType");
			String nameAll = dicAll.getName();
			names.add(nameAll);
		}
		for (Map<String, Object> currentMap : subTypeBytype) {
			DicSubType dic = (DicSubType)currentMap.get("subType");
			String name = dic.getName();
			if(!names.contains(name)){
				subTypesAll.add(currentMap);
			}
		}		
		return subTypesAll;
		
		
	}

}
