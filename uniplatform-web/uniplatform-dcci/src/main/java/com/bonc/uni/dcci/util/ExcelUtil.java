package com.bonc.uni.dcci.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.bonc.uni.common.util.MD5Util;
import com.bonc.uni.common.util.SpringContextUtil;
import com.bonc.uni.dcci.entity.Site;
import com.bonc.uni.dcci.entity.TaskRelation;
import com.bonc.uni.dcci.entity.TaskSite;
import com.bonc.uni.dcci.service.SiteService;
import com.bonc.uni.dcci.service.TaskManageService;

/**
 * excel 工具类
 * 
 * @author futao 2017年9月12日
 */
@SuppressWarnings("deprecation")
public class ExcelUtil {

	final String URL_PATTERN = "(http://|ftp://|https://|www){0,1}[^\u4e00-\u9fa5\\s]*?\\.(com|net|cn|me|tw|fr)[^\u4e00-\u9fa5\\s]*";

	Pattern urlPattern = Pattern.compile(URL_PATTERN);

	SiteService siteService;

	TaskManageService taskManageService;

	private Workbook workbook;
	private OutputStream os;
	private String pattern;// 日期格式

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public ExcelUtil(Workbook workboook) {
		this.workbook = workboook;
	}

	public ExcelUtil(InputStream is, String version) throws FileNotFoundException, IOException {
		siteService = SpringContextUtil.getBean("siteService");
		taskManageService = SpringContextUtil.getBean("taskManageService");
		if ("2003".equals(version)) {
			workbook = new HSSFWorkbook(is);
		} else {
			workbook = new XSSFWorkbook(is);
		}
	}

	public String toString() {

		return "共有 " + getSheetCount() + "个sheet 页！";
	}

	public String toString(int sheetIx) throws IOException {

		return "第 " + (sheetIx + 1) + "个sheet 页，名称： " + getSheetName(sheetIx) + "，共 " + getRowCount(sheetIx) + "行！";
	}

	/**
	 * 
	 * 根据后缀判断是否为 Excel 文件，后缀匹配xls和xlsx
	 * 
	 * @param pathname
	 * @return
	 * 
	 */
	public static boolean isExcel(String pathname) {
		if (pathname == null) {
			return false;
		}
		return pathname.endsWith(".xls") || pathname.endsWith(".xlsx");
	}

	/**
	 * 
	 * 读取 Excel 第一页所有数据
	 * 
	 * @return
	 * @throws Exception
	 * 
	 */
	public List<List<String>> read() throws Exception {
		return read(0, 0, getRowCount(0) - 1);
	}

	/**
	 * 
	 * 读取指定sheet 页所有数据
	 * 
	 * @param sheetIx
	 *            指定 sheet 页，从 0 开始
	 * @return
	 * @throws Exception
	 */
	public List<List<String>> read(int sheetIx) throws Exception {
		return read(sheetIx, 0, getRowCount(sheetIx) - 1);
	}

	/**
	 * 
	 * 读取指定sheet 页指定行数据
	 * 
	 * @param sheetIx
	 *            指定 sheet 页，从 0 开始
	 * @param start
	 *            指定开始行，从 0 开始
	 * @param end
	 *            指定结束行，从 0 开始
	 * @return
	 * @throws Exception
	 */
	public List<List<String>> read(int sheetIx, int start, int end) throws Exception {
		Sheet sheet = workbook.getSheetAt(sheetIx);
		List<List<String>> list = new ArrayList<List<String>>();

		if (end > getRowCount(sheetIx)) {
			end = getRowCount(sheetIx);
		}

		int cols = sheet.getRow(0).getLastCellNum(); // 第一行总列数

		for (int i = start; i <= end; i++) {
			List<String> rowList = new ArrayList<String>();
			Row row = sheet.getRow(i);
			for (int j = 0; j < cols; j++) {
				if (row == null) {
					rowList.add(null);
					continue;
				}
				rowList.add(getCellValueToString(row.getCell(j)));
			}
			list.add(rowList);
		}

		return list;
	}

