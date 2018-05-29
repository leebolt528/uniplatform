package com.bonc.uni.dcci.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bonc.uni.common.config.UploadConfiguration;
import com.bonc.uni.common.util.MD5Util;
import com.bonc.uni.common.util.ResultUtil;
import com.bonc.uni.dcci.entity.ApiCode;
import com.bonc.uni.dcci.entity.ApiDetail;
import com.bonc.uni.dcci.entity.ApiRequestExample;
import com.bonc.uni.dcci.entity.ApiRequestParam;
import com.bonc.uni.dcci.entity.ApiReturnExample;
import com.bonc.uni.dcci.entity.ApiReturnParam;
import com.bonc.uni.dcci.entity.DataApi;
import com.bonc.uni.dcci.service.DataApiService;
import com.bonc.uni.dcci.util.APIType;
import com.bonc.uni.dcci.util.EnumUtil;

import net.sf.json.JSONObject;

/**
 * 商城api控制器
 * 
 * @author futao 2017年9月22日
 */
@CrossOrigin
@RestController
@RequestMapping("/dcci/api")
public class DataApiController {

	@Autowired
	DataApiService dataApiService;

	public static String ALL = "ALL";

	private final ResourceLoader resourceLoader;

	private String imagepath;

	@Autowired
	public DataApiController(ResourceLoader resourceLoader, UploadConfiguration uploadConfiguration) {
		this.resourceLoader = resourceLoader;
		this.imagepath = uploadConfiguration.getDcci() + "/image/";
	}

	/**
	 * 获取枚举值
	 * 
	 * @return
	 */
	@RequestMapping("/list/enum")
	public String listEnum() {
		JSONObject json = new JSONObject();
		json.put(APIType.class.getSimpleName().toLowerCase(), EnumUtil.apiType);
		json.put(APIType.Status.class.getSimpleName().toLowerCase(), EnumUtil.apiType_Status);
		json.put(APIType.Unit.class.getSimpleName().toLowerCase(), EnumUtil.apiType_Unit);
		return ResultUtil.success("请求成功", json);
	}

	/**
	 * 列表
	 * 
	 * @param name
	 * @param typestr
	 * @param statustr
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@RequestMapping("/list")
	public String list(@RequestParam(value = "name", required = false, defaultValue = "") String name,
			@RequestParam(value = "type", required = false, defaultValue = "ALL") String typestr,
			@RequestParam(value = "status", required = false, defaultValue = "ALL") String statustr,
			@RequestParam(value = "number", required = false, defaultValue = "1") int pageNum,
			@RequestParam(value = "size", required = false, defaultValue = "10") int pageSize) {
		APIType type = null;
		APIType.Status status = null;
		if (!typestr.equalsIgnoreCase(ALL)) {
			type = APIType.valueOf(typestr);
		}
		if (!statustr.equalsIgnoreCase(ALL)) {
			status = APIType.Status.valueOf(statustr);
		}

		Page<DataApi> pages = dataApiService.pageList(name, type, status, pageNum, pageSize);
		return ResultUtil.success("请求成功", pages);
	}

	/**
	 * 添加
	 * 
	 * @param dataApi
	 * @return
	 */
	@Transactional
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(@ModelAttribute("dataApi") DataApi dataApi, @RequestParam("image") MultipartFile imagefile) {
		try {
			String fileName = imagefile.getOriginalFilename();
			String suffixName = fileName.substring(fileName.lastIndexOf("."));
			String imageName = fileName = MD5Util.getMd5ByFile16(imagefile.getInputStream()) + suffixName;
			String path = imagepath + imageName;
			File filePath = new File(path);
			if (!filePath.getParentFile().exists()) {
				filePath.getParentFile().mkdir();
			}
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(filePath));
			out.write(imagefile.getBytes());
			out.flush();
			out.close();

			dataApi.setImageName(imageName);
			dataApi.setImagePath(path);
			dataApi.setStatus(APIType.Status.OPEN);
			DataApi save = dataApiService.save(dataApi);
			if (null != save) {
				return ResultUtil.success("添加成功", save);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultUtil.error("添加失败", e.getMessage());
		}
		return ResultUtil.addError();
	}

