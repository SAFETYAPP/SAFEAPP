package com.viewnine.safeapp.activity;

import android.os.Bundle;

import com.viewnine.safeapp.database.VideoDBAdapter;
import com.viewnine.safeapp.model.VideoObject;

import java.util.List;

/**
 * Created by user on 4/19/15.
 */
public class RecordForegroundVideoActivity extends ParentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupViews();

//        startService(new Intent(this, LockScreenService.class));
    }

    private void setupViews() {
        addChidlView(R.layout.record_foreground_video_view);

        VideoDBAdapter videoDBAdapter = new VideoDBAdapter(this);
        List<VideoObject> listVideos = videoDBAdapter.getAllVideos();

        showHideHeader(false);


    }
}