	/**
	 * 读取上传配置的excel
	 */
	public Map<String, Object> readUploadExcel() {
		Map<String, Object> maps = new HashMap<String, Object>();
		List<String> fails = new LinkedList<String>();
		Map<String, String> urls = new HashMap<String, String>();
		int sheetIx = 0;
		Sheet sheet = workbook.getSheetAt(sheetIx);
		int start = 0;
		int end = getRowCount(0) - 1;
		if (end > getRowCount(sheetIx)) {
			end = getRowCount(sheetIx);
		}
		if (end > getRowCount(sheetIx)) {
			end = getRowCount(sheetIx);
		}
		// int cols = sheet.getRow(0).getLastCellNum(); // 第一行总列数
		for (int i = start; i <= end; i++) {
			Row row = sheet.getRow(i);
			if (null != row) {
				String status = getCellValueToString(row.getCell(4));
				if (StringUtils.isNotBlank(status)) {
					String url = getCellValueToString(row.getCell(2));
					String urlHash = MD5Util.MD5Url(url);
					if (null == urlHash) {
						continue;
					} else {
						Matcher matcher = urlPattern.matcher(status);
						if (matcher.find()) {
							urls.put(urlHash, status);
						} else {
							fails.add(urlHash);
						}
					}

				}
			}
		}
		maps.put("fails", fails);
		maps.put("urls", urls);
		return maps;
	}

	/**
	 * 
	 * 读取指定sheet 页指定行数据
	 * 
	 * @param sheetIx
	 *            指定 sheet 页，从 0 开始
	 * @param start
	 *            指定开始行，从 0 开始
	 * @param end
	 *            指定结束行，从 0 开始
	 * @return
	 * @throws Exception
	 */
	public Map<StatusType.UrlSiteType2, List<TaskSite>> readToSite(TaskRelation taskRelation) throws Exception {
		Map<StatusType.UrlSiteType2, List<TaskSite>> maps = new HashMap<StatusType.UrlSiteType2, List<TaskSite>>();
		int sheetIx = 0;
		Sheet sheet = workbook.getSheetAt(sheetIx);
		Map<String, TaskSite> mapAll = new HashMap<String, TaskSite>();
		Map<String, TaskSite> mapRepeat = new HashMap<String, TaskSite>();
		List<TaskSite> list = new LinkedList<TaskSite>();
		List<TaskSite> repeat = new LinkedList<TaskSite>();
		int taskId = taskRelation.getId();
		int start = 0;
		int end = getRowCount(0) - 1;
		if (end > getRowCount(sheetIx)) {
			end = getRowCount(sheetIx);
		}

		for (int i = start; i <= end; i++) {
			Row row = sheet.getRow(i);
			TaskSite taskSite = new TaskSite();
			if (null != row) {
				taskSite.setTaskRelation(taskId);
				taskSite.setName(getCellValueToString(row.getCell(0)));
				taskSite.setBoard(getCellValueToString(row.getCell(1)));
				taskSite.setUrl(getCellValueToString(row.getCell(2)));
				String urlHash = MD5Util.MD5Url(taskSite.getUrl());

				if (null == urlHash) {
					continue;
				} else {
					Site site = siteService.getSite(urlHash, 1);
					taskSite.setUrlHash(urlHash);
					if (null == site) {
						/*
						 * TaskSite taskSiteg = taskManageService.getSiteByHashS(urlHash); if (null ==
						 * taskSiteg) { taskSite.setStatus(StatusType.UrlSiteType.UNDISTRIBUTED); } else
						 * { taskSite.setStatus(StatusType.UrlSiteType.REPEAT); mapRepeat.put(urlHash,
						 * taskSite); }
						 */
						taskSite.setStatus(StatusType.UrlSiteType.UNDISTRIBUTED);
					} else {
						taskSite.setStatus(StatusType.UrlSiteType.REPEAT);
						mapRepeat.put(urlHash, taskSite);
					}
				}
				mapAll.put(urlHash, taskSite);
			}

		}
		list.addAll(mapAll.values());
		repeat.addAll(mapRepeat.values());
		maps.put(StatusType.UrlSiteType2.REPEAT, repeat);
		maps.put(StatusType.UrlSiteType2.ALL, list);
		return maps;
	}

