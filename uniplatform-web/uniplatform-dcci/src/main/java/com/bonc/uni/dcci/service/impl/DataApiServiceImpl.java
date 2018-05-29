package com.bonc.uni.dcci.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.bonc.uni.common.jpa.Specification.SpecificationUtil;
import com.bonc.uni.dcci.dao.ApiDetailRepository;
import com.bonc.uni.dcci.dao.ApiRequestExampleRepository;
import com.bonc.uni.dcci.dao.ApiRequestParamRepository;
import com.bonc.uni.dcci.dao.ApiReturnExampleRepository;
import com.bonc.uni.dcci.dao.ApiReturnParamRepository;
import com.bonc.uni.dcci.dao.ApiCodeRepository;
import com.bonc.uni.dcci.dao.DataApiRepository;
import com.bonc.uni.dcci.entity.ApiDetail;
import com.bonc.uni.dcci.entity.ApiRequestExample;
import com.bonc.uni.dcci.entity.ApiRequestParam;
import com.bonc.uni.dcci.entity.ApiReturnExample;
import com.bonc.uni.dcci.entity.ApiReturnParam;
import com.bonc.uni.dcci.entity.ApiCode;
import com.bonc.uni.dcci.entity.DataApi;
import com.bonc.uni.dcci.service.DataApiService;
import com.bonc.uni.dcci.util.APIType;

@Service("dataApiService")
public class DataApiServiceImpl implements DataApiService {

	@Autowired
	DataApiRepository dataApiRepository;
	
	@Autowired
	ApiDetailRepository apiDetailRepository;
	
	@Autowired
	ApiCodeRepository apiCodeRepository;
	
	@Autowired
	ApiRequestParamRepository apiRequestParamRepository;
	
	@Autowired
	ApiReturnParamRepository apiReturnParamRepository;
	
	@Autowired
	ApiRequestExampleRepository apiRequestExampleRepository;
	
	@Autowired
	ApiReturnExampleRepository apiReturnExampleRepository;

	@Override
	public long countDataApi () {
		return dataApiRepository.count();
	}
	
	@Override
	public DataApi save(DataApi dataApi) {
		return dataApiRepository.save(dataApi);
	}
	
	@Override
	public DataApi getById(Integer id) {
		return dataApiRepository.findOne(id);
	}
	
