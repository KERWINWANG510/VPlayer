package com.example.vplayer;

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
        play();
    }

    /**
     * 播放视频
     */
    private void play(){
        Button playOrPause = findViewById(R.id.playOrPauseBtn);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "iconfont.ttf");
        playOrPause.setTypeface(typeface);
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
                TextView start = findViewById(R.id.startTimeView);
                start.setText("00:00:00");
            }
        });
    }

    private void setController(final Video video){
        TextView end = findViewById(R.id.endTimeView);
        end.setText(TimeUtils.formatDuration(video.getDuration() / 1000));
        SeekBar seek = findViewById(R.id.seekBar);
        seek.setMax((int) video.getDuration() / 1000);
        final VideoView view = findViewById(R.id.playVideoView);
        //定时更新进度条和时间
        new Thread(new Runnable() {
            @Override
            public void run() {
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
     * 更新进度条和当前播放时间
     */
    private void updateController(int position){
        SeekBar seek = findViewById(R.id.seekBar);
        seek.setProgress(position / 1000);
        TextView start = findViewById(R.id.startTimeView);
        start.setText(TimeUtils.formatDuration(position / 1000));
    }

    public void playOrPause(View view){
        VideoView videoView = findViewById(R.id.playVideoView);
        Button btn = findViewById(R.id.playOrPauseBtn);
        if (videoView.isPlaying()){
            videoView.pause();
            btn.setText(getString(R.string.play));
        }else {
            videoView.start();
            btn.setText(getString(R.string.pause));
            //设置播放时间和进度条，最好提取成公用方法
        }
    }

    static class MyHandler extends Handler{
        WeakReference<PlayVideoActivity> myActivity;
        public MyHandler(PlayVideoActivity activity){
            this.myActivity = new WeakReference<>(activity);
        }
        @Override
        public void handleMessage(@NonNull Message msg) {
            PlayVideoActivity playVideoActivity = myActivity.get();
            switch (msg.what){
                case 1:
                    removeMessages(1);
                    playVideoActivity.updateController((int) msg.obj);
                    break;
            }
        }
    }
}
