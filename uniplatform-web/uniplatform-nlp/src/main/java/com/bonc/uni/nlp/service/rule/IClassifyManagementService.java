package com.bonc.uni.nlp.service.rule;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public interface IClassifyManagementService {

	List<Map<String, Object>> findAllClassify();
	
	boolean addClassify(String classifyName, String preClassifyAndObject, String objects);
	
	void deleteClassify(String classifyId);
	
	Map<String, Object> generateTree(String classifyId);
	
	List<Map<String, Object>> allClassifyInfo();
	
	boolean updateClassifyName(String classifyId, String newName);
	
	void updateObjects(String classifyId, String updateObjects);
	
	void addObjects(String classifyId, String objects);
	
	void deleteObjects(String classifyId, String objectIds);
	
	void updateDependences(String classifyId, String dependences);
	
	List<Map<String, Object>> searchByKeyWord(String keyWord);
	
	void exportClassify2XML(String[] classifyIdArr, HttpServletResponse response);

	Boolean existSameClassify(String classifyId, String classifyName);
	
	boolean importXML(MultipartFile file) throws Exception;
}
