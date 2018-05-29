package com.bonc.uni.nlp.service.Impl.rule;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bonc.uni.nlp.dao.classify.ClassifyDependRepository;
import com.bonc.uni.nlp.dao.classify.ClassifyObjectRepository;
import com.bonc.uni.nlp.dao.classify.ClassifyRepository;
import com.bonc.uni.nlp.dao.corpus.CorpusSetRepository;
import com.bonc.uni.nlp.dao.model.DataSetRepository;
import com.bonc.uni.nlp.entity.classify.Classify;
import com.bonc.uni.nlp.entity.classify.ClassifyDependence;
import com.bonc.uni.nlp.entity.classify.ClassifyObject;
import com.bonc.uni.nlp.exception.AdminException;
import com.bonc.uni.nlp.service.rule.IClassifyManagementService;
import com.bonc.uni.nlp.utils.MyXmlUtil;
import com.bonc.usdp.odk.common.collection.CollectionUtil;
import com.bonc.usdp.odk.common.string.StringUtil;
import com.bonc.usdp.odk.logmanager.LogManager;

/**
 * 
 * @author zlq
 *
 */
@Service
@Transactional
public class ClassifyMgmtServiceImpl implements IClassifyManagementService {

	@Autowired
	ClassifyRepository classifyRepository;
	
	@Autowired
	ClassifyObjectRepository classifyObjectRepository;
	
	@Autowired
	ClassifyDependRepository dependencyRepository;
	
	@Autowired
	CorpusSetRepository corpusSetRepository;
	
	@Autowired
	DataSetRepository dataSetRepository;
	
	private static final String XML_CLASSIFIES = "classifies";

	private static final String XML_CLASSIFY = "classify";

	private static final String XML_NAME = "name";

	private static final String XML_OBJECTS = "objects";

	private static final String XML_OBJECT = "object";

	private static final String NAME = "name";
	
	private static final String CHILDREN = "children";
	@Override
	public List<Map<String, Object>> findAllClassify() {
		LogManager.Process("Process in service: the method findAllClassify of ClassifyMgmtServiceImpl");

		List<Map<String, Object>> rsList = new ArrayList<>();
		Sort sort = new Sort(Sort.Direction.DESC, "createTime");
		List<Classify> classifies = classifyRepository.findAll(sort);
		if (classifies.isEmpty()) {
			LogManager.Process("Process out service: the method findAllClassify of ClassifyMgmtServiceImpl");
			return rsList;
		}
		for (Classify classify : classifies) {
			Map<String, Object> tempMap = new HashMap<>();
			List<ClassifyObject> children = classifyObjectRepository.findAllByClassifyId(classify.getId(), sort);
			tempMap.put("name", classify.getName());
			Map<String, String> idMap = new HashMap<>();
			idMap.put("id", classify.getId());
			if (CollectionUtil.isEmpty(children)) {
				tempMap.put("isLeaf", true);
			} else {
				tempMap.put("isLeaf", false);
			}
			tempMap.put("expanded", false);
			tempMap.put("checked", false);
			tempMap.put("selected", false);
			tempMap.put("data", idMap);
			tempMap.put("disabled", true);
			tempMap.put("children", getChildrenList(classify.getId()));
			rsList.add(tempMap);
		}
		LogManager.Process("Process out service: the method findAllClassify of ClassifyMgmtServiceImpl");
		return rsList;
	}

	private List<Map<String, Object>> getChildrenList(String classifyId) {
		Sort sort = new Sort(Sort.Direction.DESC, "createTime");
		List<Map<String, Object>> rsList = new ArrayList<>();
		List<ClassifyObject> children = classifyObjectRepository.findAllByClassifyId(classifyId, sort);
		if (CollectionUtil.isEmpty(children)) {
			return rsList;
		}
		for (ClassifyObject classifyObject : children) {
			Map<String, Object> tempMap = new HashMap<>();
			Map<String, String> idMap = new HashMap<>();
			idMap.put("id", classifyId + ":" + classifyObject.getId());
			tempMap.put("name", classifyObject.getName());
			tempMap.put("isLeaf", true);
			tempMap.put("checked", false);
			tempMap.put("selected", false);
			tempMap.put("data", idMap);
			rsList.add(tempMap);
		}
		return rsList;
	}

