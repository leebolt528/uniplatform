package com.bonc.uni.nlp.service.Impl.label;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bonc.uni.nlp.dao.DictionaryRepository;
import com.bonc.uni.nlp.dao.WordOrdinaryRepository;
import com.bonc.uni.nlp.dao.label.LabelRepository;
import com.bonc.uni.nlp.dao.label.LabelWordRelationRepository;
import com.bonc.uni.nlp.entity.dic.Dictionary;
import com.bonc.uni.nlp.entity.dic.WordOrdinary;
import com.bonc.uni.nlp.entity.label.Label;
import com.bonc.uni.nlp.entity.label.LabelWordRelation;
import com.bonc.uni.nlp.exception.AdminException;
import com.bonc.uni.nlp.service.label.ILabelMgmtService;
import com.bonc.uni.nlp.utils.Encoding;
import com.bonc.uni.nlp.utils.PathUtil;
import com.bonc.uni.nlp.utils.ZipUtil;
import com.bonc.usdp.odk.common.exception.PathNotFoundException;
import com.bonc.usdp.odk.common.file.FileUtil;
import com.bonc.usdp.odk.common.string.StringUtil;
import com.bonc.usdp.odk.logmanager.LogManager;

/**
 * @author : GaoQiuyuer
 * @version: 2017年12月5日 下午2:42:51
 */
@Service
public class LabelMgmtServiceImpl implements ILabelMgmtService {

	@Autowired
	LabelRepository labelRepository;
	@Autowired
	LabelWordRelationRepository labelWordRelationRepository;
	@Autowired
	WordOrdinaryRepository wordOrdinaryRepository;
	@Autowired
	DictionaryRepository dictionaryRepository;

	private static String LABEL_SAVE_PATH;

	static {
		try {
			LABEL_SAVE_PATH = PathUtil.getResourcesPath() + File.separator + "label";
			File pluginDir = new File(LABEL_SAVE_PATH);
			if (!pluginDir.exists()) {
				pluginDir.mkdirs();
			}
		} catch (PathNotFoundException e) {
			LogManager.Exception(e);
		}
	}

	@Override
	public Map<String, Object> listLabels(String keyword, int pageIndex, int pageSize, boolean ascSort) {
		Map<String, Object> labelsInfo = new HashMap<>();
		Sort sort = null;
		if (ascSort) {
			sort = new Sort(Sort.Direction.ASC, "createTime");
		} else {
			sort = new Sort(Sort.Direction.DESC, "createTime");
		}
		Pageable pageable = new PageRequest(pageIndex - 1, pageSize, sort);
		List<Label> labels = new ArrayList<>();
		int labelsNumber = 0;
		if (StringUtil.isEmpty(keyword)) {
			labels = labelRepository.findAll(pageable).getContent();
			labelsNumber = (int) labelRepository.count();
		} else {
			keyword = "%" + keyword + "%";
			labels = labelRepository.findAllByNameLike(keyword, pageable);
			labelsNumber = labelRepository.count(keyword);
		}
		List<Object> labelInfo = new ArrayList<>();
		Map<String, Object> labelInfoMap = new HashMap<>();
		for (Label label : labels) {
			labelInfoMap = new HashMap<>();
			String labelId = label.getId();
			List<LabelWordRelation> labelWordRelations = labelWordRelationRepository.findAllByLabelId(labelId);
			int wordsNumber = 0;
			if (null != labelWordRelations) {
				List<String> wordIds = new ArrayList<>();
				for (LabelWordRelation labelWordRelation : labelWordRelations) {
					String wordId = labelWordRelation.getWordId();
					wordIds.add(wordId);
				}
				if (wordIds.size() > 0) {
					wordsNumber = (int) wordOrdinaryRepository.countByIdsIn(wordIds);
				}
			}
			labelInfoMap.put("id", labelId);
			labelInfoMap.put("name", label.getName());
			labelInfoMap.put("createTime", label.getCreateTime());
			labelInfoMap.put("status", label.getStatus());
			labelInfoMap.put("updateTime", label.getUpdateTime());
			labelInfoMap.put("wordsNumber", wordsNumber);
			labelInfo.add(labelInfoMap);
		}
		labelsInfo.put("labels", labelInfo);
		labelsInfo.put("numbers", labelsNumber);

		return labelsInfo;
	}

	@Override
	public boolean validateLabelExists(String labelName) {
		if (StringUtil.isEmpty(labelName)) {
			return false;
		}
		Label existedLabel = labelRepository.findOneByName(labelName);
		if (null != existedLabel) {
			return true;
		}
		return false;
	}

	@Override
	public boolean addLabel(String names) {
		String[] namesArr = names.split(",");
		StringBuilder labels = new StringBuilder();
		for (String name : namesArr) {
			labels.append(name.trim()).append("_");
		}
		String labelsStr = labels.toString();
		labelsStr = labelsStr.substring(0, labelsStr.length() - 1);
		if (validateLabelExists(labelsStr)) {
			throw new AdminException("该标签名称已存在，请重新输入");
		}

		Label label = new Label();
		label.setCreateTime(new Date());
		label.setName(labelsStr);
//		label.setUserId(CurrentUserUtils.getInstance().getUser().getId());
		labelRepository.save(label);

		return true;
	}

