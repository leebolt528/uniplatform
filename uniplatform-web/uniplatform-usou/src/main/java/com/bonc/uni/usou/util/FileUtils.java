package com.bonc.uni.usou.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yedunyao on 2017/9/7.
 */
public class FileUtils {

    //生成文件名
    public static String genFileNameByDate () {
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmssZ");
        String format = sdf.format(new Date());
        return format;
    }
}
