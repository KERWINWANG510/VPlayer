package com.example.vplayer;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vplayer.bean.Video;
import com.example.vplayer.utils.TimeUtils;
import com.google.gson.Gson;

import java.lang.ref.WeakReference;

/**
 * 播放视频
 */
public class PlayVideoActivity extends AppCompatActivity {

    MyHandler myHandler = new MyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
        init();
    }

    /**
     * 初始化
     */
    private void init(){
        Button playOrPause = findViewById(R.id.playOrPauseBtn);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "iconfont.ttf");
        playOrPause.setTypeface(typeface);
        Button fullScreen = findViewById(R.id.fullScreenBtn);
        fullScreen.setTypeface(typeface);
        this.seekBarListener();
        //播放视频
        play();
    }

    /**
     * SeekBar监听
     */
    private void seekBarListener(){
        SeekBar seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                TextView start = PlayVideoActivity.this.findViewById(R.id.startTimeView);
                start.setText(TimeUtils.formatDuration(progress / 1000));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                TextView start = PlayVideoActivity.this.findViewById(R.id.startTimeView);
                start.setText(TimeUtils.formatDuration(progress / 1000));
                VideoView view = PlayVideoActivity.this.findViewById(R.id.playVideoView);
                view.seekTo(progress);
            }
        });
    }

    /**
     * 播放视频
     */
    private void play(){
        String str = getIntent().getStringExtra(VideoListActivity.INTENT_PARAM_VIDEO);
        final Video video = new Gson().fromJson(str, Video.class);
        VideoView view = findViewById(R.id.playVideoView);
        view.setVideoURI(Uri.parse(video.getUri()));
        TextView titleVIew = findViewById(R.id.titleView);
        titleVIew.setText(video.getTitle());
        //监听视频是否准备完成
        view.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                setController(video);
            }
        });
        //监听视频是否播放完成
        view.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                SeekBar seek = findViewById(R.id.seekBar);
                seek.setProgress(0);
                Button btn = findViewById(R.id.playOrPauseBtn);
                btn.setText(getString(R.string.play));
            }
        });
    }

    private void setController(final Video video){
        TextView end = findViewById(R.id.endTimeView);
        end.setText(TimeUtils.formatDuration(video.getDuration() / 1000));
        SeekBar seek = findViewById(R.id.seekBar);
        seek.setMax((int) video.getDuration());
        //定时更新进度条和时间
        this.updateController();
    }

    /**
     * 更新进度条和当前播放时间
     */
    private void updateController(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                VideoView view = findViewById(R.id.playVideoView);
                while (view.isPlaying()){
                    int position = view.getCurrentPosition();
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = position;
                    myHandler.sendMessage(msg);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * 播放暂停按钮
     * @param view btn
     */
    public void playOrPause(View view){
        final VideoView videoView = findViewById(R.id.playVideoView);
        Button btn = findViewById(R.id.playOrPauseBtn);
        if (videoView.isPlaying()){
            videoView.pause();
            btn.setText(getString(R.string.play));
        }else {
            videoView.start();
            btn.setText(getString(R.string.pause));
            //定时更新进度条和时间
            this.updateController();
        }
    }

    /**
     * 全屏按钮
     * @param view btn
     */
    public void fullScreen(View view){
        //竖屏
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            //设置跟随传感器横屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            this.showOrHide(false); //横屏时隐藏按钮
        }else { //横屏
            //设置竖屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

    }

    /**
     * 显示或隐藏控件
     * @param flag true：显示  false：隐藏
     */
    private void showOrHide(boolean flag){
        TextView title = findViewById(R.id.titleView);
        View controller = findViewById(R.id.controllerView);
        if (flag){  //显示
            controller.setVisibility(View.VISIBLE);
            title.setVisibility(View.VISIBLE);
        }else {     //隐藏
            controller.setVisibility(View.GONE);
            title.setVisibility(View.GONE);
        }
    }

    /**
     * 点击屏幕显示或隐藏控件
     * @param view VideoView
     */
    public void clickVideoView(View view){
        View controller = findViewById(R.id.controllerView);
        if (controller.getVisibility() == View.GONE){    //已隐藏则显示
            this.showOrHide(true);
        }else { //已显示则隐藏
            this.showOrHide(false);
        }
    }

    static class MyHandler extends Handler{
        WeakReference<PlayVideoActivity> myActivity;
        MyHandler(PlayVideoActivity activity){
            this.myActivity = new WeakReference<>(activity);
        }
        @Override
        public void handleMessage(@NonNull Message msg) {
            PlayVideoActivity playVideoActivity = myActivity.get();
            switch (msg.what){
                case 1:
                    removeMessages(1);
                    int position = (int) msg.obj;
                    //更新进度条和时间
                    SeekBar seek = playVideoActivity.findViewById(R.id.seekBar);
                    seek.setProgress(position);
                    break;
            }
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
