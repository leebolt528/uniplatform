package com.bonc.uni.nlp.service.label;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

/** 
* @author : GaoQiuyuer 
* @version: 2017年12月5日 下午2:42:34 
*/
public interface ILabelMgmtService {

	Map<String, Object> listLabels(String keyword, int pageIndex, int pageSize, boolean ascSort);

	boolean validateLabelExists(String labelName);
	
	boolean addLabel(String names);

	boolean editLabel(String labelId, String newNames);

	boolean delLabel(String labelIds);

	Map<String, Object> labelInfo(String labelId, String keyword, int pageIndex, int pageSize, boolean ascSort);
	
	void downloadLable(String labelId, String fileName, HttpServletResponse response);

	List<Integer> uploadLabels(MultipartFile[] files);

	void downloadLables(String labelIds, String fileName, HttpServletResponse response);

}
 