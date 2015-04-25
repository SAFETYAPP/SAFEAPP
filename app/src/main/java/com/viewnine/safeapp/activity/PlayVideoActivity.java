package com.viewnine.safeapp.activity;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import com.viewnine.safeapp.ulti.Constants;

/**
 * Created by user on 4/25/15.
 */
public class PlayVideoActivity extends Activity implements View.OnClickListener{
    VideoView myVideoView;

    private int position = 0;
    private String videoUrl;
    private Button btnShare;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initBundle();

        initViews();

        initVideo();

    }

    private void initBundle() {
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            videoUrl = bundle.getString(Constants.VIDEO_LINK);
        }
    }

    private void initViews() {
        setContentView(R.layout.playing_video_view);

        myVideoView =(VideoView)findViewById(R.id.videoView1);
        btnBack = (Button) findViewById(R.id.button_back);
        btnShare = (Button) findViewById(R.id.button_share);
        btnBack.setOnClickListener(this);
        btnShare.setOnClickListener(this);
    }

    private void initVideo(){
        MediaController mediaControls= new MediaController(this);

        try {
            //set the media controller in the VideoView
            myVideoView.setMediaController(mediaControls);

            //set the uri of the video to be played
            Uri uri=Uri.parse(videoUrl);
            myVideoView.setVideoURI(uri);

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }


        myVideoView.requestFocus();
        //we also set an setOnPreparedListener in order to know when the video file is ready for playback
        myVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            public void onPrepared(MediaPlayer mediaPlayer) {
                //if we have a position on savedInstanceState, the video playback should start from here
                myVideoView.seekTo(position);
                if (position == 0) {
                    myVideoView.start();
                } else {
                    //if we come from a resumed activity, video playback will be paused
                    myVideoView.pause();
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        //we use onSaveInstanceState in order to store the video playback position for orientation change
        savedInstanceState.putInt("Position", myVideoView.getCurrentPosition());
        myVideoView.pause();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //we use onRestoreInstanceState in order to play the video playback from the stored position
        position = savedInstanceState.getInt("Position");
        myVideoView.seekTo(position);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_back:
                finish();
                break;
            case R.id.button_share:
                handleClickOnShareButton();
                break;
            default:
        }
    }

    private void handleClickOnShareButton() {

    }
}
