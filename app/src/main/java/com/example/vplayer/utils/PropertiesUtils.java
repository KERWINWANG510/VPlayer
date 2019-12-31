package com.example.vplayer.utils;

import android.content.Context;

import java.io.InputStream;
import java.util.Properties;

/**
 * 配置文件工具类
 */
public class PropertiesUtils {
    public static Properties getProperties(Context c){
        Properties props = new Properties();
        try {
            //通过class获取setting.properties的FileInputStream
            InputStream in = PropertiesUtils.class.getResourceAsStream("/assets/config.properties ");
            props.load(in);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return props;
    }
}
