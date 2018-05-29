package com.bonc.uni.dcci.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.bonc.uni.dcci.entity.ApiDetail;
import com.bonc.uni.dcci.entity.ApiRequestExample;
import com.bonc.uni.dcci.entity.ApiRequestParam;
import com.bonc.uni.dcci.entity.ApiReturnExample;
import com.bonc.uni.dcci.entity.ApiReturnParam;
import com.bonc.uni.dcci.entity.ApiCode;
import com.bonc.uni.dcci.entity.DataApi;
import com.bonc.uni.dcci.util.APIType;

/**
 * 商城  数据api服务
 * @author futao
 * 2017年9月22日
 */
public interface DataApiService {

	/**
	 * 统计
	 * @return
	 */
	public long countDataApi ();

	/**
	 * 保存
	 * @param dataApi
	 * @return
	 */
	DataApi save(DataApi dataApi);
	
	/**
	 * id获取对象
	 * @param id
	 * @return
	 */
	DataApi getById(Integer id);
	
	/**id 删除
	 * @param id
	 * @return
	 */
	boolean delById(Integer id);
	
	/**修改
	 * @param dataApi
	 * @return
	 */
	DataApi upd(DataApi dataApi);
	
	/**
	 * 列表翻页
	 * @param name
	 * @param type
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	Page<DataApi> pageList(String name,APIType type,APIType.Status status,int pageNum, int pageSize);
	
	/**保存
	 * @param apiDetail
	 * @return
	 */
	ApiDetail save(ApiDetail apiDetail);
	
	/**获取对象
	 * @param id
	 * @return
	 */
	ApiDetail getByApiDetailId(Integer id);
	
	/**删除
	 * @param id
	 * @return
	 */
	boolean delByApiDetailId(Integer id);
	
	/**
	 * 删除
	 * @param apiId
	 * @return
	 */
	int delDetailByApiId(int apiId);
	
	/**修改
	 * @param apiDetail
	 * @return
	 */
	ApiDetail updApiDetail(ApiDetail apiDetail);
	
	/**列表
	 * @param name
	 * @param url
	 * @param method
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	Page<ApiDetail> pageApiDetailList(int apiId,String name,String url,APIType.Method method,int pageNum, int pageSize);
	
	
	List<ApiDetail> listApiDetailList(int apiId);
	
	/**
	 * 保存
	 * @param ApiCode
	 * @return
	 */
	ApiCode saveCode(ApiCode ApiCode);
	
	/**删除
	 * @param id
	 * @return
	 */
	boolean delCodeById(Integer id);
	
	/**获取对象
	 * @param id
	 * @return
	 */
	ApiCode getCodeById(Integer id);
	
	/**判断重复
	 * @param code
	 * @return
	 */
	ApiCode getCodeByCode(int code);
	
	/**修改
	 * @param ApiCode
	 * @return
	 */
	ApiCode updCode(ApiCode ApiCode);
	
	/**翻页列表  如果code==0  查询所有
	 * @param code
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	Page<ApiCode> pageCodeList(int code,int pageNum, int pageSize);
	
	/**
	 * 集合
	 * @return
	 */
	List<ApiCode> listCodeList();
	/**保存
	 * @param apiRequestParam
	 * @return
	 */
	ApiRequestParam saveRequestParam(ApiRequestParam apiRequestParam) ;
	
	/**删除
	 * @param id
	 * @return
	 */
	boolean delRequestParamById(Integer id);
	
	/**
	 * 批量删除
	 * @param apiId
	 * @return
	 */
	int delRequestParamByDetailId(int detailId);
	
	/**获取对象
	 * @param id
	 * @return
	 */
	ApiRequestParam getRequestParamById(Integer id);
	
	/**获取对象  判断重复
	 * @param detailId
	 * @param name
	 * @return
	 */
	ApiRequestParam getRequestParam(int detailId,String name);
	
	/**
	 * 修改
	 * @param apiRequestParam
	 * @return
	 */
	ApiRequestParam updRequestParam(ApiRequestParam apiRequestParam);
	
	/**翻页
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	Page<ApiRequestParam> pageRequestParamList(int detailId,int pageNum, int pageSize);
	
	/**列出所有
	 * @param detailId
	 * @return
	 */
	List<ApiRequestParam> listRequestParamList(int detailId);
	
	/**保存
	 * @param apiReturnParam
	 * @return
	 */
	ApiReturnParam saveReturnParam(ApiReturnParam apiReturnParam);
	
	/**删除
	 * @param id
	 * @return
	 */
	boolean delReturnParamById(Integer id);
	
	/**批量删除
	 * @param apiId
	 * @return
	 */
	int delReturnParamByDetailId(int detailId);
	
	/**获取对象
	 * @param id
	 * @return
	 */
	ApiReturnParam getReturnParamById(Integer id);
	
	/**获取对象  判断重复
	 * @param detailId
	 * @param name
	 * @return
	 */
	ApiReturnParam getReturnParam(int detailId,String name);
	
	/**修改
	 * @param apiReturnParam
	 * @return
	 */
	ApiReturnParam updReturnParam(ApiReturnParam apiReturnParam);
	
	/**列表
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	Page<ApiReturnParam> pageReturnParamList(int detailId,int pageNum, int pageSize);
	
	/**列出所有
	 * @param detailId
	 * @return
	 */
	List<ApiReturnParam> listReturnParamList(int detailId);
	
	
	/**
	 * @param apiRequestExample
	 * @return
	 */
	ApiRequestExample saveRequestExample(ApiRequestExample apiRequestExample);
	
	/**删除
	 * @param id
	 * @return
	 */
	boolean delRequestExampleById(Integer id);
	
	/**批量删除
	 * @param apiId
	 * @return
	 */
	int delRequestExampleByDetailId(int detailId);
	
	/**获取对象
	 * @param id
	 * @return
	 */
	ApiRequestExample getRequestExampleById(Integer id);
	
	/**修改
	 * @param apiRequestExample
	 * @return
	 */
	ApiRequestExample updRequestExample(ApiRequestExample apiRequestExample);
	
	
	/**添加或修改
	 * @param apiRequestExample
	 * @return
	 */
	ApiRequestExample addAndUpdRequestExample(ApiRequestExample apiRequestExample);
	
	/**
	 * @param detailId
	 * @param language
	 * @return
	 */
	ApiRequestExample getRequestExample(int detailId,APIType.Language language);
	
	/**、
	 * 翻页
	 * @param apiId
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	Page<ApiRequestExample> pageRequestExampleList(int detailId,int pageNum, int pageSize);
	
	
	/**保存
	 * @param apiReturnExample
	 * @return
	 */
	ApiReturnExample saveReturnExample(ApiReturnExample apiReturnExample);
	
	/**删除
	 * @param id
	 * @return
	 */
	boolean delReturnExampleById(Integer id);
	
	/**
	 * 根据detailid  删除
	 * @param detailId
	 * @return
	 */
	int delReturnExampleByDetailId(int detailId);
	
	/**获取对象
	 * @param id
	 * @return
	 */
	ApiReturnExample getReturnExampleById(Integer id);
	
	/**通过detailid获取对象
	 * @param detailId
	 * @return
	 */
	ApiReturnExample getReturnExample(int detailId);
	
	/**修改
	 * @param apiReturnExample
	 * @return
	 */
	ApiReturnExample updReturnExample(ApiReturnExample apiReturnExample);
	
	/**返回示例的添加和删除
	 * @param apiReturnExample
	 * @return
	 */
	ApiReturnExample addAndUpdReturnExample(ApiReturnExample apiReturnExample);
}
