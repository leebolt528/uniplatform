package com.bonc.uni.nlp.entity.product;

import java.util.List;
/**
 * @ClassName: RuleClassifyResult
 * @Package: com.bonc.uni.nlp.entity.product
 * @Description: 规则分类结果
 * @author: Chris
 * @date: 2018年1月5日 下午3:45:17
 */
public class RuleClassifyResult {

	private String fileName;
	
	private List<String> classNames;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public List<String> getClassNames() {
		return classNames;
	}

	public void setClassNames(List<String> classNames) {
		this.classNames = classNames;
	}

	@Override
	public String toString() {
		return "RuleClassifyResult [fileName=" + fileName + ", classNames=" + classNames + "]";
	}
	
}