	/**
	 * 
	 * 将数据写入到 Excel 默认第一页中，从第1行开始写入
	 * 
	 * @param rowData
	 *            数据
	 * @return
	 * @throws IOException
	 * 
	 */
	public boolean write(List<List<String>> rowData) throws IOException {
		return write(0, rowData, 0);
	}

	/**
	 * 
	 * 将数据写入到 Excel 新创建的 Sheet 页
	 * 
	 * @param rowData
	 *            数据
	 * @param sheetName
	 *            长度为1-31，不能包含后面任一字符: ：\ / ? * [ ]
	 * @return
	 * @throws IOException
	 */
	public boolean write(List<List<String>> rowData, String sheetName, boolean isNewSheet) throws IOException {
		Sheet sheet = null;
		if (isNewSheet) {
			sheet = workbook.createSheet(sheetName);
		} else {
			sheet = workbook.createSheet();
		}
		int sheetIx = workbook.getSheetIndex(sheet);
		return write(sheetIx, rowData, 0);
	}

	/**
	 * 
	 * 将数据追加到sheet页最后
	 * 
	 * @param rowData
	 *            数据
	 * @param sheetIx
	 *            指定 Sheet 页，从 0 开始
	 * @param isAppend
	 *            是否追加,true 追加，false 重置sheet再添加
	 * @return
	 * @throws IOException
	 */
	public boolean write(int sheetIx, List<List<String>> rowData, boolean isAppend) throws IOException {
		if (isAppend) {
			return write(sheetIx, rowData, getRowCount(sheetIx));
		} else {// 清空再添加
			clearSheet(sheetIx);
			return write(sheetIx, rowData, 0);
		}
	}

	/**
	 * 
	 * 将数据写入到 Excel 指定 Sheet 页指定开始行中,指定行后面数据向后移动
	 * 
	 * @param rowData
	 *            数据
	 * @param sheetIx
	 *            指定 Sheet 页，从 0 开始
	 * @param startRow
	 *            指定开始行，从 0 开始
	 * @return
	 * @throws IOException
	 */
	public boolean write(int sheetIx, List<List<String>> rowData, int startRow) throws IOException {
		Sheet sheet = workbook.getSheetAt(sheetIx);
		int dataSize = rowData.size();
		if (getRowCount(sheetIx) > 0) {// 如果小于等于0，则一行都不存在
			sheet.shiftRows(startRow, getRowCount(sheetIx), dataSize);
		}
		for (int i = 0; i < dataSize; i++) {
			Row row = sheet.createRow(i + startRow);
			for (int j = 0; j < rowData.get(i).size(); j++) {
				Cell cell = row.createCell(j);
				cell.setCellValue(rowData.get(i).get(j) + "");
			}
		}
		return true;
	}

	/**
	 * 写入对象中
	 * 
	 * @param sites
	 * @return
	 * @throws IOException
	 */
	public boolean writeToTaskSite(List<TaskSite> sites) throws IOException {

		Sheet sheet = workbook.createSheet();
		sheet.setColumnWidth(2, 20000);
		/*
		 * Font headfont = workbook.createFont(); headfont.setFontName("黑体");
		 * headfont.setFontHeightInPoints((short) 12);// 字体大小 headfont.setBold(true);
		 * CellStyle style = workbook.createCellStyle();
		 * style.setAlignment(HorizontalAlignment.GENERAL); style.setFont(headfont); Row
		 * hrow = sheet.createRow(0); Cell cell = hrow.createCell(0);
		 * cell.setCellValue("站点名"); cell.setCellStyle(style); cell =
		 * hrow.createCell(1); cell.setCellValue("板块"); cell.setCellStyle(style); cell =
		 * hrow.createCell(2); cell.setCellValue("url"); cell.setCellStyle(style); cell
		 * = hrow.createCell(3); cell.setCellValue("状态"); cell.setCellStyle(style);
		 */
		int dataSize = sites.size();
		for (int i = 0; i < dataSize; i++) {
			TaskSite taskSite = sites.get(i);
			Row row = sheet.createRow(i);
			Cell cell = row.createCell(0);
			cell.setCellValue(taskSite.getName());
			cell = row.createCell(1);
			cell.setCellValue(taskSite.getBoard());
			cell = row.createCell(2);
			cell.setCellValue(taskSite.getUrl());
			cell = row.createCell(3);
			cell.setCellValue(taskSite.getStatus().getValue());
		}
		return true;
	}

