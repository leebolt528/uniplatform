package com.bonc.uni.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;


/**
 * 日期处理工具类
 * @author futao
 * 2017年9月13日
 */
public class DateUtil {

	public static final String YYYYMMDD_HHMMSS = "yyyy-MM-dd HH:mm:ss";

	public static final String YYYYMMDD = "yyyyMMdd";
	
	public static final String YYYYMMDDHHMMSS = "yyyy.MM.dd HH:mm:ss";

	/**
	 * 将字符串转成日期对象
	 * 
	 * @param dateString
	 * @param dateFormat
	 * @return
	 */
	public static Date stringToDate(String dateString, String dateFormat) {
		if (StringUtils.isBlank(dateString)) {
			return null;
		}
		try {
			return new SimpleDateFormat(dateFormat).parse(dateString);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * getDateTimeBeforeN:获取n天前的时间. <br/>
	 * 
	 * @param n
	 *            N天前
	 * @return N天前的时间
	 */
	public static long getTimeBeforeN(int n) {
		return new Date(System.currentTimeMillis() - n * 1000 * 60 * 60 * 24).getTime();
	}

	/**
	 * 将时间戳转换为时间
	 * 
	 * @param timeStamp
	 *            时间戳
	 * @return
	 */
	public static String stampToDate(String timeStamp) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date(Long.parseLong(timeStamp));
		return simpleDateFormat.format(date);
	}

	/**
	 * 日期转化为时间戳
	 */
	public static long dateToStamp(String data) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.parse(data).getTime();
	}

	/**
	 * 是否为yyyy.MM.dd HH格式
	 * 
	 * @param date
	 *            日期
	 * @return 是否为小时格式
	 */
	public static boolean isTimeHourFormat(String date) {
		String regex = "[0-9]+\\.[0-9]+\\.[0-9]+\\s[0-9][0-9]$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(date);
		if (matcher.find()) {
			return true;
		}
		return false;
	}

	/**
	 * 从Date对象得到Calendar对象. <BR>
	 * JDK提供了Calendar.getTime()方法, 可从Calendar对象得到Date对象,
	 * 但没有提供从Date对象得到Calendar对象的方法.
	 * 
	 * @param date
	 *            给定的Date对象
	 * @return 得到的Calendar对象. 如果date参数为null, 则得到表示当前时间的Calendar对象.
	 */
	public static Calendar toCalendar(Date date) {
		Calendar cal = Calendar.getInstance();
		if (date != null) {
			cal.setTime(date);
		}
		return cal;
	}

	/**
	 * 格式化时间
	 * 
	 * @param date
	 * @param pattern
	 */
	public static String format2String(Date date, String pattern) {
		return new SimpleDateFormat(pattern).format(date);
	}

	public static String timeMillis2Stirng(long timeMillis) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date(timeMillis);
		return simpleDateFormat.format(date);
	}
	
	public static String timeMillis2Stirng(long timeMillis, String pattern) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		Date date = new Date(timeMillis);
		return simpleDateFormat.format(date);
	}

	/**
	 * 获取前几天或后几天
	 * 
	 * @param date
	 * @param day
	 * @return
	 */
	public static Date intervalDate(Date date, int day) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, day);
		date = calendar.getTime();
		return date;
	}

	public static Date intervalDate(int day) {
		return intervalDate(new Date(), day);
	}

	/**
	 * 获取当前时间
	 */
	public static String getCurrentTime (String format) {
		Date now = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		String currentTime = simpleDateFormat.format(now);
		return currentTime;
	}
}