	@Override
	public boolean editLabel(String labelId, String newNames) {
		Label label = labelRepository.findOne(labelId);
		if (null == label) {
			throw new AdminException("编辑失败，该标签不存在");
		}
		String[] namesArr = newNames.split(",");
		StringBuilder labels = new StringBuilder();
		for (String name : namesArr) {
			labels.append(name.trim()).append("_");
		}
		String labelsStr = labels.toString();
		labelsStr = labelsStr.substring(0, labelsStr.length() - 1);
		if (validateLabelExists(labelsStr) && !label.getName().equals(labelsStr)) {
			throw new AdminException("该节点名称已存在，请重新输入");
		}

		label.setName(labelsStr);
		label.setUpdateTime(new Date());
		// label.setUserId(userId);
		labelRepository.save(label);

		return true;
	}

	@Override
	public boolean delLabel(String labelIds) {
		String[] labelIdsArr = labelIds.split(",");
		for (String labelId : labelIdsArr) {
			Label label = labelRepository.findOne(labelId);
			if (null == label) {
				continue;
			}
			if (1 == label.getStatus()) {
				continue;
			}
			labelRepository.delete(labelId);
		}

		return true;
	}

	@Override
	public Map<String, Object> labelInfo(String labelId, String keyword, int pageIndex, int pageSize, boolean ascSort) {
		Sort sort = null;
		if (ascSort) {
			sort = new Sort(Sort.Direction.ASC, "createTime");
		} else {
			sort = new Sort(Sort.Direction.DESC, "createTime");
		}
		Pageable pageable = new PageRequest(pageIndex - 1, pageSize, sort);

		Label label = labelRepository.findOne(labelId);
		if (null == label) {
			throw new AdminException("获取词列表失败，该标签不存在");
		}
		Map<String, Object> wordsMap = new HashMap<>();
		List<LabelWordRelation> labelWordRelations = labelWordRelationRepository.findAllByLabelId(labelId);
		List<Object> words = new ArrayList<>();
		Map<String, Object> wordsInfoMap = new HashMap<>();
		List<WordOrdinary> wordFields = new ArrayList<>();
		int wordsNumber = 0;
		List<String> wordIds = new ArrayList<>();
		for (LabelWordRelation labelWordRelation : labelWordRelations) {
			String wordId = labelWordRelation.getWordId();
			wordIds.add(wordId);
		}
		if (StringUtil.isEmpty(keyword)) {
			wordFields = wordOrdinaryRepository.findAllByIdsIn(wordIds, pageable);
			for (WordOrdinary wordField : wordFields) {
				wordsInfoMap = new HashMap<>();
				wordsInfoMap.put("wordName", wordField.getWord());
				wordsInfoMap.put("nature", wordField.getNature());
				String dicId = wordField.getDictionaryId();
				Dictionary dictionary = dictionaryRepository.findOne(dicId);
				wordsInfoMap.put("dicName", dictionary.getName());
				words.add(wordsInfoMap);
			}
			if (wordIds.size() > 0) {
				wordsNumber = (int) wordOrdinaryRepository.countByIdsIn(wordIds);
			}
		} else {
			keyword = "%" + keyword + "%";
			wordFields = wordOrdinaryRepository.findAllByNameLike(wordIds, keyword, pageable);
			for (WordOrdinary wordField : wordFields) {
				wordsInfoMap = new HashMap<>();
				wordsInfoMap.put("wordName", wordField.getWord());
				wordsInfoMap.put("nature", wordField.getNature());
				String dicId = wordField.getDictionaryId();
				Dictionary dictionary = dictionaryRepository.findOne(dicId);
				wordsInfoMap.put("dicName", dictionary.getName());
				words.add(wordsInfoMap);
			}
			if (wordIds.size() > 0) {
				wordsNumber = wordOrdinaryRepository.countByNameLike(wordIds, keyword);
			}
		}
		wordsMap.put("words", words);
		wordsMap.put("wordsNumber", wordsNumber);
		wordsMap.put("labelName", label.getName());

		return wordsMap;
	}

