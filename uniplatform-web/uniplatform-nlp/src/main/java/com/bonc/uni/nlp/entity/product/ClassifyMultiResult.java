package com.bonc.uni.nlp.entity.product;

import java.util.List;
/**
 * @ClassName: SvmClassifyResylt
 * @Package: com.bonc.uni.nlp.entity.product
 * @Description: 功能展示-文本分类结果(根据上传文件)
 * @author: Chris
 * @date: 2018年1月5日 下午3:31:07
 */
public class ClassifyMultiResult {

	private String fileName;
	
	private List<ClassifyResult> classes;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public List<ClassifyResult> getClasses() {
		return classes;
	}

	public void setClasses(List<ClassifyResult> classes) {
		this.classes = classes;
	}

	@Override
	public String toString() {
		return "ClassifyMultiResult [fileName=" + fileName + ", classes=" + classes + "]";
	}

}