	@Override
	public boolean addClassify(String classifyName, String preClassifyAndObjects, String objects) {
		LogManager.Process("Process in service: the method addClassify of ClassifyMgmtServiceImpl");
		// 添加数据到 nlap_classify 表中
		String postClassifyId = null;
		if (!existClassify(classifyName)) {
	        Date date = new Date();
	        Calendar cal = Calendar.getInstance();
	        cal.setTime(date);//date 换成已经已知的Date对象
	        cal.add(Calendar.HOUR_OF_DAY, 8);// before 8 hour
			// SysUser user = CurrentUserUtils.getInstance().getUser();
			Classify classify = new Classify();
			classify.setName(classifyName);
			classify.setUsed(false);
			classify.setCreateTime(cal.getTime());
			classify.setCreateUser("uni");
			classifyRepository.save(classify);
			postClassifyId = classify.getId();
		} else {
			LogManager.Debug("The classify of " + classifyName + " already exit !");
			throw new AdminException("该分类体系已经存在！");
		}
		if (!StringUtil.isEmpty(preClassifyAndObjects)) {
			// 有前置依赖的分类体系
			String[] preClassifyAndObjectArr = preClassifyAndObjects.split(";");
			for (String preClassifyAndObject : preClassifyAndObjectArr) {
				addDependence(postClassifyId, preClassifyAndObject);
			}
		}
		if (!StringUtil.isEmpty(objects)) {
			// 有创建分类对象
			String[] objectArr = objects.split(",");
			for (String object : objectArr) {
				if (!StringUtil.isEmpty(object.trim())) {
					addObject(postClassifyId, object.trim());
				}
			}
		}
		LogManager.Process("Process out service: the method addClassify of ClassifyMgmtServiceImpl");

		return true;
	}

	/**
	 * 添加依赖关系
	 * 
	 * @param postClassifyId
	 * @param preClassifyAndObjects
	 */
	private void addDependence(String postClassifyId, String preClassifyAndObject) {
		LogManager.Process("Process in service: the method addDependence of ClassifyMgmtServiceImpl");

		if (StringUtil.isEmpty(preClassifyAndObject) || 2 != preClassifyAndObject.split(":").length) {
			LogManager.debug("The preClassifyAndObjects not correct!");
			return;
		}
		String preClassifyId = preClassifyAndObject.split(":")[0];
		String preObjectId = preClassifyAndObject.split(":")[1];
		if (StringUtil.isEmpty(preClassifyId) || StringUtil.isEmpty(preObjectId)) {
			LogManager.debug("PreClassifyId or preObjectId is empty!");
			return;
		}
		if (null == classifyRepository.getOne(preClassifyId) || null == classifyObjectRepository.getOne(preObjectId)) {
			LogManager.debug("The preClassifyId or Object not exit ！");
			return;
		}
		ClassifyDependence dependence = new ClassifyDependence();
		dependence.setPostClassifyId(postClassifyId);
		dependence.setPreClassifyId(preClassifyId);
		dependence.setPreObjectId(preObjectId);
		dependencyRepository.save(dependence);

		LogManager.Process("Process out service: the method addDependence of ClassifyMgmtServiceImpl");
	}

	/**
	 * 添加分类体系对象
	 * 
	 * @param object
	 */
	private void addObject(String classifyId, String object) {
		LogManager.Process("Process in service: the method addObjects of ClassifyMgmtServiceImpl");
		// SysUser user = CurrentUserUtils.getInstance().getUser();

		if (!existObject(classifyId, object)) {
	        Date date = new Date();
	        Calendar cal = Calendar.getInstance();
	        cal.setTime(date);//date 换成已经已知的Date对象
	        cal.add(Calendar.HOUR_OF_DAY, 8);// before 8 hour

			ClassifyObject newObject = new ClassifyObject();
			newObject.setName(object);
			newObject.setClassifyId(classifyId);
			newObject.setCreateTime(cal.getTime());
			newObject.setCreateUser("uni");
			classifyObjectRepository.save(newObject);

		} else {
			throw new AdminException("该分类体系下已经存在同名对象'" + object + "'!");
		}

		LogManager.Process("Process out service: the method addObjects of ClassifyMgmtServiceImpl");
	}

