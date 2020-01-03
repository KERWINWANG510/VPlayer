package com.example.vplayer.bean;

import lombok.Data;

/**
 * 时间实体类
 */
@Data
public class Time {
    private int hour;
    private int minute;
    private int second;

    public Time(int hour, int minute, int second){
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }
}
