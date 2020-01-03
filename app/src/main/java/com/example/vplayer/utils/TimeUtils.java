package com.example.vplayer.utils;

import com.example.vplayer.bean.Time;

/**
 * 时间工具类
 */
public class TimeUtils {

    /**
     * 根据传入的秒钟返回时分秒
     * @param second 秒
     * @return time
     */
    public static Time getTime(long second){
        if (second <= 0){
            return new Time(0, 0, 0);
        }
        if (second < 60){
            return new Time(0, 0, (int) second);
        }
        if (second < 3600){
            double minute = Math.floor(second / 60);
            return new Time(0, (int) minute, (int) (second % 60));
        }
        double hour = Math.floor(second / 3600);
        int i = (int) (second % 3600);
        double minute = Math.floor(i / 60);
        return new Time((int) hour, (int) minute, i % 60);
    }

    /**
     * 设置时长格式为00:00:00
     * @param l 毫秒
     * @return str
     */
    public static String formatDuration(long l){
        Time time = getTime(l);
        StringBuilder sb = new StringBuilder();
        if (time.getHour() < 10){
            sb.append("0").append(time.getHour());
        }else {
            sb.append(time.getHour());
        }
        sb.append(":");
        if (time.getMinute() < 10){
            sb.append("0").append(time.getMinute());
        }else {
            sb.append(time.getMinute());
        }
        sb.append(":");
        if (time.getSecond() < 10){
            sb.append("0").append(time.getSecond());
        }else {
            sb.append(time.getSecond());
        }
        return sb.toString();
    }
}
