package com.bonc.uni.dcci.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bonc.uni.dcci.util.ExcelUtil;

@RestController
@RequestMapping("/dcci")
public class DcciController {

	@RequestMapping
	public String hello() {
		return "Hello dcci!!";
	}

	@RequestMapping("/download")
	public void download(HttpServletRequest request, HttpServletResponse response) {
		try {
			response.setHeader("content-Type", "application/vnd.ms-excel");
			// 下载文件的默认名称
			response.setHeader("Content-Disposition", "attachment;filename=user.xls");
			String xlsPath = "d:/aaa.xls";
			File file = new File(xlsPath);
			ExcelUtil excel = new ExcelUtil(new FileInputStream(file), "2003");
			List<List<String>> list = excel.read();
			System.out.println(list);
			Workbook workbookss = new HSSFWorkbook();
			ExcelUtil excelw = new ExcelUtil(workbookss);
			excelw.write(list, "sheet1", false);
			workbookss.write(response.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@RequestMapping("/download/zip")
	public void downloadZip(HttpServletRequest request, HttpServletResponse response) {
		try {
			response.setHeader("content-Type", "application/vnd.ms-excel");
			// 下载文件的默认名称
			response.setHeader("Content-Disposition", "attachment;filename=user.xls");
			String xlsPath = "d:/aaa.xls";
			File file = new File(xlsPath);
			ExcelUtil excel = new ExcelUtil(new FileInputStream(file), "2003");
			List<List<String>> list = excel.read();
			System.out.println(list);
			Workbook workbookss = new HSSFWorkbook();
			ExcelUtil excelw = new ExcelUtil(workbookss);
			excelw.write(list, "sheet1", false);
			workbookss.write(response.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/media/", method = RequestMethod.GET)
	public void getDownload(HttpServletRequest request, HttpServletResponse response) {

		// Get your file stream from wherever.
		String fullPath = "d:/test.zip";
		File downloadFile = new File(fullPath);

		ServletContext context = request.getServletContext();

		// get MIME type of the file
		String mimeType = context.getMimeType(fullPath);
		if (mimeType == null) {
			// set to binary type if MIME mapping not found
			mimeType = "application/octet-stream";
			System.out.println("context getMimeType is null");
		}
		System.out.println("MIME type: " + mimeType);

		// set content attributes for the response
		response.setContentType(mimeType);
		response.setContentLength((int) downloadFile.length());

		// set headers for the response
		String headerKey = "Content-Disposition";
		String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
		response.setHeader(headerKey, headerValue);

		// Copy the stream to the response's output stream.
		try {
			InputStream myStream = new FileInputStream(fullPath);
			IOUtils.copy(myStream, response.getOutputStream());
			response.flushBuffer();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public String upload(@RequestParam("file") MultipartFile file) {
		if (!file.isEmpty()) {
			try {
				// 这里只是简单例子，文件直接输出到项目路径下。
				// 实际项目中，文件需要输出到指定位置，需要在增加代码处理。
				// 还有关于文件格式限制、文件大小限制，详见：中配置。
				BufferedOutputStream out = new BufferedOutputStream(
						new FileOutputStream(new File(file.getOriginalFilename())));
				out.write(file.getBytes());
				out.flush();
				out.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return "上传失败," + e.getMessage();
			} catch (IOException e) {
				e.printStackTrace();
				return "上传失败," + e.getMessage();
			}
			return "上传成功";
		} else {
			return "上传失败，因为文件是空的.";
		}
	}

	private final ResourceLoader resourceLoader;

	@Autowired
	public DcciController(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	@RequestMapping(method = RequestMethod.GET, value = "pic/{filename:.+}")
	@ResponseBody
	public ResponseEntity<?> getFile(@PathVariable String filename) {
		try {
			return ResponseEntity.ok(resourceLoader.getResource("file:" + Paths.get("d://", filename).toString()));
		} catch (Exception e) {
			return ResponseEntity.notFound().build();
		}
	}
}
