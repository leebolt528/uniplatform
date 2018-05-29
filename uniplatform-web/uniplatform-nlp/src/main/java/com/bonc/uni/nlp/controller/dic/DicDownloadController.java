package com.bonc.uni.nlp.controller.dic;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bonc.uni.nlp.dao.DictionaryRepository;
import com.bonc.uni.nlp.dao.FieldTypeRepository;
import com.bonc.uni.nlp.dao.WordMapRepository;
import com.bonc.uni.nlp.dao.WordOrdinaryRepository;
import com.bonc.uni.nlp.dao.WordSentimentRepository;
import com.bonc.uni.nlp.dao.label.LabelRepository;
import com.bonc.uni.nlp.dao.label.LabelWordRelationRepository;
import com.bonc.uni.nlp.entity.dic.Dictionary;
import com.bonc.uni.nlp.entity.dic.WordMap;
import com.bonc.uni.nlp.entity.dic.WordOrdinary;
import com.bonc.uni.nlp.entity.dic.WordSentiment;
import com.bonc.uni.nlp.entity.label.Label;
import com.bonc.uni.nlp.entity.label.LabelWordRelation;
import com.bonc.usdp.odk.common.string.StringUtil;
import com.bonc.usdp.odk.logmanager.LogManager;

/**
 * @author : GaoQiuyuer
 * @version: 2017年10月30日 上午11:26:37
 */
@RestController
@CrossOrigin
@RequestMapping(value = "/nlap/admin/dic/download")
public class DicDownloadController {

	@Autowired
	DictionaryRepository dictionaryRepository;
	@Autowired
	WordMapRepository wordMapRepository;
	@Autowired
	WordOrdinaryRepository wordOrdinaryRepository;
	@Autowired
	WordSentimentRepository wordSentimentRepository;
	@Autowired
	FieldTypeRepository fieldTypeRepository;
	@Autowired
	LabelWordRelationRepository labelWordRelationRepository;
	@Autowired
	LabelRepository labelRepository;

