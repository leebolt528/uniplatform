package com.bonc.uni.nlp.service.datasource;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.bonc.uni.nlp.entity.plugin.PluginType;

/**
 * @ClassName:PluginService
 * @Package:com.bonc.text.service
 * @Description:TODO
 * @author:Gao Qiuyue
 * @date:2017年8月13日 上午11:17:47
 */
public interface IPluginService {
	/**
	 * <p>
	 * Title: addPlugin
	 * </p>
	 * <p>
	 * Description: 添加plugin
	 * </p>
	 * 
	 * @Author:xmy
	 * @param name
	 *            名称
	 * @param file
	 *            文件
	 * @param classNames
	 *            类型和对应的全类名：type1:className1;type2:className2
	 * @return
	 */
	boolean addPlugin(String name, MultipartFile file, String classNames);

	/**
	 * 修改Plugin
	 * @param id
	 * @param name
	 * @param file
	 * @param classNames
	 * @return
	 */
	boolean editPlugin(String id, String name, MultipartFile file, String classNames);

	

	/**
	 * 删除Plugin
	 * @param ids
	 * @return
	 */
	boolean deletePlugins(String ids);

	/**
	 * 搜索Plugin
	 * 
	 * @param searchWord
	 * @param pageIndex
	 * @param pageSize
	 * @param ascSort
	 * @return
	 */
	List<Object> listPlugin(String searchWord, int pageIndex, int pageSize, boolean ascSort);

	/**
	 * 获取Plugin总数
	 * @return
	 */
	Long countPlugin();

	/**
	 * PluginType插入数据
	 */
	void initPluginType();

	/**
	 * 获取所有Plugin
	 * @return
	 */
	List<PluginType> listPluginType();

	/**
	 * 判断Plugin是否被任务运用
	 * @param id
	 * @return
	 */
	boolean notUsed(String id);
}
