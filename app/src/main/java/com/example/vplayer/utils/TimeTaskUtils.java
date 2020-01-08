package com.example.vplayer.utils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 定时任务工具类
 */
public class TimeTaskUtils {
    private Timer timer;
    private TimerTask task;
    private long time;  //间隔时间，单位ms
    private long delay; //多少ms后开始执行

    public TimeTaskUtils(long delay, long time, TimerTask task){
        this.task = task;
        this.time = time;
        this.delay = delay;
        if (timer == null){
            timer = new Timer();
        }
    }

    public void start(){
        //每隔time时间执行一次
        timer.schedule(task, delay, time);
    }

    public void stop(){
        if (timer != null){
            timer.cancel();
            if (task != null){
                task.cancel();
            }
        }
    }
}