	@Override
	public boolean delById(Integer id) {
		try {
			dataApiRepository.delete(id);
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@Override
	public DataApi upd(DataApi dataApi) {
		DataApi upd = getById(dataApi.getId());
		if(null !=upd) {
			upd.setCount(dataApi.getCount());
			upd.setName(dataApi.getName());
			upd.setPrice(dataApi.getPrice());
			upd.setSpecifications(dataApi.getSpecifications());
			upd.setStatus(dataApi.getStatus());
			upd.setType(dataApi.getType());
			upd.setUnit(dataApi.getUnit());
			if(StringUtils.isNotBlank(dataApi.getImageName())) {
				upd.setImageName(dataApi.getImageName());
			}
			if(StringUtils.isNotBlank(dataApi.getImagePath())) {
				upd.setImagePath(dataApi.getImagePath());
			}
			return dataApiRepository.save(upd);
		}
		return null;
	}
	
	@Override
	public Page<DataApi> pageList(String name,APIType type,APIType.Status status,int pageNum, int pageSize){
		Specification<DataApi> spec = SpecificationUtil.<DataApi>and().
				ep(type!=null, "type", type)
				.ep(status!=null, "status", status)
				.like(StringUtils.isNotBlank(name),"name", "%"+name+"%").build();
		
		return dataApiRepository.findAll(spec, new PageRequest(pageNum - 1, pageSize));
	}
	
	@Override
	public ApiDetail save(ApiDetail apiDetail) {
		return apiDetailRepository.save(apiDetail);
	}
	
	@Override
	public ApiDetail getByApiDetailId(Integer id) {
		return apiDetailRepository.findOne(id);
	}
	
	@Override
	public boolean delByApiDetailId(Integer id) {
		try {
			apiDetailRepository.delete(id);
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@Override
	public int delDetailByApiId(int apiId) {
		return apiDetailRepository.delete(apiId);
	}
	
	@Override
	public ApiDetail updApiDetail(ApiDetail apiDetail) {
		ApiDetail upd = getByApiDetailId(apiDetail.getId());
		if(null !=upd) {
			upd.setName(apiDetail.getName());
			upd.setEmail(apiDetail.getEmail());
			upd.setUrl(apiDetail.getUrl());
			upd.setUser(apiDetail.getUser());
			upd.setMethod(apiDetail.getMethod());
			upd.setPhone(apiDetail.getPhone());
			upd.setSample(apiDetail.getSample());
			return apiDetailRepository.save(upd);
		}
		return null;
	}
	
	@Override
	public Page<ApiDetail> pageApiDetailList(int apiId,String name,String url,APIType.Method method,int pageNum, int pageSize){
		Specification<ApiDetail> spec = SpecificationUtil.<ApiDetail>and()
				.ep("apiId", apiId)
				.ep(method!=null, "method", method)
				.like(StringUtils.isNotBlank(url),"url", "%"+url+"%")
				.like(StringUtils.isNotBlank(name),"name", "%"+name+"%").build();
		
		return apiDetailRepository.findAll(spec, new PageRequest(pageNum - 1, pageSize));
	}
	
	@Override
	public List<ApiDetail> listApiDetailList(int apiId){
		Specification<ApiDetail> spec = SpecificationUtil.<ApiDetail>and()
				.ep("apiId", apiId).build();
		return apiDetailRepository.findAll(spec);
	}
	
	
	/**--------------apiCodeRepository--------------*/
	
	@Override
	public ApiCode saveCode(ApiCode ApiCode) {
		return apiCodeRepository.save(ApiCode);
	}
	
	@Override
	public boolean delCodeById(Integer id) {
		try {
			apiCodeRepository.delete(id);
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@Override
	public ApiCode getCodeById(Integer id) {
		return apiCodeRepository.findOne(id);
	}
	
	@Override
	public ApiCode getCodeByCode(int code) {
		Specification<ApiCode> spec = SpecificationUtil.<ApiCode>and().ep("code", code).build();
		return apiCodeRepository.findOne(spec);
	}
	
	@Override
	public ApiCode updCode(ApiCode ApiCode) {
		ApiCode upd = getCodeById(ApiCode.getId());
		if(null != upd) {
			upd.setCode(ApiCode.getCode());
			upd.setExplain(ApiCode.getExplain());
			return apiCodeRepository.save(upd);
		}
		return null;
	}
	
	@Override
	public Page<ApiCode> pageCodeList(int code,int pageNum, int pageSize){
		Specification<ApiCode> spec = SpecificationUtil.<ApiCode>and().ep(code!=0, "code", code).build();
		return apiCodeRepository.findAll(spec, new PageRequest(pageNum - 1, pageSize));
	}
	
	@Override
	public List<ApiCode> listCodeList(){
		return (List<ApiCode>) apiCodeRepository.findAll();
	}
	
	
	/**--------------ApiRequestParamRepository-------------------------------*/
	
	@Override
	public ApiRequestParam saveRequestParam(ApiRequestParam apiRequestParam) {
		return apiRequestParamRepository.save(apiRequestParam);
	}
	
	@Override
	public boolean delRequestParamById(Integer id) {
		try {
			apiRequestParamRepository.delete(id);
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@Override
	public int delRequestParamByDetailId(int detailId) {
		return apiRequestParamRepository.delete(detailId);
	}
	
	@Override
	public ApiRequestParam getRequestParamById(Integer id) {
		return apiRequestParamRepository.findOne(id);
	}
	
	@Override
	public ApiRequestParam getRequestParam(int detailId,String name) {
		Specification<ApiRequestParam> spec = SpecificationUtil.<ApiRequestParam>and().ep("detailId", detailId).ep("name", name).build();
		return apiRequestParamRepository.findOne(spec);
	}
	
	@Override
	public ApiRequestParam updRequestParam(ApiRequestParam apiRequestParam) {
		ApiRequestParam upd = getRequestParamById(apiRequestParam.getId());
		if(null != upd) {
			upd.setName(apiRequestParam.getName());
			upd.setType(apiRequestParam.getType());
			upd.setRequire(apiRequestParam.getRequire());
			upd.setExample(apiRequestParam.getExample());
			upd.setDescription(apiRequestParam.getDescription());
			return apiRequestParamRepository.save(upd);
		}
		return null;
	}
	
	@Override
	public Page<ApiRequestParam> pageRequestParamList(int detailId,int pageNum, int pageSize){
		Specification<ApiRequestParam> spec = SpecificationUtil.<ApiRequestParam>and().ep("detailId", detailId).build();
		return apiRequestParamRepository.findAll(spec,new PageRequest(pageNum - 1, pageSize));
	}
	
	@Override
	public List<ApiRequestParam> listRequestParamList(int detailId){
		Specification<ApiRequestParam> spec = SpecificationUtil.<ApiRequestParam>and().ep("detailId", detailId).build();
		return apiRequestParamRepository.findAll(spec);
	}
	
	/**-------------apiReturnParamRepository-----------------------------------*/
	
	@Override
	public ApiReturnParam saveReturnParam(ApiReturnParam apiReturnParam) {
		return apiReturnParamRepository.save(apiReturnParam);
	}
	
	@Override
	public boolean delReturnParamById(Integer id) {
		try {
			apiReturnParamRepository.delete(id);
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@Override
	public int delReturnParamByDetailId(int detailId) {
		return apiReturnParamRepository.delete(detailId);
	}
	
	@Override
	public ApiReturnParam getReturnParamById(Integer id) {
		return apiReturnParamRepository.findOne(id);
	}
	
	@Override
	public ApiReturnParam getReturnParam(int detailId,String name) {
		Specification<ApiReturnParam> spec = SpecificationUtil.<ApiReturnParam>and().ep("detailId", detailId).ep("name", name).build();
		return apiReturnParamRepository.findOne(spec);
	}
	
	@Override
	public ApiReturnParam updReturnParam(ApiReturnParam apiReturnParam) {
		ApiReturnParam upd = getReturnParamById(apiReturnParam.getId());
		if(null != upd) {
			upd.setName(apiReturnParam.getName());
			upd.setType(apiReturnParam.getType());
			upd.setExample(apiReturnParam.getExample());
			upd.setDescription(apiReturnParam.getDescription());
			return apiReturnParamRepository.save(upd);
		}
		return null;
	}
	
	@Override
	public Page<ApiReturnParam> pageReturnParamList(int detailId,int pageNum, int pageSize){
		Specification<ApiReturnParam> spec = SpecificationUtil.<ApiReturnParam>and().ep("detailId", detailId).build();
		return apiReturnParamRepository.findAll(spec,new PageRequest(pageNum - 1, pageSize));
	}
	
	@Override
	public List<ApiReturnParam> listReturnParamList(int detailId){
		Specification<ApiReturnParam> spec = SpecificationUtil.<ApiReturnParam>and().ep("detailId", detailId).build();
		return apiReturnParamRepository.findAll(spec);
	}
	
	/**--------------apiRequestExampleRepository---------------------------*/
	
	
	@Override
	public ApiRequestExample saveRequestExample(ApiRequestExample apiRequestExample) {
		return apiRequestExampleRepository.save(apiRequestExample);
	}
	
	@Override
	public boolean delRequestExampleById(Integer id) {
		try {
			apiRequestExampleRepository.delete(id);
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@Override
	public int delRequestExampleByDetailId(int detailId) {
		return apiRequestExampleRepository.delete(detailId);
	}
	
	@Override
	public ApiRequestExample getRequestExampleById(Integer id) {
		return apiRequestExampleRepository.findOne(id);
	}
	
	@Override
	public ApiRequestExample updRequestExample(ApiRequestExample apiRequestExample) {
		ApiRequestExample upd = getRequestExampleById(apiRequestExample.getId());
		if(null != upd) {
			upd.setCode(apiRequestExample.getCode());
			upd.setLanguage(apiRequestExample.getLanguage());
			return apiRequestExampleRepository.save(upd);
		}
		return null;
	}
	
	@Override
	public ApiRequestExample addAndUpdRequestExample(ApiRequestExample apiRequestExample) {
		if(apiRequestExample.getId() == 0) {
			if(apiRequestExample.getDetailId() != 0) {
				ApiRequestExample add = getRequestExample(apiRequestExample.getDetailId(),apiRequestExample.getLanguage());
				if(null != add) {
					apiRequestExample.setId(add.getId());
					return updRequestExample(apiRequestExample);
				}
			}
			return apiRequestExampleRepository.save(apiRequestExample);
		}else {
			return updRequestExample(apiRequestExample);
		}
	}
	
	@Override
	public ApiRequestExample getRequestExample(int detailId,APIType.Language language) {
		Specification<ApiRequestExample> spec = SpecificationUtil.<ApiRequestExample>and().ep("language", language).ep(detailId!=0, "detailId", detailId).build();
		return apiRequestExampleRepository.findOne(spec);
	}
	
	@Override
	public Page<ApiRequestExample> pageRequestExampleList(int detailId,int pageNum, int pageSize){
		Specification<ApiRequestExample> spec = SpecificationUtil.<ApiRequestExample>and().ep("detailId", detailId).build();
		return apiRequestExampleRepository.findAll(spec,new PageRequest(pageNum - 1, pageSize));
	}
	
	
	/**---------------apiReturnExampleRepository----------------------------------*/
	
	@Override
	public ApiReturnExample saveReturnExample(ApiReturnExample apiReturnExample) {
		return apiReturnExampleRepository.save(apiReturnExample);
	}
	
	@Override
	public boolean delReturnExampleById(Integer id) {
		try {
			apiReturnExampleRepository.delete(id);
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@Override
	public int delReturnExampleByDetailId(int detailId) {
		return apiReturnExampleRepository.delete(detailId);
	}
	
	@Override
	public ApiReturnExample getReturnExampleById(Integer id) {
		return apiReturnExampleRepository.findOne(id);
	}
	
	@Override
	public ApiReturnExample getReturnExample(int detailId) {
		Specification<ApiReturnExample> spec = SpecificationUtil.<ApiReturnExample>and().ep("detailId", detailId).build();
		return apiReturnExampleRepository.findOne(spec);
	}
	
	@Override
	public ApiReturnExample updReturnExample(ApiReturnExample apiReturnExample) {
		ApiReturnExample upd = getReturnExampleById(apiReturnExample.getId());
		if(null != upd) {
			upd.setExample(apiReturnExample.getExample());
			return apiReturnExampleRepository.save(upd);
		}
		return null;
	}
	
	@Override
	public ApiReturnExample addAndUpdReturnExample(ApiReturnExample apiReturnExample) {
		if(apiReturnExample.getId() == 0) {
			if(apiReturnExample.getDetailId() != 0) {
				ApiReturnExample apiReturnExamples = getReturnExample(apiReturnExample.getDetailId());
				if(apiReturnExamples != null) {
					apiReturnExample.setId(apiReturnExamples.getId());
					return updReturnExample(apiReturnExample);
				}
			}
			return apiReturnExampleRepository.save(apiReturnExample);
		}else {
			return updReturnExample(apiReturnExample);
		}
	}
}
