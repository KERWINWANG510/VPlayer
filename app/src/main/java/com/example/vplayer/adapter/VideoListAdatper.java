package com.example.vplayer.adapter;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.vplayer.R;
import com.example.vplayer.bean.Video;
import com.example.vplayer.utils.TimeUtils;

import java.util.List;

/**
 * 视频列表adapter
 */
public class VideoListAdatper extends BaseAdapter {
    private Context myContext;
    private int resourceId;
    private List<Video> list;
    public VideoListAdatper(Context c, int resourceId, List<Video> list){
        this.myContext = c;
        this.resourceId = resourceId;
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        Video video = (Video) getItem(position);
        if (null == convertView){
            convertView = LayoutInflater.from(myContext).inflate(resourceId, null);
            holder = new ViewHolder();
            holder.video = video;
            holder.videoView = convertView.findViewById(R.id.videoView);
            holder.titleView = convertView.findViewById(R.id.titleView);
            holder.durationView = convertView.findViewById(R.id.durationView);
            holder.resolutionView = convertView.findViewById(R.id.resolutionView);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.videoView.setBackground(new BitmapDrawable(myContext.getResources(), video.getThumbnail()));
        holder.titleView.setText(setTitle(video.getTitle()));
        holder.durationView.setText(TimeUtils.formatDuration(video.getDuration() / 1000));
        holder.resolutionView.setText(video.getResolution());
        return convertView;
    }

    /**
     * 设置标题
     * @param title 标题
     * @return 处理后的标题
     */
    private String setTitle(String title){
        if (title.length() > 15){
            return title.substring(0, 14) + "...";
        }
        return title;
    }

    public class ViewHolder{
        Video video;            //视频
        View videoView;         //视频封面
        TextView titleView;     //视频名称
        TextView durationView;  //时长
        TextView resolutionView;//分辨率

        public Video getVideo() {
            return video;
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
