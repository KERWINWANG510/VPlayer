package com.example.vplayer;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.vplayer.bean.Video;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VideoListActivity extends AppCompatActivity {

    public static final int PERMISSION_REQUEST_CODE = 1;
    MyHandler myHandler = new MyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);

        checkPermission();
    }

    /**
     * 检查是否有sdcard访问权限
     */
    private void checkPermission(){
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        int i = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (PackageManager.PERMISSION_GRANTED != i){    //未授权
            ActivityCompat.requestPermissions(VideoListActivity.this, perms, PERMISSION_REQUEST_CODE);
        }else {
            getVideoList();
        }
    }

    /**
     * 授权的回调方法
     * @param requestCode 请求码
     * @param permissions   权限列表
     * @param grantResults 授权结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){   //已授权
                    getVideoList();
                }else {
                    Toast.makeText(this, "请先授予存储读写权限！", Toast.LENGTH_SHORT).show();
                }
        }
    }

    /**
     * 获取视频列表
     */
    private void getVideoList(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Video> list = new ArrayList<>();
                String selection = MediaStore.Video.Media.RELATIVE_PATH + "=?";
                String[] args = {"Movies/"};
                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                Cursor cursor = getContentResolver().query(uri, null, selection, args, null, null);
                while (cursor.moveToNext()){
                    try {
                        Video video = new Video();
                        video.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)));
                        video.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)));
                        video.setDisplayName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)));
                        video.setSize(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)));
                        video.setAddDate(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED))));
                        video.setModifiedDate(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED))));
                        video.setMineType(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE)));
                        video.setDuration(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)));
                        video.setAlbum(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.ALBUM)));
                        video.setResolution(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.RESOLUTION)));
                        video.setWidth(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.WIDTH)));
                        video.setHeight(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.HEIGHT)));
                        video.setUri(ContentUris.withAppendedId(uri, video.getId()));
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                            video.setThumbnail(getContentResolver().loadThumbnail(video.getUri(), new Size(190, 100), null));
                        }
                        list.add(video);
                        /*Message msg = new Message();
                        msg.what = 1;
                        msg.obj = video;
                        myHandler.sendMessage(msg);*/
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    static class MyHandler extends Handler{
        WeakReference<VideoListActivity> myActivity;
        MyHandler(VideoListActivity activity){
            myActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            VideoListActivity videoListActivity = myActivity.get();
            switch (msg.what){
                case 1:
                    VideoView video = videoListActivity.findViewById(R.id.videoView);
                    Bitmap bitmap = (Bitmap) msg.obj;
                    video.setBackground(new BitmapDrawable(videoListActivity.getResources(), bitmap));
            }
        }
    }
}
