package com.bonc.uni.nlp.entity.product;

import java.util.List;

/** 
* @author : GaoQiuyuer 
* @version: 2017年12月23日 下午5:50:26 
*/
public class EmotionResult {

	private String sentence;
	
	private List<String> evals;

	public String getSentence() {
		return sentence;
	}

	public void setSentence(String sentence) {
		this.sentence = sentence;
	}

	public List<String> getEvals() {
		return evals;
	}

	public void setEvals(List<String> evals) {
		this.evals = evals;
	}
	


}
 