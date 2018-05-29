package com.bonc.uni.nlp.service.datasource;

import java.util.List;
import java.util.Map;

import com.bonc.uni.nlp.entity.datasource.DataSource;

/**
 * @ClassName:DataSourceService
 * @Package:com.bonc.text.service
 * @Description:TODO
 * @author:xmy
 */
public interface IDataSourceService {
	/**
	 * 添加数据源
	 * @param name
	 * @param ip
	 * @param port
	 * @param username
	 * @param password
	 * @param path
	 * @param dataSourceType
	 * @param dataType
	 * @return
	 */
	boolean addDataSource(String name, String ip, String port, String username, String password, String path,
			String dataSourceType, String dataType);

	/**
	 * 修改数据源
	 * @param id
	 * @param name
	 * @param ip
	 * @param port
	 * @param username
	 * @param password
	 * @param path
	 * @param dataSourceType
	 * @param dataType
	 * @return
	 */
	boolean editDataSource(String id, String name, String ip, String port, String username, String password,
			String path, String dataSourceType, String dataType);
	/**
	 * 删除数据源
	 * @param ids
	 * @return
	 */
	boolean deleteDataSource(String id);



	/**
	 * 导入数据源类型数据
	 */
	void initDataSourceType();

	/**
	 * 导入数据源内数据类型数据
	 */
	void initDataType();

	/**
	 * 搜索数据源
	 * @param searchWord
	 * @param pageNumber
	 * @param pageSize
	 * @param ascSort
	 * @return
	 */
	List<Map<String, Object>> listDataSources(String searchWord, int pageNumber, int pageSize, boolean ascSort);

	/**
	 * 获取数据源总数
	 * @return
	 */
	Long countDataSource();

}