	/**
	 * 判断分类体系下是否存在同名对象
	 * 
	 * @param classifyId
	 * @param object
	 * @return
	 */
	private boolean existObject(String classifyId, String object) {

		if (StringUtil.isEmpty(object)
				|| null != classifyObjectRepository.findOneByNameAndClassifyId(object, classifyId)) {
			LogManager.debug("该分类体系下已经存在同名对象！");

			return true;
		}
		return false;
	}

	/**
	 * 判断同名分类体系是否存在
	 * 
	 * @param classifyId
	 * @return
	 */
	private boolean existClassify(String classifyName) {
		if (StringUtil.isEmpty(classifyName) || null != classifyRepository.findOneByName(classifyName)) {
			LogManager.debug("The classify already exist !");
			return true;
		}
		return false;
	}

	@Override
	public void deleteClassify(String classifyId) {
		// 1.删除依赖
		if (null == classifyRepository.getOne(classifyId)) {
			LogManager.debug("The classify not exist !");
			throw new AdminException("选择的分类体系不存在！");
		}
		if (classifyRepository.getOne(classifyId).isUsed()) {
			LogManager.debug("The classify is in used !");
			throw new AdminException("分类体系  ：" + classifyRepository.getOne(classifyId).getName() + "正在被使用，删除失败！");
		}
		dependencyRepository.deleteByPreClassifyId(classifyId);
		dependencyRepository.deleteByPostClassifyId(classifyId);
		// 2.删除体系下的所有对象
		classifyObjectRepository.deleteByClassifyId(classifyId);
		// 2.删除分类体系
		classifyRepository.delete(classifyId);
	}

	/**
	 * 判断是否为叶子对象
	 * 
	 * @param objectId
	 * @return
	 */
	private boolean leafObject(String objectId) {
		List<ClassifyDependence> dependences = dependencyRepository.findAllByPreObjectId(objectId);
		if (CollectionUtil.isEmpty(dependences)) {
			return true;
		}
		return false;
	}

	@Override
	public Map<String, Object> generateTree(String classifyId) {
		LogManager.Process("Process in service: the method generateTree of ClassifyMgmtServiceImpl");
		
		Map<String, Object> returnMap = new HashMap<>();
		Sort sort = new Sort(Sort.Direction.DESC, "createTime");
		Classify classify = classifyRepository.getOne(classifyId);
		returnMap.put(NAME, classify.getName());
		if (CollectionUtil.isEmpty(classifyObjectRepository.findAllByClassifyId(classifyId, sort))) {
			return returnMap;
		}
		returnMap.put(CHILDREN, traversal(classifyId));
		
		LogManager.Process("Process out service: the method generateTree of ClassifyMgmtServiceImpl");
		return returnMap;
	}

