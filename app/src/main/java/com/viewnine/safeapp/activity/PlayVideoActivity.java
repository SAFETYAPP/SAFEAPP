package com.viewnine.safeapp.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import com.viewnine.safeapp.application.SafeAppApplication;
import com.viewnine.safeapp.manager.VideoManager;
import com.viewnine.safeapp.model.VideoObject;
import com.viewnine.safeapp.ulti.AlertHelper;
import com.viewnine.safeapp.ulti.Constants;

/**
 * Created by user on 4/25/15.
 */
public class PlayVideoActivity extends Activity implements View.OnClickListener{
    VideoView myVideoView;

    private int position = 0;
//    private String videoUrl;
    private Button btnShare;
    private Button btnBack;
    private VideoObject videoObject;
    private Button btnDeleteVideo;

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
            videoObject = bundle.getParcelable(Constants.VIDEO_LINK);
        }
    }

    private void initViews() {
        setContentView(R.layout.playing_video_view);

        myVideoView =(VideoView)findViewById(R.id.videoView1);
        btnBack = (Button) findViewById(R.id.button_back);
        btnShare = (Button) findViewById(R.id.button_share);
        btnDeleteVideo = (Button) findViewById(R.id.button_delete_video);
        btnBack.setOnClickListener(this);
        btnShare.setOnClickListener(this);
        btnDeleteVideo.setOnClickListener(this);
    }

    private void initVideo(){
        MediaController mediaControls= new MediaController(this);

        try {
            //set the media controller in the VideoView
            myVideoView.setMediaController(mediaControls);

            //set the uri of the video to be played
            Uri uri=Uri.parse(videoObject.getVideoUrl());
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
                exitThisScreen();
                break;
            case R.id.button_share:
                handleClickOnShareButton();
                break;
            case R.id.button_delete_video:
                handleClickOnDeleteButton();
                break;
            default:
        }
    }

    private void exitThisScreen(){
        onBackPressed();
        overridePendingTransition(R.anim.stay, R.anim.push_down_out);
    }

    private void handleClickOnDeleteButton() {
        AlertHelper.getInstance().showMessageAlert(this, getString(R.string.delete_video_confirmation), true, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteVideo();
            }
        });
    }



    private void deleteVideo(){
        VideoManager.getInstance(this).deleteVideo(this, videoObject, new VideoManager.IDeleteVideoListener() {
            @Override
            public void successful(VideoObject videoObject) {
                SafeAppApplication safeAppApplication = (SafeAppApplication) getApplication();
                safeAppApplication.getSafeAppDataObject().notifyVideoChanged(PlayVideoActivity.class.getName(), videoObject, Constants.DELETE_VIDEO_SIGNAL);
                exitThisScreen();
            }

            @Override
            public void fail() {
                AlertHelper.getInstance().showMessageAlert(PlayVideoActivity.this, getString(R.string.could_not_delete_this_video));
            }
        });
    }

    private void handleClickOnShareButton() {

    }
}
