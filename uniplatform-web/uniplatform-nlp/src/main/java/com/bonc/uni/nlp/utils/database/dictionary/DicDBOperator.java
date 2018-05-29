package com.bonc.uni.nlp.utils.database.dictionary;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.bonc.uni.nlp.utils.database.DBConnectionPool;
import com.bonc.uni.nlp.utils.database.DBUtil;
import com.bonc.uni.nlp.utils.redis.entity.RedisPublishDic;
import com.bonc.usdp.odk.logmanager.LogManager;
/**
 * @ClassName: DicDBOperator
 * @Package: com.bonc.uni.nlp.utils.database.dictionary
 * @Description: TODO
 * @author: Chris
 * @date: 2017年12月28日 下午9:14:04
 */
public class DicDBOperator {
	
	private final static String dicTable = "nlap_dictionary";
	private final static String dicTypeTable = "nlap_dic_type";
	private final static String dicSubTypeTable = "nlap_dic_sub_type";
	private final static String dicFunctionTable = "nlap_function";
	
	public static String getFunctionNameById(String functionId) {
		String functionName = null;
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DBConnectionPool.getInstance().getConnection();
			String sql = "select name from " + dicFunctionTable + " where id = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, functionId);
			rs = ps.executeQuery();
			while(rs.next()) {
				functionName = rs.getString(1);
				break;
			}
		} catch (SQLException e) {
			LogManager.Exception(e);
		} finally {
			DBUtil.close(conn, ps, rs);
		}
		
		return functionName;
	}
	
	private static String getTypeNameById(String typeId) {
		String typeName = null;
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DBConnectionPool.getInstance().getConnection();
			String sql = "select name from " + dicTypeTable + " where id = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, typeId);
			rs = ps.executeQuery();
			while(rs.next()) {
				typeName = rs.getString(1);
				break;
			}
		} catch (SQLException e) {
			LogManager.Exception(e);
		} finally {
			DBUtil.close(conn, ps, rs);
		}
		
		return typeName;
	}
	
	private static String getSubTypeNameById(String subTypeId) {
		String subTypeName = null;
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DBConnectionPool.getInstance().getConnection();
			String sql = "select name from " + dicSubTypeTable + " where id = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, subTypeId);
			rs = ps.executeQuery();
			while(rs.next()) {
				subTypeName = rs.getString(1);
				break;
			}
		} catch (SQLException e) {
			LogManager.Exception(e);
		} finally {
			DBUtil.close(conn, ps, rs);
		}
		
		return subTypeName;
	}
	
	public static RedisPublishDic getDicInfoById(String dicId) {
		
		LogManager.debug("Query dic info, dic id : " + dicId);
		
		RedisPublishDic dic = null;
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DBConnectionPool.getInstance().getConnection();
			String sql = "select `name`, dic_type_id, dic_sub_type_id, format from "
					+ dicTable + " where id = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, dicId);
			rs = ps.executeQuery();
			while(rs.next()) {
				dic = new RedisPublishDic();
				dic.setDicId(dicId);
				
				String dicName = rs.getString(1);
				dic.setDicName(dicName);
				
				String dicTypeId = rs.getString(2);
				dic.setDicType(getTypeNameById(dicTypeId));
				
				String dicSubTypeId = rs.getString(3);
				dic.setDicSubType(getSubTypeNameById(dicSubTypeId));
				
				String format = rs.getString(4);
				dic.setDicFormat(format);
				
				break;
			}
		} catch (SQLException e) {
			LogManager.Exception(e);
		} finally {
			DBUtil.close(conn, ps, rs);
		}
		
		LogManager.debug("Queried dic info : " + dic);
		
		return dic;
	}
	

}