	/**
	 * 分类体系遍历
	 * @param classifyId
	 * @return
	 */
	private List<Map<String, Object>> traversal(String classifyId) {
		LogManager.Process("Process in service: the method traversal of ClassifyMgmtServiceImpl");

		List<Map<String, Object>> returnList = new ArrayList<>();
		Sort sort = new Sort(Sort.Direction.DESC, "createTime");
		List<ClassifyObject> objects = classifyObjectRepository.findAllByClassifyId(classifyId, sort);
		
		for (ClassifyObject object : objects) {
			Map<String, Object> tempMap = new HashMap<>();
			tempMap.put(NAME, object.getName());
			if (!leafObject(object.getId())) {
				//非叶子节点，遍历子分类体系
				List<ClassifyDependence> dependences = dependencyRepository
						.findAllByPreClassifyIdAndPreObjectId(classifyId, object.getId());
				List<Map<String, Object>> children = new ArrayList<>();
				for (ClassifyDependence dependence : dependences) {
					children.addAll(traversal(dependence.getPostClassifyId()));
				}
				tempMap.put(CHILDREN, children);
			}
			returnList.add(tempMap);
		}
		
		LogManager.Process("Process out service: the method traversal of ClassifyMgmtServiceImpl");
		return returnList;
	}
	/**
	 * 获取使用分类体系的全部语料集
	 * @param classifyId
	 * @return
	 */
	private List<String> getCorpusSetByClassify(String classifyId) {
		List<String> name = new ArrayList<>();
		name = corpusSetRepository.findNameByClassifyId(classifyId);
		if (CollectionUtil.isEmpty(name)) {
			name = dataSetRepository.findNameByClassifyId(classifyId);
		}
		return name;
	}
	@Override
	public List<Map<String, Object>> allClassifyInfo() {
		LogManager.Process("Process in service: the method allClassifyInfo of ClassifyMgmtServiceImpl");
		List<Map<String, Object>> rsList = new ArrayList<>();
		Sort sort = new Sort(Sort.Direction.DESC, "createTime");
		List<Classify> classifies = classifyRepository.findAll(sort);
		if (classifies.isEmpty()) {
			LogManager.Process("Process out service: the method findAllClassify of ClassifyMgmtServiceImpl");
			return rsList;
		}
		for (Classify classify : classifies) {
			Map<String, Object> tempMap = new HashMap<>();
			tempMap.put("classify", classify);
			tempMap.put("object", getObjectWithStatus(classify.getId()));
			tempMap.put("pre", getPreClassifyAndObject(classify.getId()));
			tempMap.put("descendantClassify", getDescendantClassify(classify.getId()));
			tempMap.put("corpusSet", getCorpusSetByClassify(classify.getId()));
			rsList.add(tempMap);
		}
		LogManager.Process("Process out service: the method findAllClassify of ClassifyMgmtServiceImpl");
		return rsList;
	}

	/**
	 * 获取带可修改属性的对象列表
	 * 
	 * @param classifyId
	 * @return
	 */
	private List<Map<String, Object>> getObjectWithStatus(String classifyId) {
		List<Map<String, Object>> rsList = new ArrayList<>();
		List<ClassifyObject> objects = classifyObjectRepository.findObjectsIdAndName(classifyId);
		for (ClassifyObject object : objects) {
			Map<String, Object> tempMap = new HashMap<>();
			String id = object.getId();
			tempMap.put("id", id);
			tempMap.put("classifyId", classifyId);
			tempMap.put("name", object.getName());
			tempMap.put("editable", dependencyRepository.findAllByPreObjectId(id).isEmpty() ? true : false);
			rsList.add(tempMap);
		}
		return rsList;
	}

	/**
	 * 获取某分类体系的后代分类体系
	 * 
	 * @param classifyId
	 * @return
	 */
	private Set<String> getDescendantClassify(String classifyId) {
		LogManager.Process("Process in service: the method getDescendantClassify of ClassifyMgmtServiceImpl");
		Set<String> rsSet = new HashSet<>();
		rsSet.add(classifyId);
		if (CollectionUtil.isEmpty(dependencyRepository.findAllByPreClassifyId(classifyId))) {
			LogManager.Process("Process out service: the method getDescendantClassify of ClassifyMgmtServiceImpl");
			return rsSet;
		}
		rsSet.addAll(traversalChildClassify(classifyId));
		LogManager.Process("Process out service: the method getDescendantClassify of ClassifyMgmtServiceImpl");
		return rsSet;
	}

	/**
	 * 递归获取后代分类体系
	 * 
	 * @param classifyId
	 * @return
	 */
	private Set<String> traversalChildClassify(String classifyId) {
		LogManager.Process("Process in service: the method traversalChildClassify of ClassifyMgmtServiceImpl");
		Set<String> rsSet = new HashSet<>();
		List<ClassifyDependence> dependences = dependencyRepository.findAllByPreClassifyId(classifyId);
		for (ClassifyDependence dependence : dependences) {
			String postClassifyId = dependence.getPostClassifyId();
			rsSet.add(postClassifyId);
			rsSet.addAll(traversalChildClassify(postClassifyId));
		}
		LogManager.Process("Process out service: the method updateObjects of ClassifyMgmtServiceImpl");
		return rsSet;
	}