	@Override
	public void downloadLable(String labelId, String fileName, HttpServletResponse response) {
		String saveFileDir = LABEL_SAVE_PATH;
		File pathDir = new File(saveFileDir);
		if (!pathDir.exists()) {
			pathDir.mkdirs();
		}
		Label label = labelRepository.findOne(labelId);
		String labelName = label.getName();
		List<LabelWordRelation> labelWordRelations = labelWordRelationRepository.findAllByLabelId(labelId);
		List<String> dicIds = new ArrayList<>();
		List<String> wordIds = new ArrayList<>();
		for (LabelWordRelation labelWordRelation : labelWordRelations) {
			String wordId = labelWordRelation.getWordId();
			if (!wordIds.contains(wordId)) {
				wordIds.add(wordId);
			}
		}
		for (String wordId : wordIds) {
			WordOrdinary wordField = wordOrdinaryRepository.findOne(wordId);
			if (!dicIds.contains(wordField.getDictionaryId())) {
				dicIds.add(wordField.getDictionaryId());
			}
		}
		String wordsContentStr = null;
		for (String dicId : dicIds) {
			StringBuilder wordsContent = new StringBuilder();
			// 一个词典 一个文件
			Dictionary dic = dictionaryRepository.findOne(dicId);
			String dicName = dic.getName();
			List<WordOrdinary> wordOrdinaries = wordOrdinaryRepository.findAllByDicIdAndWordIdIn(dicId, wordIds);
			List<String> words = new ArrayList<>();
			for (WordOrdinary wordOrdinary : wordOrdinaries) {
				words.add(wordOrdinary.getWord());
			}
			for (String word : words) {
				wordsContent.append(word + "\r\n");
			}
			wordsContentStr = wordsContent.toString();
			String saveFileName = LABEL_SAVE_PATH + File.separator + labelName + File.separator + dicName + ".txt";
			File file = new File(saveFileName);
			if (!file.exists()) {
				file.getParentFile().mkdirs();
			}
			try {
				file.createNewFile();
				FileOutputStream outputStream = new FileOutputStream(file);
				outputStream.write(wordsContentStr.getBytes());
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// 根据path下载
		if (dicIds.size()>1) {
			response.setContentType("APPLICATION/OCTET-STREAM");
			try {
				response.addHeader("Content-Disposition",
						"attachment;filename=\"" + new String(("labelWords.zip").getBytes("GBK"), "ISO8859_1") + "\"");
				ZipOutputStream out = new ZipOutputStream(response.getOutputStream());
				ZipUtil.doCompress(saveFileDir, out);

				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			FileUtil.deleteDirectory(saveFileDir);
		}else {
			// 该标签下只有一个词典的词
			StringBuilder wordsContentOne = new StringBuilder();
			OutputStream out = null;
			try {
				response.setContentType("application/x-msdownload");
				response.addHeader("Content-Disposition",
						"attachment;filename=\"" + new String((labelName + "Words.txt").getBytes("GBK"), "ISO8859_1") + "\"");
			} catch (UnsupportedEncodingException e1) {
				LogManager.Exception(e1);
			}
			List<WordOrdinary> wordOrdinaries = wordOrdinaryRepository.findAllByDicIdAndWordIdIn(dicIds.get(0), wordIds);
			List<String> words = new ArrayList<>();
			for (WordOrdinary wordOrdinary : wordOrdinaries) {
				words.add(wordOrdinary.getWord());
			}
			for (String word : words) {
				wordsContentOne.append(word + "\r\n");
			}
			try {
				out = response.getOutputStream();
				out.write(wordsContentOne.toString().getBytes());
			} catch (IOException e) {
				LogManager.Exception(e);
			}
		}
	}

	@Override
	public List<Integer> uploadLabels(MultipartFile[] files) {
		List<Integer> num = new ArrayList<>();
		int labelNum = 0;
		int totalNum = 0;
		for (MultipartFile file : files) {
			try {
				if (!"UTF-8".equals(Encoding.tryEncoding(file))) {
					throw new AdminException("请上传utf-8格式的文件");
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				InputStream inStream = file.getInputStream();
				inStream = file.getInputStream();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			if (file.isEmpty()) {
				continue;
			}
			File fileF = null;
			try {
				fileF = File.createTempFile("tmp", null);
				file.transferTo(fileF);
				fileF.deleteOnExit();
			} catch (IOException e) {
				e.printStackTrace();
			}
			InputStreamReader reader;
			try {
				// 建立一个输入流对象reader
				reader = new InputStreamReader(new FileInputStream(fileF));
				BufferedReader br = new BufferedReader(reader);
				String labelName = null;
				// 建立一个对象，它把文件内容转成计算机能读懂的语言
				while ((labelName = br.readLine()) != null) {
					if (!labelName.isEmpty()) {
						totalNum += 1;
						if (validateLabelExists(labelName)) {
							continue;
						}
						if (StringUtil.isEmpty(labelName)) {
							continue;
						}
						labelNum += 1;
						Label label = new Label();
						label.setName(labelName.trim());
						label.setCreateTime(new Date());
						// label.setUserId(userId);
						labelRepository.save(label);
					}
				}
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		num.add(labelNum);
		num.add(totalNum);

		return num;
	}

	@Override
	public void downloadLables(String labelIds, String fileName, HttpServletResponse response) {
		if (null == labelIds || 0 == labelIds.length()) {
			throw new AdminException("请选择要导出的标签");
		}
		String[] labelIdsArr = labelIds.split(",");
		StringBuilder xmlContent = new StringBuilder();
		for (String labelId : labelIdsArr) {
			Label label = labelRepository.findOne(labelId);
			xmlContent.append(label.getName() + "\r\n");
		}
		OutputStream out = null;
		try {
			response.setContentType("application/x-msdownload");
			response.addHeader("Content-Disposition",
					"attachment;filename=\"" + new String(("labels.txt").getBytes("GBK"), "ISO8859_1") + "\"");
			out = response.getOutputStream();
			out.write(xmlContent.toString().getBytes());
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					LogManager.Exception(e);
				}
			}
		}
	}

}
