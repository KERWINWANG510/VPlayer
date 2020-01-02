package com.example.vplayer.bean;

import android.graphics.Bitmap;
import android.net.Uri;

import java.util.Date;

import lombok.Data;

/**
 * 视频实体类
 */
@Data
public class Video {
    private int id;             //id
    private String title;       //名称
    private String displayName; //显示名称
    private long size;          //大小
    private Date addDate;       //添加日期
    private Date modifiedDate;  //修改日期
    private String mineType;    //文件类型
    private long duration;      //时长
    private String album;       //专辑
    private String resolution;  //分辨率
    private int width;          //宽
    private int height;         //高
    private Uri uri;            //uri
    private Bitmap thumbnail;   //缩略图
}