	/**
	 * 获取前置分类体系及前置对象
	 * 
	 * @param classifyId
	 * @return
	 */
	private List<Map<String, String>> getPreClassifyAndObject(String classifyId) {
		List<Map<String, String>> rsList = new ArrayList<>();
		List<ClassifyDependence> dependences = dependencyRepository.findAllByPostClassifyId(classifyId);
		for (ClassifyDependence classifyDependence : dependences) {
			Map<String, String> tempMap = new HashMap<>();
			String preClassifyId = classifyDependence.getPreClassifyId();
			String preObjectId = classifyDependence.getPreObjectId();
			String preClassifyName = classifyRepository.getOne(preClassifyId).getName();
			String preObjectName = classifyObjectRepository.getOne(preObjectId).getName();
			tempMap.put("preClassifyId", preClassifyId);
			tempMap.put("preClassifyName", preClassifyName);
			tempMap.put("preObjectId", preClassifyId + ":" + preObjectId);
			tempMap.put("preObjectName", preObjectName);
			rsList.add(tempMap);
		}
		return rsList;
	}

	@Override
	public boolean updateClassifyName(String classifyId, String newName) {
		if (null == classifyRepository.getOne(classifyId)) {
			throw new AdminException("该分类体系不存在！");
		}
		classifyRepository.updateClassifyName(newName, classifyId);
		return true;
	}

	@Override
	public void addObjects(String classifyId, String objects) {
		LogManager.Process("Process in service: the method addObjects of ClassifyMgmtServiceImpl");
		if (null == classifyRepository.getOne(classifyId)) {
			LogManager.error("The classifyId " + classifyId + " not exist!");
			throw new AdminException("分类体系不存在！");
		}
		String[] objectsArr = objects.split(",");
		for (String object : objectsArr) {
			addObject(classifyId, object.trim());
		}
		LogManager.Process("Process out service: the method addObjects of ClassifyMgmtServiceImpl");
	}

	@Override
	public void deleteObjects(String classifyId, String objectIds) {
		if (null == classifyRepository.getOne(classifyId)) {
			LogManager.error("The classifyId " + classifyId + " not exist!");
			throw new AdminException("分类体系不存在！");
		}
		String[] objectIdArr = objectIds.split(",");
		for (String objectId : objectIdArr) {
			deleteObject(objectId);
		}
	}

	/**
	 * 删除分类体系对象
	 * 
	 * @param objectId
	 */
	private void deleteObject(String objectId) {
		if (null == classifyObjectRepository.findOne(objectId)) {
			LogManager.debug("The object not exist!");
		}
		if (!dependencyRepository.findAllByPreObjectId(objectId).isEmpty()) {
			LogManager.debug("Can not delete the object!");
		}
		;
		classifyObjectRepository.delete(objectId);
	}

	/**
	 * 判断是否有子分类体系
	 * 
	 * @param classifyId
	 * @return
	 */
	private boolean hasChildClassify(String classifyId) {
		if (CollectionUtil.isEmpty(dependencyRepository.findAllByPreClassifyId(classifyId))) {
			return false;
		}
		return true;
	}

	@Override
	public void updateObjects(String classifyId, String updateObjects) {
		LogManager.Process("Process in service: the method updateObjects of ClassifyMgmtServiceImpl");
		String[] objectsIdAndNameArr = updateObjects.split(";");
		for (String objectIdAndName : objectsIdAndNameArr) {
			updateObject(classifyId, objectIdAndName);
		}
		LogManager.Process("Process out service: the method updateObjects of ClassifyMgmtServiceImpl");

	}

	/**
	 * 更新分类对象
	 * 
	 * @param classifyId
	 * @param objectIdAndName
	 */
	private void updateObject(String classifyId, String objectIdAndName) {
		if (StringUtil.isEmpty(objectIdAndName) || 2 != objectIdAndName.split(":").length) {
			LogManager.debug("The objectIdAndName format is not correct!");
			return;
		}
		String objectId = objectIdAndName.split(":")[0];
		String objectName = objectIdAndName.split(":")[1].trim();
		if (StringUtil.isEmpty(objectId) || StringUtil.isEmpty(objectName)) {
			LogManager.debug("ObjectId or objectName is empty!");
			return;
		}
		if (null == classifyRepository.getOne(classifyId) || null == classifyObjectRepository.getOne(objectId)) {
			LogManager.debug("The classifyId or objectId not exist ！");
			return;
		}
		if (null == classifyObjectRepository.findOneByIdAndClassifyId(objectId, classifyId)) {
			LogManager.debug("The objectId not exit under the classifyId!");
			return;
		}
		if (!existObject(classifyId, objectName.trim())) {
			classifyObjectRepository.updateObjectName(objectName.trim(), objectId);
		} else {
			throw new AdminException("该分类体系下已经存在同名对象'" + objectName + "'!");
		}
	}