	public boolean writeToSite(List<Site> sites) throws IOException {

		Sheet sheet = workbook.createSheet();
		sheet.setColumnWidth(6, 20000);
		Font headfont = workbook.createFont();
		headfont.setFontName("黑体");
		headfont.setFontHeightInPoints((short) 12);// 字体大小
		headfont.setBold(true);
		CellStyle style = workbook.createCellStyle();
		style.setAlignment(HorizontalAlignment.GENERAL);
		style.setFont(headfont);
		Row hrow = sheet.createRow(0);
		Cell cell = hrow.createCell(0);
		cell.setCellValue("站点名");
		cell.setCellStyle(style);
		cell = hrow.createCell(1);
		cell.setCellValue("板块");
		cell.setCellStyle(style);
		cell = hrow.createCell(2);
		cell.setCellValue("频道");
		cell.setCellStyle(style);
		/*cell = hrow.createCell(3);
		cell.setCellValue("媒体属性");
		cell.setCellStyle(style);
		cell = hrow.createCell(4);
		cell.setCellValue("媒体类型");
		cell.setCellStyle(style);
		cell = hrow.createCell(5);
		cell.setCellValue("行业类型");
		cell.setCellStyle(style);*/
		cell = hrow.createCell(3);
		cell.setCellValue("url");
		cell.setCellStyle(style);
		int dataSize = sites.size();
		for (int i = 0; i < dataSize; i++) {
			Site taskSite = sites.get(i);
			Row row = sheet.createRow(i + 1);
			cell = row.createCell(0);
			cell.setCellValue(taskSite.getName());
			cell = row.createCell(1);
			cell.setCellValue(taskSite.getBoard());
			cell = row.createCell(2);
			cell.setCellValue(taskSite.getCategory());
			/*cell = row.createCell(3);
			cell.setCellValue(taskSite.getMediaPro());
			cell = row.createCell(4);
			cell.setCellValue(taskSite.getMediaType());
			cell = row.createCell(5);
			cell.setCellValue(taskSite.getIndustry());*/
			cell = row.createCell(3);
			cell.setCellValue(taskSite.getUrl());
		}
		return true;
	}

	/**
	 * 
	 * 设置cell 样式
	 * 
	 * @param sheetIx
	 *            指定 Sheet 页，从 0 开始
	 * @param colIndex
	 *            指定列，从 0 开始
	 * @return
	 * @throws IOException
	 */
	public boolean setStyle(int sheetIx, int rowIndex, int colIndex, CellStyle style) throws IOException {
		Sheet sheet = workbook.getSheetAt(sheetIx);
		// sheet.autoSizeColumn(colIndex, true);// 设置列宽度自适应
		sheet.setColumnWidth(colIndex, 4000);

		Cell cell = sheet.getRow(rowIndex).getCell(colIndex);
		cell.setCellStyle(style);

		return true;
	}

	/**
	 * 
	 * 设置样式
	 * 
	 * @param type
	 *            1：标题 2：第一行
	 * @return
	 */
	public CellStyle makeStyle(int type) {
		CellStyle style = workbook.createCellStyle();

		DataFormat format = workbook.createDataFormat();
		style.setDataFormat(format.getFormat("@"));// // 内容样式 设置单元格内容格式是文本
		style.setAlignment(CellStyle.ALIGN_CENTER);// 内容居中

		// style.setBorderTop(CellStyle.BORDER_THIN);// 边框样式
		// style.setBorderRight(CellStyle.BORDER_THIN);
		// style.setBorderBottom(CellStyle.BORDER_THIN);
		// style.setBorderLeft(CellStyle.BORDER_THIN);

		Font font = workbook.createFont();// 文字样式

		if (type == 1) {
			// style.setFillForegroundColor(HSSFColor.LIGHT_BLUE.index);//颜色样式
			// 前景颜色
			// style.setFillBackgroundColor(HSSFColor.LIGHT_BLUE.index);//背景色
			// style.setFillPattern(CellStyle.ALIGN_FILL);// 填充方式
			font.setBold(true);
			font.setFontHeight((short) 500);
		}

		if (type == 2) {
			font.setBold(true);
			font.setFontHeight((short) 300);
		}

		style.setFont(font);

		return style;
	}

