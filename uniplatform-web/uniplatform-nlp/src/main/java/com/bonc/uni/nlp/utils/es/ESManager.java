package com.bonc.uni.nlp.utils.es;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.bonc.uni.nlp.config.SystemConfig;
import com.bonc.usdp.odk.common.collection.MapUtil;
import com.bonc.usdp.odk.common.string.StringUtil;
import com.bonc.usdp.odk.elasticsearch.esUtil.search.ISearchService;
import com.bonc.usdp.odk.elasticsearch.esUtil.search.impl.SearchServiceImpl;
import com.bonc.usdp.odk.logmanager.LogManager;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;

/**
 * ES连接管理类
 * 支持使用 odk-elasticsearch、sql4es 两种方式创建ES连接
 */
public class ESManager {

	private static ESManager instance = null;

	/**
	 * odk-elasticsearch 连接接口
	 */
	private ISearchService searchService;

	/**
	 * sql4es 数据源连接池
	 */
	private static Map<String, DruidDataSource> esDataSources;

	private ESManager() throws UnknownHostException {
		searchService = new SearchServiceImpl(SystemConfig.ELASTICSEARCH_IP,
				SystemConfig.ELASTICSEARCH_PORT, 
				SystemConfig.ELASTICSEARCH_NAME);
	}

	public static synchronized ESManager getInstance() throws UnknownHostException {
		if (instance == null) {
			instance = new ESManager();
			addShutDownHook();
		}
		return instance;
	}

	public ISearchService getSearchService() {
		return searchService;
	}

	public static SearchServiceImpl getNewSearchService() throws UnknownHostException {
		SearchServiceImpl searchService = null;
		searchService = new SearchServiceImpl(SystemConfig.ELASTICSEARCH_IP,
				SystemConfig.ELASTICSEARCH_PORT,
				SystemConfig.ELASTICSEARCH_NAME);
		return searchService;
	}

	private static String getEsIndexUrl(String index) {
		return"jdbc:sql4es://"
	 			+ SystemConfig.ELASTICSEARCH_IP + ":" 
				+ SystemConfig.ELASTICSEARCH_PORT + "/" 
	 			+ index + "?" + "cluster.name=" 
	 			+ SystemConfig.ELASTICSEARCH_NAME
	 			+ "&result.nested.lateral=false";
	}

	/**
	 * 创建指定索引的连接池
	 *
	 * @param index
	 * @return
	 */
	private static synchronized DruidDataSource getDataSource(String index) {
		if (StringUtil.isEmpty(index)) {
			return null;
		}
		if (esDataSources == null) {
			esDataSources = Maps.newHashMap();
		}

		DruidDataSource dataSource = esDataSources.get(index);
		if (dataSource == null) {
			try {
				ImmutableMap<String, String> conf = ImmutableMap.<String, String>builder()
						.put(DruidDataSourceFactory.PROP_DRIVERCLASSNAME, "com.bonc.usdp.sql4es.jdbc.ESDriver")
						.put(DruidDataSourceFactory.PROP_URL, getEsIndexUrl(index))
						.put(DruidDataSourceFactory.PROP_INITIALSIZE, "1")
						//.put(DruidDataSourceFactory.PROP_TESTWHILEIDLE, "true")
						//.put(DruidDataSourceFactory.PROP_VALIDATIONQUERY, "SELECT 'x'")
						.build();
				dataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(conf);
				esDataSources.put(index, dataSource);
			} catch (Exception e) {
				LogManager.Exception(e);
			}
		}
		return dataSource;
	}

	/**
	 * 关闭指定索引的连接并从连接池中删除
	 *
	 * @param index
	 */
	public static synchronized void closeDataSource(String index) {
		if (StringUtil.isEmpty(index) || esDataSources == null) {
			return;
		}
		DruidDataSource dataSource = esDataSources.get(index);
		if (dataSource != null) {
			dataSource.close();
			esDataSources.remove(index);
		}
	}

	private static void addShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				if (!MapUtil.isEmpty(esDataSources)) {
					esDataSources.forEach((index, ds) -> ds.close());
				}
			}
		});
	}

	/**
	 * 从池中获取指定索引的连接
	 *
	 * @param index
	 * @return
	 */
	public static Connection getConnection(String index) {
		LogManager.debug("url4es : " + getEsIndexUrl(index));
		if (StringUtil.isEmpty(index)) {
			return null;
		}
		Connection conn = null;
		DruidDataSource dataSource = getDataSource(index);
		try {
			conn = dataSource.getConnection();
        } catch (SQLException e) {
			LogManager.Exception(e);
		}
		return conn;
	}

	public static QueryRunner getQueryRunner(String index) {
		if (StringUtil.isEmpty(index)) {
			return null;
		}
		DruidDataSource dataSource = getDataSource(index);
		return new QueryRunner(dataSource);
	}

	/**
	 * 关闭索引连接
	 *
	 * @param conn
	 * @param statement
	 * @param resultSet
	 */
	public static void close(Connection conn, Statement statement, ResultSet resultSet) {
		try {
			if (conn != null)
				conn.close();
			if (statement != null)
				statement.close();
			if (resultSet != null)
				resultSet.close();
		} catch (SQLException e) {
			LogManager.Exception(e);
		}
	}

}
