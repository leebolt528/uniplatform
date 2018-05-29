package com.bonc.uni.nlp.controller.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bonc.uni.nlp.service.product.ITextClusterService;
import com.bonc.usdp.odk.logmanager.LogManager;

/** 
* @author : GaoQiuyuer 
* @version: 2017年12月25日 下午5:26:31 
*/
@RestController
@CrossOrigin
@RequestMapping(value = "/nlap/admin/text/cluster")
public class TextClusterController {

	@Autowired
	ITextClusterService textClusterService;

	@RequestMapping(value = "/clustering", method = { RequestMethod.GET, RequestMethod.POST })
	public String cluster(String textPath) {
		LogManager.Process("Process in controller: /nlap/admin/text/cluster/clustering");

		String clusterInfo = textClusterService.cluster(textPath);

		LogManager.Process("Process out controller: /nlap/admin/text/cluster/clustering");
		return clusterInfo;
	}
	
	@RequestMapping(value = "/clustering/en", method = { RequestMethod.GET, RequestMethod.POST })
	public String clusterEn(String textPath) {
		LogManager.Process("Process in controller: /nlap/admin/text/cluster/clustering/en");

		String clusterInfo = textClusterService.clusterEn(textPath);

		LogManager.Process("Process out controller: /nlap/admin/text/cluster/clustering/en");
		return clusterInfo;
	}
}
 