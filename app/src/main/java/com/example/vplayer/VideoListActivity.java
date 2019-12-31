package com.example.vplayer;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileFilter;

public class VideoListActivity extends AppCompatActivity {

    public static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);

        //checkPermission();
        getVideoList();
    }

    /**
     * 检查是否有sdcard访问权限
     */
    private void checkPermission(){
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        int i = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (PackageManager.PERMISSION_GRANTED != i){    //未授权
            ActivityCompat.requestPermissions(this, perms, PERMISSION_REQUEST_CODE);
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

        /*Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;


        MediaScannerConnection.scanFile(this, null, null, null);
        //Uri external = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver resolver = getContentResolver();
        //String selection = MediaStore.Video.Media.TITLE + "=?";
        //String[] args = new String[] {"Video"};
        String[] projection = new String[] {MediaStore.Video.Media._ID};
        //Cursor cursor = resolver.query(uri, projection, selection, args, null);
        Cursor cursor = resolver.query(uri, projection, null, null, null);
        Uri imageUri = null;
        if (cursor != null && cursor.moveToFirst()) {
            imageUri = ContentUris.withAppendedId(uri, cursor.getLong(0));
            cursor.close();
        }*/

        Cursor cursor = getApplicationContext().getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                null
        );

        //ContentResolver resolver = getContentResolver();
        //Cursor cursor = resolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()){
            String string = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
            Log.d("1111111111111111", string);
        }




        /*//File file = Environment.getExternalStorageDirectory();
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
        String path = file.getAbsolutePath();
        File file1 = new File(path);
        File[] files = file1.listFiles();
        //File[] files = file.listFiles();*/
        //Log.d("1111111111111111", path);
    }
}