	@RequestMapping(value = "/image/{filename:.+}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getFile(@PathVariable String filename) {
		try {
			return ResponseEntity.ok(resourceLoader.getResource("file:" + Paths.get(imagepath, filename).toString()));
		} catch (Exception e) {
			return ResponseEntity.notFound().build();
		}
	}

	/**
	 * 修改
	 * 
	 * @param dataApi
	 * @return
	 */
	@Transactional
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(@ModelAttribute("dataApi") DataApi dataApi,
			@RequestParam(value = "image", required = false) MultipartFile imagefile) {
		try {
			if (null != imagefile) {
				/*
				 * DataApi getDataApi = dataApiService.getById(dataApi.getId()); File delFile =
				 * new File(imagepath + getDataApi.getImageName()); if(delFile.exists()) {
				 * delFile.delete(); }
				 */
				String fileName = imagefile.getOriginalFilename();
				String suffixName = fileName.substring(fileName.lastIndexOf("."));
				String imageName = fileName = MD5Util.getMd5ByFile16(imagefile.getInputStream()) + suffixName;
				String path = imagepath + imageName;
				File filePath = new File(path);
				if (!filePath.getParentFile().exists()) {
					filePath.getParentFile().mkdir();
				}
				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(filePath));
				out.write(imagefile.getBytes());
				out.flush();
				out.close();

				dataApi.setImageName(imageName);
				dataApi.setImagePath(path);

			}
			DataApi upd = dataApiService.upd(dataApi);
			if (null != upd) {
				return ResultUtil.success("修改成功", upd);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return ResultUtil.error("修改失败", e.getMessage());
		}
		return ResultUtil.updError();
	}

