package com.bonc.uni.common.util;

import java.io.File;

public class PathUtil {

    public static String getConfigPath() {
        File file = new File(System.getProperty("user.dir") + File.separator + "config");
        String configPath = file.getAbsolutePath();
        if (!file.exists()) {
            configPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        }
        return configPath;
    }


}