	@Override
	public void updateDependences(String classifyId, String dependences) {
		// 全部解除与本分类体系相关的前置依赖关系
		dependencyRepository.deleteByPostClassifyId(classifyId);
		String[] preClassifyAndObjectArr = dependences.split(";");
		for (String preClassifyAndObject : preClassifyAndObjectArr) {
			addDependence(classifyId, preClassifyAndObject);
		}
	}

	@Override
	public List<Map<String, Object>> searchByKeyWord(String keyWord) {
		List<Map<String, Object>> rsList = new ArrayList<>();
		keyWord = "%" + keyWord + "%";
		Sort sort = new Sort(Sort.Direction.DESC, "createTime");
		List<String> rsIds = new ArrayList<>();
		for (Classify classify : classifyRepository.findAllByNameLike(keyWord)) {
			rsIds.add(classify.getId());
		}
		for (ClassifyObject classifyObject : classifyObjectRepository.findAllByNameLike(keyWord)) {
			String classifyId = classifyObject.getClassifyId();
			if (!rsIds.contains(classifyId)) {
				rsIds.add(classifyId);
			}
		}
		List<Classify> classifies = classifyRepository.findAll(rsIds);
		Collections.sort(classifies, new Comparator<Classify>() {
			@Override
			public int compare(Classify c1, Classify c2) {
				Date dt1 = c1.getCreateTime();
				Date dt2 = c2.getCreateTime();
				if (dt1.getTime() > dt2.getTime()) {
					return -1;
				} else if (dt1.getTime() < dt2.getTime()) {
					return 1;
				} else {
					return 0;
				}
			}
		});
		for (Classify classify : classifies) {
			Map<String, Object> tempMap = new HashMap<>();
			tempMap.put("classify", classify);
			tempMap.put("object", classifyObjectRepository.findAllByClassifyId(classify.getId(), sort));
			tempMap.put("hasChildClassify", hasChildClassify(classify.getId()));
			tempMap.put("corpusSet", getCorpusSetByClassify(classify.getId()));
			tempMap.put("pre", getPreClassifyAndObject(classify.getId()));
			rsList.add(tempMap);
		}
		return rsList;
	}