	/**
	 * 
	 * 合并单元格
	 * 
	 * @param sheetIx
	 *            指定 Sheet 页，从 0 开始
	 * @param firstRow
	 *            开始行
	 * @param lastRow
	 *            结束行
	 * @param firstCol
	 *            开始列
	 * @param lastCol
	 *            结束列
	 */
	public void region(int sheetIx, int firstRow, int lastRow, int firstCol, int lastCol) {
		Sheet sheet = workbook.getSheetAt(sheetIx);
		sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));
	}

	/**
	 * 
	 * 指定行是否为空
	 * 
	 * @param sheetIx
	 *            指定 Sheet 页，从 0 开始
	 * @param rowIndex
	 *            指定开始行，从 0 开始
	 * @return true 不为空，false 不行为空
	 * @throws IOException
	 */
	public boolean isRowNull(int sheetIx, int rowIndex) throws IOException {
		Sheet sheet = workbook.getSheetAt(sheetIx);
		return sheet.getRow(rowIndex) == null;
	}

	/**
	 * 
	 * 创建行，若行存在，则清空
	 * 
	 * @param sheetIx
	 *            指定 sheet 页，从 0 开始
	 * @param rownum
	 *            指定创建行，从 0 开始
	 * @return
	 * @throws IOException
	 */
	public boolean createRow(int sheetIx, int rowIndex) throws IOException {
		Sheet sheet = workbook.getSheetAt(sheetIx);
		sheet.createRow(rowIndex);
		return true;
	}

	/**
	 * 
	 * 指定单元格是否为空
	 * 
	 * @param sheetIx
	 *            指定 Sheet 页，从 0 开始
	 * @param rowIndex
	 *            指定开始行，从 0 开始
	 * @param colIndex
	 *            指定开始列，从 0 开始
	 * @return true 行不为空，false 行为空
	 * @throws IOException
	 */
	public boolean isCellNull(int sheetIx, int rowIndex, int colIndex) throws IOException {
		Sheet sheet = workbook.getSheetAt(sheetIx);
		if (!isRowNull(sheetIx, rowIndex)) {
			return false;
		}
		Row row = sheet.getRow(rowIndex);
		return row.getCell(colIndex) == null;
	}

	/**
	 * 
	 * 创建单元格
	 * 
	 * @param sheetIx
	 *            指定 sheet 页，从 0 开始
	 * @param rowIndex
	 *            指定行，从 0 开始
	 * @param colIndex
	 *            指定创建列，从 0 开始
	 * @return true 列为空，false 行不为空
	 * @throws IOException
	 */
	public boolean createCell(int sheetIx, int rowIndex, int colIndex) throws IOException {
		Sheet sheet = workbook.getSheetAt(sheetIx);
		Row row = sheet.getRow(rowIndex);
		row.createCell(colIndex);
		return true;
	}

	/**
	 * 返回sheet 中的行数
	 * 
	 * 
	 * @param sheetIx
	 *            指定 Sheet 页，从 0 开始
	 * @return
	 */
	public int getRowCount(int sheetIx) {
		Sheet sheet = workbook.getSheetAt(sheetIx);
		if (sheet.getPhysicalNumberOfRows() == 0) {
			return 0;
		}
		return sheet.getLastRowNum() + 1;

	}

	/**
	 * 
	 * 返回所在行的列数
	 * 
	 * @param sheetIx
	 *            指定 Sheet 页，从 0 开始
	 * @param rowIndex
	 *            指定行，从0开始
	 * @return 返回-1 表示所在行为空
	 */
	public int getColumnCount(int sheetIx, int rowIndex) {
		Sheet sheet = workbook.getSheetAt(sheetIx);
		Row row = sheet.getRow(rowIndex);
		return row == null ? -1 : row.getLastCellNum();

	}

	/**
	 * 
	 * 设置row 和 column 位置的单元格值
	 * 
	 * @param sheetIx
	 *            指定 Sheet 页，从 0 开始
	 * @param rowIndex
	 *            指定行，从0开始
	 * @param colIndex
	 *            指定列，从0开始
	 * @param value
	 *            值
	 * @return
	 * @throws IOException
	 */
	public boolean setValueAt(int sheetIx, int rowIndex, int colIndex, String value) throws IOException {
		Sheet sheet = workbook.getSheetAt(sheetIx);
		sheet.getRow(rowIndex).getCell(colIndex).setCellValue(value);
		return true;
	}

	/**
	 * 
	 * 返回 row 和 column 位置的单元格值
	 * 
	 * @param sheetIx
	 *            指定 Sheet 页，从 0 开始
	 * @param rowIndex
	 *            指定行，从0开始
	 * @param colIndex
	 *            指定列，从0开始
	 * @return
	 * 
	 */
	public String getValueAt(int sheetIx, int rowIndex, int colIndex) {
		Sheet sheet = workbook.getSheetAt(sheetIx);
		return getCellValueToString(sheet.getRow(rowIndex).getCell(colIndex));
	}

	/**
	 * 
	 * 重置指定行的值
	 * 
	 * @param rowData
	 *            数据
	 * @param sheetIx
	 *            指定 Sheet 页，从 0 开始
	 * @param rowIndex
	 *            指定行，从0开始
	 * @return
	 * @throws IOException
	 */
	public boolean setRowValue(int sheetIx, List<String> rowData, int rowIndex) throws IOException {
		Sheet sheet = workbook.getSheetAt(sheetIx);
		Row row = sheet.getRow(rowIndex);
		for (int i = 0; i < rowData.size(); i++) {
			row.getCell(i).setCellValue(rowData.get(i));
		}
		return true;
	}

	/**
	 * 
	 * 返回指定行的值的集合
	 * 
	 * @param sheetIx
	 *            指定 Sheet 页，从 0 开始
	 * @param rowIndex
	 *            指定行，从0开始
	 * @return
	 */
	public List<String> getRowValue(int sheetIx, int rowIndex) {
		Sheet sheet = workbook.getSheetAt(sheetIx);
		Row row = sheet.getRow(rowIndex);
		List<String> list = new ArrayList<String>();
		if (row == null) {
			list.add(null);
		} else {
			for (int i = 0; i < row.getLastCellNum(); i++) {
				list.add(getCellValueToString(row.getCell(i)));
			}
		}
		return list;
	}

	/**
	 * 
	 * 返回列的值的集合
	 * 
	 * @param sheetIx
	 *            指定 Sheet 页，从 0 开始
	 * @param rowIndex
	 *            指定行，从0开始
	 * @param colIndex
	 *            指定列，从0开始
	 * @return
	 */
	public List<String> getColumnValue(int sheetIx, int rowIndex, int colIndex) {
		Sheet sheet = workbook.getSheetAt(sheetIx);
		List<String> list = new ArrayList<String>();
		for (int i = rowIndex; i < getRowCount(sheetIx); i++) {
			Row row = sheet.getRow(i);
			if (row == null) {
				list.add(null);
				continue;
			}
			list.add(getCellValueToString(sheet.getRow(i).getCell(colIndex)));
		}
		return list;
	}

	/**
	 * 
	 * 获取excel 中sheet 总页数
	 * 
	 * @return
	 */
	public int getSheetCount() {
		return workbook.getNumberOfSheets();
	}

	public void createSheet() {
		workbook.createSheet();
	}

	/**
	 * 
	 * 设置sheet名称，长度为1-31，不能包含后面任一字符: ：\ / ? * [ ]
	 * 
	 * @param sheetIx
	 *            指定 Sheet 页，从 0 开始，//
	 * @param name
	 * @return
	 * @throws IOException
	 */
	public boolean setSheetName(int sheetIx, String name) throws IOException {
		workbook.setSheetName(sheetIx, name);
		return true;
	}

	/**
	 * 
	 * 获取 sheet名称
	 * 
	 * @param sheetIx
	 *            指定 Sheet 页，从 0 开始
	 * @return
	 * @throws IOException
	 */
	public String getSheetName(int sheetIx) throws IOException {
		Sheet sheet = workbook.getSheetAt(sheetIx);
		return sheet.getSheetName();
	}

	/**
	 * 获取sheet的索引，从0开始
	 * 
	 * @param name
	 *            sheet 名称
	 * @return -1表示该未找到名称对应的sheet
	 */
	public int getSheetIndex(String name) {
		return workbook.getSheetIndex(name);
	}

	/**
	 * 
	 * 删除指定sheet
	 * 
	 * @param sheetIx
	 *            指定 Sheet 页，从 0 开始
	 * @return
	 * @throws IOException
	 */
	public boolean removeSheetAt(int sheetIx) throws IOException {
		workbook.removeSheetAt(sheetIx);
		return true;
	}

	/**
	 * 
	 * 删除指定sheet中行，改变该行之后行的索引
	 * 
	 * @param sheetIx
	 *            指定 Sheet 页，从 0 开始
	 * @param rowIndex
	 *            指定行，从0开始
	 * @return
	 * @throws IOException
	 */
	public boolean removeRow(int sheetIx, int rowIndex) throws IOException {
		Sheet sheet = workbook.getSheetAt(sheetIx);
		sheet.shiftRows(rowIndex + 1, getRowCount(sheetIx), -1);
		Row row = sheet.getRow(getRowCount(sheetIx) - 1);
		sheet.removeRow(row);
		return true;
	}

	/**
	 * 
	 * 设置sheet 页的索引
	 * 
	 * @param sheetname
	 *            Sheet 名称
	 * @param pos
	 *            Sheet 索引，从0开始
	 */
	public void setSheetOrder(String sheetname, int sheetIx) {
		workbook.setSheetOrder(sheetname, sheetIx);
	}

	/**
	 * 
	 * 清空指定sheet页（先删除后添加并指定sheetIx）
	 * 
	 * @param sheetIx
	 *            指定 Sheet 页，从 0 开始
	 * @return
	 * @throws IOException
	 */
	public boolean clearSheet(int sheetIx) throws IOException {
		String sheetname = getSheetName(sheetIx);
		removeSheetAt(sheetIx);
		workbook.createSheet(sheetname);
		setSheetOrder(sheetname, sheetIx);
		return true;
	}

	public Workbook getWorkbook() {
		return workbook;
	}

	/**
	 * 
	 * 关闭流
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		if (os != null) {
			os.close();
		}
		workbook.close();
	}

	/**
	 * 
	 * 转换单元格的类型为String 默认的 <br>
	 * 默认的数据类型：CELL_TYPE_BLANK(3), CELL_TYPE_BOOLEAN(4),
	 * CELL_TYPE_ERROR(5),CELL_TYPE_FORMULA(2), CELL_TYPE_NUMERIC(0),
	 * CELL_TYPE_STRING(1)
	 * 
	 * @param cell
	 * @return
	 * 
	 */
	private String getCellValueToString(Cell cell) {
		String strCell = "";
		if (cell == null) {
			return null;
		}
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_BOOLEAN:
			strCell = String.valueOf(cell.getBooleanCellValue());
			break;
		case Cell.CELL_TYPE_NUMERIC:
			if (HSSFDateUtil.isCellDateFormatted(cell)) {
				Date date = cell.getDateCellValue();
				if (pattern != null) {
					SimpleDateFormat sdf = new SimpleDateFormat(pattern);
					strCell = sdf.format(date);
				} else {
					strCell = date.toString();
				}
				break;
			}
			// 不是日期格式，则防止当数字过长时以科学计数法显示
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			strCell = cell.toString();
			break;
		case Cell.CELL_TYPE_STRING:
			strCell = cell.getStringCellValue();
			break;
		default:
			break;
		}
		return strCell;
	}

	public static void main(String args[]) {
		Pattern pattern = Pattern.compile(
				"(http://|ftp://|https://|www){0,1}[^\u4e00-\u9fa5\\s]*?\\.(com|net|cn|me|tw|fr)[^\u4e00-\u9fa5\\s]*");
		Matcher matcher = pattern.matcher("http://roll.mil.news.sina.com.cn/col/zgjq/index_{1,5,1,0}.shtml");
		if (matcher.find()) {
			System.out.println("99999999");
			System.out.println(matcher.group());
		}
	}
}