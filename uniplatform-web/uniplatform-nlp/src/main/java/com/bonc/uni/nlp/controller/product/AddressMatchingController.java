package com.bonc.uni.nlp.controller.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bonc.uni.nlp.service.product.IAddressMatchingService;
import com.bonc.usdp.odk.logmanager.LogManager;

/** 
* @author : GaoQiuyuer 
* @version: 2017年12月23日 下午6:32:11 
*/
@RestController
@CrossOrigin
@RequestMapping(value = "/nlap/admin/address/match")
public class AddressMatchingController {
	
	@Autowired
	IAddressMatchingService addressMatchingService;
	
	@RequestMapping(value="/match/results",method={RequestMethod.POST})
	public String getMultiResultFromEs(String sourceAddresses){
		LogManager.Process("Process in : /nlap/admin/address/match/match/results");
		
		String result = addressMatchingService.getMultiAddressMatchResultFromEs(sourceAddresses);
		
		LogManager.Process("Process out : /nlap/admin/address/match/match/results");
		return result;
	}
	
	@RequestMapping(value="/from/excel",method={RequestMethod.POST})
	public String upsertExcelDataToEs(String path, int column){
		LogManager.Process("Process in : /nlap/admin/address/match/from/excel");
		
		String result = addressMatchingService.upsertExcelDataToEs(path, column);
		
		LogManager.Process("Process out : /nlap/admin/address/match/from/excel");
		return result;
	}
	
	@RequestMapping(value="/from/mysql",method={RequestMethod.POST})
	public String upsertMySQLDataToEs(String strName, String strColumn){
		LogManager.Process("Process in : /nlap/admin/address/match/from/mysql");
		
		String result = addressMatchingService.upsertMySQLDataToEs(strName, strColumn);
		
		LogManager.Process("Process out : /nlap/admin/address/match/from/mysql");
		return result;
	}

}
 