	@Override
	public void exportClassify2XML(String[] classifyIdArr, HttpServletResponse response) {
		Sort sort = new Sort(Sort.Direction.DESC, "createTime");
		OutputStream out = null;
		response.setContentType("application/x-msdownload");
		try {
			response.addHeader("Content-Disposition",
					"attachment;filename=\"" + new String(("classify.xml").getBytes("GBK"), "ISO8859_1") + "\"");
			out = response.getOutputStream();
			StringBuilder xmlContent = new StringBuilder();
			xmlContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
			xmlContent.append("<" + XML_CLASSIFIES + ">\r\n");
			List<Classify> classifies = classifyRepository.findByIdIn(classifyIdArr, sort);
			for (Classify classify : classifies) {
				xmlContent.append("	<" + XML_CLASSIFY + ">\r\n");

				String classifyName = classify.getName();
				xmlContent.append("		<" + XML_NAME + ">" + classifyName + "</" + XML_NAME + ">\r\n");
				List<ClassifyObject> objects = classifyObjectRepository.findAllByClassifyId(classify.getId(), sort);
				xmlContent.append("		<" + XML_OBJECTS + ">\r\n");
				for (ClassifyObject classifyObject : objects) {
					String name = classifyObject.getName();
					xmlContent.append("			<" + XML_OBJECT + ">" + name + "</" + XML_OBJECT + ">\r\n");
				}
				xmlContent.append("		</" + XML_OBJECTS + ">\r\n");
				xmlContent.append("	</" + XML_CLASSIFY + ">\r\n");
			}
			xmlContent.append("</" + XML_CLASSIFIES + ">\r\n");
			out.write(xmlContent.toString().getBytes());
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

	@Override
	public Boolean existSameClassify(String classifyId, String classifyName) {
		Boolean result = false;
		if (null == classifyRepository.getOne(classifyId)) {
			LogManager.error("The classifyId " + classifyId + " not exist!");
			throw new AdminException("分类体系不存在！");
		}
		if (null != classifyRepository.findOneByName(classifyName)) {
			String id = classifyRepository.findOneByName(classifyName).getId();
			if (!classifyId.equals(id)) {
				result = true;
				throw new AdminException("同名分类体系已经存在！");
			}
		}
		return result;
	}

	@Override
	public boolean importXML(MultipartFile file) {
		boolean flag = false;
		Map<String, Object> rsMap = new HashMap<>();
		try {
			rsMap = MyXmlUtil.XmlToMap(file);
		} catch (Exception e) {
			LogManager.Exception(e);
		}
		if (null == rsMap.get(XML_CLASSIFIES)) {
			throw new AdminException("上传文件格式不正确，缺少<classifies>标签，请参照模板！");
		}
		Map<String, Object> classifyMap = (Map<String, Object>) rsMap.get(XML_CLASSIFIES);
		if (null == classifyMap.get(XML_CLASSIFY)) {
			throw new AdminException("上传文件格式不正确，缺少<classify>标签，请参照模板！");
		}
		List<Map<String, Object>> classifyList = new ArrayList<>();
		if (classifyMap.get(XML_CLASSIFY) instanceof java.util.List) {
			// 有多个classify
			classifyList.addAll((List<Map<String, Object>>) classifyMap.get(XML_CLASSIFY));
		} else {
			// 仅有一个 classify
			classifyList.add((Map<String, Object>) classifyMap.get(XML_CLASSIFY));
		}
		for (Map<String, Object> map : classifyList) {
	        Date date = new Date();
	        Calendar cal = Calendar.getInstance();
	        cal.setTime(date);//date 换成已经已知的Date对象
	        cal.add(Calendar.HOUR_OF_DAY, 8);// before 8 hour
	        
			if (null == map.get("name")) {
				throw new AdminException("上传文件格式不正确，缺少<name>标签，请参照模板！");
			}
			String classifyName = String.valueOf(map.get("name"));
			if (StringUtil.isEmpty(classifyName)) {
				continue;
			}
			Classify classify = new Classify();
			if (existClassify(classifyName)) {
				// 同名分类体系已经存在
				classify = classifyRepository.findOneByName(classifyName);
				flag = true;
			} else {
				// 不存在同名分类体系
				classify.setCreateUser("uni");
				classify.setName(classifyName);
				classify.setCreateTime(cal.getTime());
				classifyRepository.save(classify);
			}
			if (null == map.get("objects")) {
				throw new AdminException("上传文件格式不正确，缺少<objects>标签，请参照模板！");
			}
			Map<String, Object> objectsMap = (Map<String, Object>) map.get("objects");
			if (null == objectsMap.get("object")) {
				throw new AdminException("上传文件格式不正确，缺少<object>标签，请参照模板！");
			}
			List<Object> objectList = new ArrayList<>();
			if (objectsMap.get("object") instanceof java.util.List) {
				objectList.addAll((List<Object>) objectsMap.get("object"));
			} else {
				objectList.add(objectsMap.get("object"));
			}
			for (Object object : objectList) {
				String objectStr = String.valueOf(object);
				if (StringUtil.isEmpty(objectStr) || existObject(classify.getId(), objectStr)) {
					continue;
				}
				ClassifyObject newObject = new ClassifyObject();
				newObject.setCreateUser("uni");
				newObject.setName(objectStr);
				newObject.setClassifyId(classify.getId());
				newObject.setCreateTime(cal.getTime());
				classifyObjectRepository.save(newObject);
			}
		}
		return flag;
	}
}
