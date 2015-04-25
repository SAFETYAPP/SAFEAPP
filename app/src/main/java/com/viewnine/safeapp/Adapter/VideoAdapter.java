package com.viewnine.safeapp.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.viewnine.safeapp.activity.R;
import com.viewnine.safeapp.customView.SquareImageView;
import com.viewnine.safeapp.manager.SwitchViewManager;
import com.viewnine.safeapp.model.VideoObject;
import com.viewnine.safeapp.ulti.Constants;

import java.util.ArrayList;

/**
 * Created by user on 4/24/15.
 */
public class VideoAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<VideoObject> listVideos;
    public VideoAdapter(Context context) {

        mContext = context;
    }

    public void setListVideos(ArrayList<VideoObject> listVideos) {
        if(listVideos == null){
            listVideos = new ArrayList<VideoObject>();
        }
        this.listVideos = listVideos;

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if(listVideos != null && listVideos.size() > 0){
            return listVideos.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return listVideos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.item_video, null);
            holder.videoImageView = (SquareImageView) convertView.findViewById(R.id.image_video);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        setupItemPost(holder, listVideos.get(position), position);

        return convertView;
    }

    private void setupItemPost(ViewHolder holder, final VideoObject videoObject, int position) {
        String videoLink = Constants.PREFIX_LOCAL_FILE_URL + videoObject.getImageLink();
//        ImageLoader.getInstance().displayImage(videoLink, holder.videoImageView);

        ImageLoader.getInstance().displayImage(videoLink, holder.videoImageView, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });

        holder.videoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SwitchViewManager.getInstance().gotoVideoScreen(mContext, videoObject.getVideoUrl());
            }
        });
    }

    private class ViewHolder{
        SquareImageView videoImageView;
    }

}
