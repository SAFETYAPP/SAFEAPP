package com.viewnine.nuttysnap.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareVideo;
import com.facebook.share.model.ShareVideoContent;
import com.facebook.share.widget.ShareDialog;
import com.viewnine.nuttysnap.R;
import com.viewnine.nuttysnap.application.SafeAppApplication;
import com.viewnine.nuttysnap.manager.VideoManager;
import com.viewnine.nuttysnap.model.VideoObject;
import com.viewnine.nuttysnap.ulti.AlertHelper;
import com.viewnine.nuttysnap.ulti.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by user on 4/25/15.
 */
public class PlayVideoActivity extends Activity implements View.OnClickListener{
    private static final String TAG = PlayVideoActivity.class.getName();
    VideoView myVideoView;

    private int position = 0;
//    private String videoUrl;
    private Button btnShare;
    private Button btnBack;
    private VideoObject videoObject;
    private Button btnDeleteVideo;
    private RelativeLayout rlDelete;
    private RelativeLayout rlShare;
    private RelativeLayout rlBack;
    private RelativeLayout rlHeader;

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    private ShareDialog shareDialog;
    private String locationName = Constants.EMPTY_STRING;
    private TextView lblLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFacebook();
        initBundle();

        initViews();

        initVideo();

    }

    private void initFacebook(){
        FacebookSdk.sdkInitialize(getApplicationContext());

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Log.i(TAG, "Share dialog successful");
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {
                Log.i(TAG, "Share dialog error: " + e.toString());
            }
        });

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i(TAG, "Logged successful");

                startSharing();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {
                Log.i(TAG, "Logged error: " + e.toString());
            }
        });



        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken accessToken, AccessToken accessToken1) {
                Log.i(TAG, "onCurrentAccessTokenChanged() is called");
            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile profile, Profile profile1) {
                Log.i(TAG, "onCurrentProfileChanged() is called");
            }
        };
    }

    private void initBundle() {
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            videoObject = bundle.getParcelable(Constants.VIDEO_LINK);

            try{
                String videoLink = videoObject.getVideoUrl();
                String fileName = videoLink.substring(videoLink.lastIndexOf("/"));
                locationName = fileName.substring(fileName.indexOf("("));
                locationName = locationName.substring(1, locationName.lastIndexOf(".") - 1);
            }catch (IndexOutOfBoundsException e){
                e.printStackTrace();
            }

        }
    }

    private void initViews() {
        setContentView(R.layout.playing_video_view);

        rlHeader = (RelativeLayout) findViewById(R.id.relativelayout_header);
        myVideoView =(VideoView)findViewById(R.id.videoView1);
        btnBack = (Button) findViewById(R.id.button_back);
        rlBack = (RelativeLayout) findViewById(R.id.relativelayout_back);
        btnShare = (Button) findViewById(R.id.button_share);
        rlShare = (RelativeLayout) findViewById(R.id.relativelayout_share);
        btnDeleteVideo = (Button) findViewById(R.id.button_delete_video);
        rlDelete = (RelativeLayout) findViewById(R.id.relativelayout_delete);
        lblLocation = (TextView) findViewById(R.id.textview_location);
        lblLocation.setText(locationName);
        btnBack.setOnClickListener(this);
        btnShare.setOnClickListener(this);
        btnDeleteVideo.setOnClickListener(this);
        rlBack.setOnClickListener(this);
        rlShare.setOnClickListener(this);
        rlDelete.setOnClickListener(this);

        myVideoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                handleToggleVideoView();
                return false;
            }
        });
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
            case R.id.relativelayout_back:
            case R.id.button_back:
                exitThisScreen();
                break;
            case R.id.relativelayout_share:
            case R.id.button_share:
                handleClickOnShareButton();
                break;
            case R.id.relativelayout_delete:
            case R.id.button_delete_video:
                handleClickOnDeleteButton();
                break;
            default:
        }
    }

    private void handleToggleVideoView() {
        if(rlHeader.getVisibility() != View.VISIBLE){
            rlHeader.setVisibility(View.VISIBLE);
            mHandler.postDelayed(mRunnable, 3 * 1000);
        }else {
            rlHeader.setVisibility(View.GONE);
            mHandler.removeCallbacks(mRunnable);
        }

    }

    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            rlHeader.setVisibility(View.GONE);
        }
    };

    Handler mHandler = new Handler();

    private void exitThisScreen(){
        onBackPressed();
        overridePendingTransition(R.anim.stay, R.anim.push_down_out);
    }

    private void handleClickOnDeleteButton() {

//        DialogUlti.getInstance().showDeleteVideoConfirmationDialog(this, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                deleteVideo();
//            }
//        });

        AlertHelper.getInstance().showMessageAlert(this, getString(R.string.delete_confirmation), true, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteVideo();
            }
        });

//        EmailManager.getInstance().sendMail(Constants.MAIL_SUBJECT, Constants.MAIL_CONTENT, videoObject.getVideoUrl());
    }




    private void deleteVideo(){
        VideoManager.getInstance(this).deleteSpecificVideo(this, videoObject, new VideoManager.IDeleteVideoListener() {
            @Override
            public void deleteSpecificVideoSuccessful(VideoObject videoObject) {
                SafeAppApplication safeAppApplication = (SafeAppApplication) getApplication();
                safeAppApplication.getSafeAppDataObject().notifyVideoChanged(PlayVideoActivity.class.getName(), videoObject, Constants.DELETE_VIDEO_SIGNAL);
                exitThisScreen();
            }

            @Override
            public void fail() {
                AlertHelper.getInstance().showMessageAlert(PlayVideoActivity.this, getString(R.string.could_not_delete_this_video));
            }

            @Override
            public void deleteListVideoSuccessful(ArrayList<VideoObject> listVideoObject) {
            }
        });
    }

    private void handleClickOnShareButton() {
        startSharing();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        simpleFacebook.onActivityResult(this, requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);


    }

    private void startSharing() {

        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        if(accessToken == null || accessToken.isExpired()){
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));
        }else {

            if(ShareDialog.canShow(ShareVideoContent.class)){
                Uri videoUri = Uri.fromFile(new File(videoObject.getVideoUrl()));
                ShareVideo video = new ShareVideo.Builder()
                        .setLocalUrl(videoUri)
                        .build();
                ShareVideoContent videoContent = new ShareVideoContent.Builder().setVideo(video)
                        .setContentTitle("Title")
                        .setContentDescription("Content description").build();
                shareDialog.show(this, videoContent);
//                ShareApi.share(videoContent, new FacebookCallback<Sharer.Result>() {
//                    @Override
//                    public void onSuccess(Sharer.Result result) {
//
//                    }
//
//                    @Override
//                    public void onCancel() {
//
//                    }
//
//                    @Override
//                    public void onError(FacebookException e) {
//
//                    }
//                });



                //Share link
//                ShareLinkContent linkContent = new ShareLinkContent.Builder()
//                        .setContentTitle("Hello Facebook")
//                        .setContentDescription(
//                                "The 'Hello Facebook' sample  showcases simple Facebook integration")
//                        .setContentUrl(Uri.parse("http://developers.facebook.com/android"))
//                        .build();
//
//                shareDialog.show(linkContent);


                //Share image
//                Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.lockicon);
//                SharePhoto sharePhoto = new SharePhoto.Builder().setBitmap(image).setUserGenerated(true).build();
//                SharePhotoContent imageContent = new SharePhotoContent.Builder().addPhoto(sharePhoto).build();
//                shareDialog.show(imageContent);

            }
        }


    }

}