	@RequestMapping(value = "/dic/download", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "text/html;charset=UTF-8")
	public String downloadDic(String dicId, HttpServletResponse response) {
		LogManager.Process("Process in controller: /nlap/admin/dic/download/dic/download");
		OutputStream out = null;

		try {

			Dictionary dict = dictionaryRepository.findOne(dicId);
			if (null == dict) {
				return null;
			}
			response.reset();
			if ("map".equals(dict.getFormat())) {
				List<WordMap> words = wordMapRepository.findAllByDictionaryId(dict.getId());

				response.setContentType("application/x-msdownload");
				response.addHeader("Content-Disposition", "attachment;filename=\""
						+ new String((dict.getName() + ".txt").getBytes("GBK"), "ISO8859_1") + "\"");
				out = response.getOutputStream();

				StringBuilder wordsContent = new StringBuilder();
				for (WordMap word : words) {
					wordsContent.append(word.getWordKey() + " = " + word.getWordValue() + "\r\n");
				}
				String wordsContentStr = wordsContent.toString();
				byte[] wordsContentbyte = wordsContentStr.getBytes();
				byte[] wordsContentbyteCoding = new byte[wordsContentbyte.length + 3];
				wordsContentbyteCoding[0] = -17;
				wordsContentbyteCoding[1] = -69;
				wordsContentbyteCoding[2] = -65;
				for (int i = 0; i < wordsContentbyte.length; i++) {
					wordsContentbyteCoding[i + 3] = wordsContentbyte[i];
				}
				out.write(wordsContentbyteCoding);
			} else if ("ordinary".equals(dict.getFormat())) {
				String fieldTypeId = dict.getFieldTypeId();
				List<WordOrdinary> words = wordOrdinaryRepository.findAllByDictionaryId(dict.getId());

				response.setContentType("application/x-msdownload");
				response.addHeader("Content-Disposition", "attachment;filename=\""
						+ new String((dict.getName() + ".txt").getBytes("GBK"), "ISO8859_1") + "\"");
				out = response.getOutputStream();

				StringBuilder wordsContent = new StringBuilder();
				for (WordOrdinary word : words) {
					if (!StringUtil.isEmpty(fieldTypeId)) {
						List<LabelWordRelation> labelWordRelations = labelWordRelationRepository
								.findAllByWordId(word.getId());
						StringBuilder stringBuilderLabel = new StringBuilder();
						StringBuilder stringBuilder = new StringBuilder();
						if (labelWordRelations.size() > 0) {
							for (LabelWordRelation labelWordRelation : labelWordRelations) {
								Label label = labelRepository.findOne(labelWordRelation.getLabelId());
								stringBuilderLabel.append(label.getName()).append("|");
							}
							String labelStr = stringBuilderLabel.toString().substring(0,
									stringBuilderLabel.length() - 1);
							if (StringUtil.isEmpty(word.getNature())) {
								wordsContent.append(stringBuilder.append(word.getWord()).append("|").append(labelStr)
										+ "," + word.getFrequency() + "\r\n");
							} else {
								wordsContent.append(stringBuilder.append(word.getWord()).append("|").append(labelStr)
										+ "," + word.getNature() + "," + word.getFrequency() + "\r\n");
							}
						} else {
							if (StringUtil.isEmpty(word.getNature())) {
								wordsContent.append(word.getWord() + "," + word.getFrequency() + "\r\n");
							} else {
								wordsContent.append(
										word.getWord() + "," + word.getNature() + "," + word.getFrequency() + "\r\n");
							}
						}
					} else {
						if (StringUtil.isEmpty(word.getNature())) {
							wordsContent.append(word.getWord() + "," + word.getFrequency() + "\r\n");
						} else {
							wordsContent.append(
									word.getWord() + "," + word.getNature() + "," + word.getFrequency() + "\r\n");
						}

					}
				}
				String wordsContentStr = wordsContent.toString();
				byte[] wordsContentbyte = wordsContentStr.getBytes();
				byte[] wordsContentbyteCoding = new byte[wordsContentbyte.length + 3];
				wordsContentbyteCoding[0] = -17;
				wordsContentbyteCoding[1] = -69;
				wordsContentbyteCoding[2] = -65;
				for (int i = 0; i < wordsContentbyte.length; i++) {
					wordsContentbyteCoding[i + 3] = wordsContentbyte[i];
				}

				out.write(wordsContentbyteCoding);
			} else if ("sentiment".equals(dict.getFormat())) {

				List<WordSentiment> words = wordSentimentRepository.findAllByDictionaryId(dict.getId());

				response.setContentType("application/x-msdownload");
				response.addHeader("Content-Disposition", "attachment;filename=\""
						+ new String((dict.getName() + ".txt").getBytes("GBK"), "ISO8859_1") + "\"");
				out = response.getOutputStream();

				StringBuilder wordsContent = new StringBuilder();
				for (WordSentiment word : words) {
					StringBuilder stringBuilder = new StringBuilder();
					if (!StringUtil.isEmpty(word.getNature()) && null != word.getGrade()) {
						wordsContent.append(stringBuilder.append(word.getWord()) + "," + word.getNature() + ","
								+ word.getGrade() + "\r\n");
					} else if (StringUtil.isEmpty(word.getNature()) && null != word.getGrade()) {
						wordsContent.append(stringBuilder.append(word.getWord()) + "," + word.getGrade() + "\r\n");
					} else if (!StringUtil.isEmpty(word.getNature()) && null == word.getGrade()) {
						wordsContent.append(stringBuilder.append(word.getWord()) + "," + word.getNature() + "\r\n");
					} else {
						wordsContent.append(stringBuilder.append(word.getWord()) + "\r\n");
					}
				}
				String wordsContentStr = wordsContent.toString().replaceAll("[　*| *| *|//s*]*", "");
				byte[] wordsContentbyte = wordsContentStr.getBytes();
				byte[] wordsContentbyteCoding = new byte[wordsContentbyte.length + 3];
				wordsContentbyteCoding[0] = -17;
				wordsContentbyteCoding[1] = -69;
				wordsContentbyteCoding[2] = -65;
				for (int i = 0; i < wordsContentbyte.length; i++) {
					wordsContentbyteCoding[i + 3] = wordsContentbyte[i];
				}

				out.write(wordsContentbyteCoding);

			}
			LogManager.Debug("Download dictionary library.");

		} catch (

		IOException e) {
			LogManager.Exception("DicDownloadController dicWordsDownload exception : ", e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					LogManager.Exception("DicDownloadController dicWordsDownload exception : ", e);
				}
			}
		}
		LogManager.Process("Process out controller: /nlap/admin/dic/download/dic/download");
		return null;
	}

}
