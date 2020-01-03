package com.example.vplayer;

import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Size;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.vplayer.adapter.VideoListAdatper;
import com.example.vplayer.bean.Video;
import com.google.gson.Gson;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VideoListActivity extends AppCompatActivity {

    public static final int PERMISSION_REQUEST_CODE = 1;
    MyHandler myHandler = new MyHandler(this);
    public static final String INTENT_PARAM_VIDEO = "video";

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
                        video.setUri(ContentUris.withAppendedId(uri, video.getId()).toString());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            video.setThumbnail(getContentResolver().loadThumbnail(Uri.parse(video.getUri()), new Size(190, 100), null));
                        }
                        list.add(video);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Message msg = new Message();
                msg.what = 1;
                msg.obj = list;
                myHandler.sendMessage(msg);
            }
        }).start();
    }

    /**
     * 显示视频列表
     * @param list 视频列表
     */
    private void showVideoList(List<Video> list){
        GridView videoGrid = findViewById(R.id.videoGrid);
        VideoListAdatper adapter = new VideoListAdatper(this, R.layout.video_list_item, list);
        videoGrid.setAdapter(adapter);
        videoGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(VideoListActivity.this, PlayVideoActivity.class);
                VideoListAdatper.ViewHolder holder = (VideoListAdatper.ViewHolder) view.getTag();
                String s = new Gson().toJson(holder.getVideo());
                intent.putExtra(INTENT_PARAM_VIDEO, s);
                startActivity(intent);
            }
        });
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
                    removeMessages(1);
                    List<Video> list = (List<Video>) msg.obj;
                    videoListActivity.showVideoList(list);
                    break;
            }
        }
    }
}
