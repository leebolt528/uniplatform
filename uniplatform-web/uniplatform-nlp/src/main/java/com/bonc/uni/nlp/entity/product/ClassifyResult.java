package com.bonc.uni.nlp.entity.product;

/**
 * @ClassName: ClassifyResult
 * @Package: com.bonc.text.core.classifier.common.entity
 * @Description: 分类结果
 * @author: Chris
 * @date: 2017年9月27日 上午10:08:40
 */
public class ClassifyResult {
	/**
	 * 分类类别
	 */
	private String className;
	/**
	 * 置信度
	 */
	private double accuracy;

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public double getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}

	@Override
	public String toString() {
		return "ClassifyResult [className=" + className + ", accuracy=" + accuracy + "]";
	}

}
