package com.bonc.uni.nlp.utils;

import java.util.Calendar;
import java.util.Date;

public class TimeUtil {

	public static Date getNowDate() {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);//date 换成已经已知的Date对象
        cal.add(Calendar.HOUR_OF_DAY, 8);// after 8 hour
        return cal.getTime();
	}
}
