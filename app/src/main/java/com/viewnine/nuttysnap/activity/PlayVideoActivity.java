package com.viewnine.nuttysnap.activity;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.plus.PlusClient;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.viewnine.nuttysnap.R;
import com.viewnine.nuttysnap.application.SafeAppApplication;
import com.viewnine.nuttysnap.manager.VideoManager;
import com.viewnine.nuttysnap.model.VideoObject;
import com.viewnine.nuttysnap.ulti.AlertHelper;
import com.viewnine.nuttysnap.ulti.Constants;
import com.viewnine.nuttysnap.youtube.Auth;
import com.viewnine.nuttysnap.youtube.UploadService;
import com.viewnine.nuttysnap.youtube.ulti.VideoData;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by user on 4/25/15.
 */
public class PlayVideoActivity extends Activity implements View.OnClickListener, GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener{
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

    private static final int REQUEST_GOOGLE_PLAY_SERVICES = 0;
    private static final int REQUEST_GMS_ERROR_DIALOG = 1;
    private static final int REQUEST_ACCOUNT_PICKER = 2;
    private static final int REQUEST_AUTHORIZATION = 3;
    private static final int RESULT_PICK_IMAGE_CROP = 4;
    private static final int RESULT_VIDEO_CAP = 5;
    private static final int REQUEST_DIRECT_TAG = 6;
    final HttpTransport transport = AndroidHttp.newCompatibleTransport();
    final JsonFactory jsonFactory = new GsonFactory();
    GoogleAccountCredential credential;
    private String mChosenAccountName;
    private Uri mFileURI = null;
    private VideoData mVideoData;
    private UploadBroadcastReceiver broadcastReceiver;

    private PlusClient mPlusClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFacebook();
        initBundle();

        initViews();

        initVideo();

        initYoutube(savedInstanceState);

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

                startShareFacebook();
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

                addVideoToMediaStore(new File(videoLink));
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
    protected void onResume() {
        super.onResume();
        if (broadcastReceiver == null)
            broadcastReceiver = new UploadBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(
                Constants.REQUEST_AUTHORIZATION_INTENT);
        LocalBroadcastManager.getInstance(this).registerReceiver(
                broadcastReceiver, intentFilter);
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
//        startShareFacebook();
        startShareYoutube();

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


        //Youtube section
        switch (requestCode) {
            case REQUEST_GMS_ERROR_DIALOG:
                break;
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode == Activity.RESULT_OK) {
                    haveGooglePlayServices();
                } else {
                    checkGooglePlayServicesAvailable();
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode != Activity.RESULT_OK) {
                    chooseAccount();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == Activity.RESULT_OK && data != null
                        && data.getExtras() != null) {
                    String accountName = data.getExtras().getString(
                            AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        mChosenAccountName = accountName;
                        credential.setSelectedAccountName(accountName);
                        saveAccount();
                    }
                }
                break;

        }


    }

    private void startShareFacebook() {

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

            }
        }


    }

    private void startShareYoutube(){

        if(!mPlusClient.isConnected()){
            mPlusClient.connect();
        }else {
            android.accounts.Account[] accounts = credential.getAllAccounts();
            if(mChosenAccountName == null || mChosenAccountName.isEmpty()){
                chooseAccount();
            }else {
                uploadVideo();
            }
        }
    }


    //Youtube section

    private void initYoutube(Bundle savedInstanceState){

        mPlusClient = new PlusClient.Builder(this, this, this)
                .setScopes(Auth.SCOPES)
                .build();

        credential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(Auth.SCOPES));

        // set exponential backoff policy
        credential.setBackOff(new ExponentialBackOff());

        if (savedInstanceState != null) {
            mChosenAccountName = savedInstanceState.getString(Constants.ACCOUNT_KEY);
        } else {
            loadAccount();
        }

        credential.setSelectedAccountName(mChosenAccountName);
    }

    private void loadAccount() {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(this);
        mChosenAccountName = sp.getString(Constants.ACCOUNT_KEY, null);
    }

    private void saveAccount() {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(this);
        sp.edit().putString(Constants.ACCOUNT_KEY, mChosenAccountName).commit();
    }

    private void haveGooglePlayServices() {
        // check if there is already an account selected
        if (credential.getSelectedAccountName() == null) {
            // ask user to choose account
            chooseAccount();
        }
    }

    private void chooseAccount() {
        startActivityForResult(credential.newChooseAccountIntent(),
                REQUEST_ACCOUNT_PICKER);
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     */
    private boolean checkGooglePlayServicesAvailable() {
        final int connectionStatusCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
            return false;
        }
        return true;
    }

    public void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        runOnUiThread(new Runnable() {
            public void run() {
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                        connectionStatusCode, PlayVideoActivity.this,
                        REQUEST_GOOGLE_PLAY_SERVICES);
                dialog.show();
            }
        });
    }


    private class UploadBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.REQUEST_AUTHORIZATION_INTENT)) {
                Log.d(TAG, "Request auth received - executing the intent");
                Intent toRun = intent
                        .getParcelableExtra(Constants.REQUEST_AUTHORIZATION_INTENT_PARAM);
                startActivityForResult(toRun, REQUEST_AUTHORIZATION);
            }
        }
    }

    public void uploadVideo() {
        if (mChosenAccountName == null) {
            return;
        }
//        // if a video is picked or recorded.
        Uri mFileUri = Uri.fromFile(new File(videoObject.getVideoUrl()));
//        String videoPath = "/storage/emulated/0/SafeApp/Videos/SafeApp_1433268414036.mp4";
//        Uri mFileUri = Uri.fromFile(new File(videoPath));
        if (mFileUri != null) {
            Intent uploadIntent = new Intent(this, UploadService.class);
            uploadIntent.setData(mFileUri);
            uploadIntent.putExtra(Constants.ACCOUNT_KEY, mChosenAccountName);
            uploadIntent.putExtra("VIDEO_PATH", videoObject.getVideoUrl());
            startService(uploadIntent);
            Toast.makeText(this, R.string.youtube_upload_started,
                    Toast.LENGTH_LONG).show();
            // Go back to MainActivity after upload
//            finish();
        }


    }


    @Override
    public void onConnected(Bundle bundle) {
        mChosenAccountName = mPlusClient.getAccountName();
        Toast.makeText(this, "Connected youtube account: " + mPlusClient.getAccountName(), Toast.LENGTH_LONG ).show();
        uploadVideo();
//        mUploadsListFragment.updateInforAfterConnectedYoutube(mPlusClient);
    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            Toast.makeText(this,
                    R.string.connection_to_google_play_failed, Toast.LENGTH_SHORT)
                    .show();

            Log.e(TAG,
                    String.format(
                            "Connection to Play Services Failed, error: %d, reason: %s",
                            connectionResult.getErrorCode(),
                            connectionResult.toString()));
            try {
                connectionResult.startResolutionForResult(this, 0);
            } catch (IntentSender.SendIntentException e) {
                Log.e(TAG, e.toString(), e);
            }
        }
    }

    public Uri addVideoToMediaStore(File videoFile) {
        ContentValues values = new ContentValues(3);
        values.put(MediaStore.Video.Media.TITLE, "My video title");
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
        values.put(MediaStore.Video.Media.DATA, videoFile.getAbsolutePath());
        return getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
    }


}