	/**
	 * 删除
	 * 
	 * @param id
	 * @return
	 */
	@Transactional
	@RequestMapping(value = "/delete/{id}")
	public String delete(@PathVariable int id) {
		try {
			List<ApiDetail> details = dataApiService.listApiDetailList(id);
			Iterator<ApiDetail> its = details.iterator();
			while (its.hasNext()) {
				ApiDetail detail = its.next();
				int detailId = detail.getId();
				dataApiService.delRequestExampleByDetailId(detailId);
				dataApiService.delRequestParamByDetailId(detailId);
				dataApiService.delReturnParamByDetailId(detailId);
				dataApiService.delReturnExampleByDetailId(detailId);
			}
			dataApiService.delDetailByApiId(id);
			// DataApi getDataApi = dataApiService.getById(id);
			boolean del = dataApiService.delById(id);
			/*
			 * File delFile = new File(imagepath + getDataApi.getImageName());
			 * if(delFile.exists()) { delFile.delete(); }
			 */
			if (del) {
				return ResultUtil.delSuccess();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultUtil.error("删除失败", e.getMessage());
		}

		return ResultUtil.delError();
	}

	/**
	 * 获取对象
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/get/{id}")
	public String get(@PathVariable int id) {
		DataApi dataApi = dataApiService.getById(id);
		if (null != dataApi) {
			return ResultUtil.success("获取成功", dataApi);
		}
		return ResultUtil.getError();
	}

	/**
	 * 关闭
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/status/{id}")
	public String status(@PathVariable int id) {
		DataApi dataApi = dataApiService.getById(id);
		if (null != dataApi) {
			if (dataApi.getStatus() == APIType.Status.CLOSE) {
				dataApi.setStatus(APIType.Status.OPEN);
			} else {
				dataApi.setStatus(APIType.Status.CLOSE);
			}

			DataApi upd = dataApiService.upd(dataApi);
			if (null != upd) {
				return ResultUtil.success("修改状态成功", upd);
			}
		}
		return ResultUtil.error("修改状态失败", null);
	}

	@RequestMapping("/detail/list")
	public String listApiDetail(@RequestParam(value = "apiId", required = true) int apiId,
			@RequestParam(value = "name", required = false, defaultValue = "") String name,
			@RequestParam(value = "method", required = false, defaultValue = "ALL") String methodstr,
			@RequestParam(value = "url", required = false, defaultValue = "") String url,
			@RequestParam(value = "number", required = false, defaultValue = "1") int pageNum,
			@RequestParam(value = "size", required = false, defaultValue = "10") int pageSize) {
		APIType.Method method = null;
		if (!methodstr.equalsIgnoreCase(ALL)) {
			method = APIType.Method.valueOf(methodstr);
		}

		Page<ApiDetail> pages = dataApiService.pageApiDetailList(apiId, name, url, method, pageNum, pageSize);
		return ResultUtil.success("请求成功", pages);
	}

	@RequestMapping("/detail/list/all")
	public String listApiDetail(@RequestParam(value = "apiId", required = true) int apiId) {
		List<ApiDetail> lists = dataApiService.listApiDetailList(apiId);
		return ResultUtil.success("请求成功", lists);
	}

	@RequestMapping(value = "/detail/save", method = RequestMethod.POST)
	public String saveApiDetail(@ModelAttribute("apiDetail") ApiDetail apiDetail) {
		ApiDetail save = dataApiService.save(apiDetail);
		if (null != save) {
			return ResultUtil.success("添加成功", save);
		}
		return ResultUtil.addError();
	}

	@RequestMapping(value = "/detail/update", method = RequestMethod.POST)
	public String updateApiDetail(@ModelAttribute("apiDetail") ApiDetail apiDetail) {
		ApiDetail upd = dataApiService.updApiDetail(apiDetail);
		if (null != upd) {
			return ResultUtil.success("修改成功", upd);
		}
		return ResultUtil.updError();
	}

	/**
	 * 删除
	 * 
	 * @param id
	 * @return
	 */
	@Transactional
	@RequestMapping(value = "/detail/delete/{id}")
	public String deleteApiDetail(@PathVariable int id) {
		ApiDetail detail = dataApiService.getByApiDetailId(id);
		int detailId = detail.getId();
		dataApiService.delRequestExampleByDetailId(detailId);
		dataApiService.delRequestParamByDetailId(detailId);
		dataApiService.delReturnParamByDetailId(detailId);
		dataApiService.delReturnExampleByDetailId(detailId);
		boolean del = dataApiService.delByApiDetailId(id);
		if (del) {
			return ResultUtil.delSuccess();
		}
		return ResultUtil.delError();
	}

	/**
	 * 获取对象
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/detail/get/{id}")
	public String getApiDetail(@PathVariable int id) {
		ApiDetail apiDetail = dataApiService.getByApiDetailId(id);
		if (null != apiDetail) {
			return ResultUtil.success("获取成功", apiDetail);
		}
		return ResultUtil.getError();
	}

	/** --------------apiCodeRepository-------------- */

	/**
	 * 状态码
	 * 
	 * @param code
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@RequestMapping("/code/list")
	public String listCode(@RequestParam(value = "code", required = false, defaultValue = "0") int code,
			@RequestParam(value = "number", required = false, defaultValue = "1") int pageNum,
			@RequestParam(value = "size", required = false, defaultValue = "10") int pageSize) {
		Page<ApiCode> pages = dataApiService.pageCodeList(code, pageNum, pageSize);
		return ResultUtil.success("请求成功", pages);
	}
	
	@RequestMapping("/code/list/all")
	public String listCode() {
		List<ApiCode> lists = dataApiService.listCodeList();
		return ResultUtil.success("请求成功", lists);
	}

	@RequestMapping(value = "/code/get/{id}")
	public String getCode(@PathVariable int id) {
		ApiCode apiCode = dataApiService.getCodeById(id);
		if (null != apiCode) {
			return ResultUtil.success("获取成功", apiCode);
		}
		return ResultUtil.getError();
	}

	/**
	 * 删除
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/code/delete/{id}")
	public String deleteCode(@PathVariable int id) {
		boolean del = dataApiService.delCodeById(id);
		if (del) {
			return ResultUtil.delSuccess();
		}
		return ResultUtil.delError();
	}

	/**
	 * 保存
	 * 
	 * @param apiCode
	 * @return
	 */
	@RequestMapping(value = "/code/save", method = RequestMethod.POST)
	public String saveCode(@ModelAttribute("apiCode") ApiCode apiCode) {
		ApiCode exist = dataApiService.getCodeByCode(apiCode.getCode());
		if (null != exist) {
			return ResultUtil.error("添加失败,状态码不能重复", null);
		}
		ApiCode save = dataApiService.saveCode(apiCode);
		if (null != save) {
			return ResultUtil.success("添加成功", save);
		}
		return ResultUtil.addError();
	}

	/**
	 * 修改
	 * 
	 * @param apiCode
	 * @return
	 */
	@RequestMapping(value = "/code/update", method = RequestMethod.POST)
	public String updateCode(@ModelAttribute("apiCode") ApiCode apiCode) {
		ApiCode exist = dataApiService.getCodeByCode(apiCode.getCode());
		if (null != exist) {
			return ResultUtil.error("修改失败,状态码不能重复", null);
		}
		ApiCode upd = dataApiService.updCode(apiCode);
		if (null != upd) {
			return ResultUtil.success("修改成功", upd);
		}
		return ResultUtil.updError();
	}

	/** --------------ApiRequestParamRepository------------------------------- */

	/**
	 * 翻页
	 * 
	 * @param apiId
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@RequestMapping("/request/param/list")
	public String listRequestParam(@RequestParam(value = "detailId", required = true) int detailId,
			@RequestParam(value = "number", required = false, defaultValue = "1") int pageNum,
			@RequestParam(value = "size", required = false, defaultValue = "10") int pageSize) {
		Page<ApiRequestParam> pages = dataApiService.pageRequestParamList(detailId, pageNum, pageSize);
		return ResultUtil.success("请求成功", pages);
	}

	/**
	 * 获取对象
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/request/param/get/{id}")
	public String getRequestParam(@PathVariable int id) {
		ApiRequestParam apiRequestParam = dataApiService.getRequestParamById(id);
		if (null != apiRequestParam) {
			return ResultUtil.success("获取成功", apiRequestParam);
		}
		return ResultUtil.getError();
	}

	/**
	 * 删除
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/request/param/delete/{id}")
	public String deleteRequestParam(@PathVariable int id) {
		boolean del = dataApiService.delRequestParamById(id);
		if (del) {
			return ResultUtil.delSuccess();
		}
		return ResultUtil.delError();
	}

	/**
	 * 保存
	 * 
	 * @param apiRequestParam
	 * @return
	 */
	@RequestMapping(value = "/request/param/save", method = RequestMethod.POST)
	public String saveRequestParam(@ModelAttribute("apiRequestParam") ApiRequestParam apiRequestParam) {
		int detailId = apiRequestParam.getDetailId();
		ApiRequestParam exist = dataApiService.getRequestParam(detailId, apiRequestParam.getName());
		if (null != exist) {
			return ResultUtil.error("添加失败,名称不能重复", null);
		}
		ApiRequestParam save = dataApiService.saveRequestParam(apiRequestParam);
		if (null != save) {
			return ResultUtil.success("添加成功", save);
		}
		return ResultUtil.addError();
	}

	/**
	 * 修改
	 * 
	 * @param apiRequestParam
	 * @return
	 */
	@RequestMapping(value = "/request/param/update", method = RequestMethod.POST)
	public String updateRequestParam(@ModelAttribute("apiRequestParam") ApiRequestParam apiRequestParam) {
		int detailId = apiRequestParam.getDetailId();
		if (detailId == 0) {
			detailId = dataApiService.getRequestParamById(apiRequestParam.getId()).getDetailId();
		}
		ApiRequestParam exist = dataApiService.getRequestParam(detailId, apiRequestParam.getName());
		if (null != exist) {
			return ResultUtil.error("修改失败,名称不能重复", null);
		}
		ApiRequestParam upd = dataApiService.updRequestParam(apiRequestParam);
		if (null != upd) {
			return ResultUtil.success("修改成功", upd);
		}
		return ResultUtil.updError();
	}

	/** -------------apiReturnParamRepository----------------------------------- */

	/**
	 * 翻页
	 * 
	 * @param apiId
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@RequestMapping("/return/param/list")
	public String listReturnParam(@RequestParam(value = "detailId", required = true) int detailId,
			@RequestParam(value = "number", required = false, defaultValue = "1") int pageNum,
			@RequestParam(value = "size", required = false, defaultValue = "10") int pageSize) {
		Page<ApiReturnParam> pages = dataApiService.pageReturnParamList(detailId, pageNum, pageSize);
		return ResultUtil.success("请求成功", pages);
	}

	/**
	 * 获取对象
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/return/param/get/{id}")
	public String getReturnParam(@PathVariable int id) {
		ApiReturnParam apiReturnParam = dataApiService.getReturnParamById(id);
		if (null != apiReturnParam) {
			return ResultUtil.success("获取成功", apiReturnParam);
		}
		return ResultUtil.getError();
	}

	/**
	 * 删除
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/return/param/delete/{id}")
	public String deleteReturnParam(@PathVariable int id) {
		boolean del = dataApiService.delReturnParamById(id);
		if (del) {
			return ResultUtil.delSuccess();
		}
		return ResultUtil.delError();
	}

	/**
	 * 保存
	 * 
	 * @param apiRequestParam
	 * @return
	 */
	@RequestMapping(value = "/return/param/save", method = RequestMethod.POST)
	public String saveReturnParam(@ModelAttribute("apiReturnParam") ApiReturnParam apiReturnParam) {
		int detailId = apiReturnParam.getDetailId();
		ApiReturnParam exist = dataApiService.getReturnParam(detailId, apiReturnParam.getName());
		if (null != exist) {
			return ResultUtil.error("添加失败,名称不能重复", null);
		}
		ApiReturnParam save = dataApiService.saveReturnParam(apiReturnParam);
		if (null != save) {
			return ResultUtil.success("添加成功", save);
		}
		return ResultUtil.addError();
	}

	/**
	 * 修改
	 * 
	 * @param apiRequestParam
	 * @return
	 */
	@RequestMapping(value = "/return/param/update", method = RequestMethod.POST)
	public String updateReturnParam(@ModelAttribute("apiReturnParam") ApiReturnParam apiReturnParam) {
		int detailId = apiReturnParam.getDetailId();
		if (detailId == 0) {
			detailId = dataApiService.getReturnParamById(apiReturnParam.getId()).getDetailId();
		}
		ApiReturnParam exist = dataApiService.getReturnParam(detailId, apiReturnParam.getName());
		if (null != exist) {
			return ResultUtil.error("修改失败,名称不能重复", null);
		}
		ApiReturnParam upd = dataApiService.updReturnParam(apiReturnParam);
		if (null != upd) {
			return ResultUtil.success("修改成功", upd);
		}
		return ResultUtil.updError();
	}

	/** --------------apiReturnExampleRepository----------------------------- */

	/**
	 * 获取对象
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/return/example/get/{id}")
	public String getReturnExampleId(@PathVariable int id) {
		ApiReturnExample apiReturnParam = dataApiService.getReturnExampleById(id);
		if (null != apiReturnParam) {
			return ResultUtil.success("获取成功", apiReturnParam);
		}
		return ResultUtil.getError();
	}

	@RequestMapping(value = "/return/example/get")
	public String getReturnExample(@RequestParam(value = "detailId", required = true) int detailId) {
		ApiReturnExample apiReturnParam = dataApiService.getReturnExample(detailId);
		if (null != apiReturnParam) {
			return ResultUtil.success("获取成功", apiReturnParam);
		}
		return ResultUtil.error("没有数据", null);
	}

	/**
	 * 删除
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/return/example/delete/{id}")
	public String deleteReturnExample(@PathVariable int id) {
		boolean del = dataApiService.delReturnExampleById(id);
		if (del) {
			return ResultUtil.delSuccess();
		}
		return ResultUtil.delError();
	}

	/**
	 * 保存
	 * 
	 * @param apiRequestParam
	 * @return
	 */
	@RequestMapping(value = "/return/example/save", method = RequestMethod.POST)
	public String saveReturnExample(@ModelAttribute("apiReturnExample") ApiReturnExample apiReturnExample) {
		ApiReturnExample save = dataApiService.saveReturnExample(apiReturnExample);
		if (null != save) {
			return ResultUtil.success("添加成功", save);
		}
		return ResultUtil.addError();
	}

	/**
	 * 修改
	 * 
	 * @param apiRequestParam
	 * @return
	 */
	@RequestMapping(value = "/return/example/update", method = RequestMethod.POST)
	public String updateReturnExample(@ModelAttribute("apiReturnExample") ApiReturnExample apiReturnExample) {
		ApiReturnExample upd = dataApiService.updReturnExample(apiReturnExample);
		if (null != upd) {
			return ResultUtil.success("修改成功", upd);
		}
		return ResultUtil.updError();
	}

	@RequestMapping(value = "/return/example/addorupd", method = RequestMethod.POST)
	public String addOrUpdReturnExample(@ModelAttribute("apiReturnExample") ApiReturnExample apiReturnExample) {
		ApiReturnExample addAndUpd = dataApiService.addAndUpdReturnExample(apiReturnExample);
		if (null != addAndUpd) {
			return ResultUtil.success("操作成功", addAndUpd);
		}
		return ResultUtil.error("操作失败", null);
	}

	/** --------------apiRequestExampleRepository--------------------------- */

	@RequestMapping(value = "/request/example/save", method = RequestMethod.POST)
	public String saveRequestExample(@ModelAttribute("apiRequestExample") ApiRequestExample apiRequestExample) {
		ApiRequestExample save = dataApiService.saveRequestExample(apiRequestExample);
		if (null != save) {
			return ResultUtil.success("添加成功", save);
		}
		return ResultUtil.addError();
	}

	@RequestMapping(value = "/request/example/get/{id}")
	public String getRequestExample(@PathVariable int id) {
		ApiRequestExample apiRequestExample = dataApiService.getRequestExampleById(id);
		if (null != apiRequestExample) {
			return ResultUtil.success("获取成功", apiRequestExample);
		}
		return ResultUtil.getError();
	}

	@RequestMapping(value = "/request/example/get")
	public String getRequestExample(@RequestParam(value = "detailId", required = true) int detailId,
			@RequestParam(value = "language", required = true) String languagestr) {
		APIType.Language language = APIType.Language.valueOf(languagestr.toUpperCase());
		ApiRequestExample apiRequestExample = dataApiService.getRequestExample(detailId, language);
		if (null != apiRequestExample) {
			return ResultUtil.success("获取成功", apiRequestExample);
		}
		return ResultUtil.error("没有数据", null);
	}

	/**
	 * 删除
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/request/example/delete/{id}")
	public String deleteRequestExample(@PathVariable int id) {
		boolean del = dataApiService.delRequestExampleById(id);
		if (del) {
			return ResultUtil.delSuccess();
		}
		return ResultUtil.delError();
	}

	/**
	 * 添加修改
	 * 
	 * @param apiRequestExample
	 * @return
	 */
	@RequestMapping(value = "/request/example/addorupd", method = RequestMethod.POST)
	public String addOrUpdRequestExample(@ModelAttribute("apiRequestExample") ApiRequestExample apiRequestExample) {
		ApiRequestExample addAndUpd = dataApiService.addAndUpdRequestExample(apiRequestExample);
		if (null != addAndUpd) {
			return ResultUtil.success("操作成功", addAndUpd);
		}
		return ResultUtil.error("操作失败", null);
	}

	/**
	 * 产品页获取detail详细
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/detail/list/all/{id}")
	public String listAllDetail(@PathVariable int id) {
		Map<String,Object> maps = new HashMap<String,Object>();
		maps.put("apiDetail", dataApiService.getByApiDetailId(id));
		maps.put("requestParms", dataApiService.listRequestParamList(id));
		maps.put("requestExample", dataApiService.getRequestExample(id, APIType.Language.JAVA));
		maps.put("returnExample", dataApiService.getReturnExample(id));
		maps.put("returnParms", dataApiService.listReturnParamList(id));
		return ResultUtil.success("请求成功", maps);
	}

}
