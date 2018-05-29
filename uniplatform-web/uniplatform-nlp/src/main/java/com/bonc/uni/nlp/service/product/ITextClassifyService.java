package com.bonc.uni.nlp.service.product;

import java.util.List;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import com.bonc.uni.nlp.entity.product.ClassifyMultiResult;
import com.bonc.uni.nlp.entity.product.RuleClassifyResult;

/** 
* @author : GaoQiuyuer 
* @version: 2017年12月23日 下午4:12:33 
*/
public interface ITextClassifyService {
	/**
	 * <p>Title: svmClassify</p>
	 * <p>Description: 文本分类</p>
	 * @param text
	 * @return
	 */
	String svmClassify(String text);

	/**
	 * <p>Title: svmClassifyByFile</p>
	 * <p>Description: 对上传的文件进行分类</p>
	 * @param file
	 * @return
	 */
	List<ClassifyMultiResult> svmClassifyByFile(MultipartFile[] files);

	/**
	 * <p>Title: ruleCalssify</p>
	 * <p>Description: 规则分类</p>
	 * @param templateId 规则模板id
	 * @param file 文件
	 * @return
	 */
	List<RuleClassifyResult> ruleCalssify(String templateId, MultipartFile[] files);
	/**
	 * <p>Title: getAllSvmClasses</p>
	 * <p>Description: 获取svm分类默认模型的所有类别</p>
	 * @return
	 */
	List<String> getAllSvmClasses(String modelName);

	/**
	 * @return
	 */
	List<Object> getAllRules();

	/**
	 * @param text
	 * @return
	 */
	String svmClassifyEn(String text);

	/**
	 * @return
	 */
	Set<String> getAllSvmClassesEn(String modelName);
}
 