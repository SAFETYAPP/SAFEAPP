package com.viewnine.nuttysnap.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.viewnine.nuttysnap.R;
import com.viewnine.nuttysnap.application.SafeAppApplication;
import com.viewnine.nuttysnap.manager.SwitchViewManager;
import com.viewnine.nuttysnap.ulti.AlertHelper;
import com.viewnine.nuttysnap.ulti.Constants;
import com.viewnine.nuttysnap.ulti.LogUtils;
import com.viewnine.nuttysnap.ulti.Ulti;
import com.viewnine.nuttysnap.view.CameraPreview;
import com.viewnine.nuttysnap.view.CircleProgressView;

import java.util.TimerTask;

/**
 * Created by user on 4/19/15.
 */
public class RecordForegroundVideoActivityV2 extends ParentActivity implements CameraPreview.IRecordListener, CircleProgressView.OnProgressListener {

    private RelativeLayout rlCameraTakePicture;
    private int heightSizeOfTopView;
    private int[] sizeOfScreen = new int[2];
    private int mCurrentFlashMode = CameraPreview.FLASH_AUTO;
    private int mCurrentCameraID = CameraPreview.DEFAULT_CAMERA;
    private CameraPreview mPreviewTakePicture;
    private Button btnFlashMode;
    private Button btnSwitchCamera;
    private Button btnTakeOrRecordCamera;
    private Button btnAudio;
    private boolean enableRecordAudio = true;
    private View btnGallery;
    private static final String TAG = RecordForegroundVideoActivityV2.class.getName();
    private TextView lblRecordTime;
    CircleProgressView circleProgress;
    private int MAX_TIME_RECORD_VIDEO = 10 * 1000;

    CountDownTimer countTime;
    int second = -1;

    int currentZoomLevel;
    float beginZoom = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        stopTimerTask();
        stopRecordingInBackgroundThread();

        getWidthHeightScreen();
        setupViews();

//        startService(new Intent(this, LockScreenService.class));


    }

    @Override
    protected void onResume() {

        super.onResume();
        initCameraView();

    }

    @Override
    protected void onPause() {

        super.onPause();

        recording = false;
        stopTimerTask();
        mPreviewTakePicture.releaseMediaRecorder();
        releaseCameraView();
//        finish();

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

    }

    private void setupViews() {
        addChidlView(R.layout.record_foreground_video_view_v2);

//        VideoDBHelper videoDBAdapter = new VideoDBHelper(this);
//        List<VideoObject> listVideos = videoDBAdapter.getAllVideos();

        showHideHeader(false);

        rlCameraTakePicture = (RelativeLayout) findViewById(R.id.relativelayout_camera);
        btnFlashMode = (Button) findViewById(R.id.button_flash_mode);
        btnFlashMode.setOnClickListener(this);
        btnSwitchCamera = (Button) findViewById(R.id.button_switch_camera);
        btnSwitchCamera.setOnClickListener(this);

        btnTakeOrRecordCamera = (Button) findViewById(R.id.button_take_record_camera);
        btnTakeOrRecordCamera.setOnClickListener(this);
        btnGallery = (Button) findViewById(R.id.button_gallery);
        btnGallery.setOnClickListener(this);

        btnAudio = (Button) findViewById(R.id.button_audio_mode);
        btnAudio.setOnClickListener(this);
        circleProgress = (CircleProgressView) findViewById(R.id.circleView);
        circleProgress.setMaxTimeToFillFullCircle(MAX_TIME_RECORD_VIDEO);
        circleProgress.setOnCircleTouchListener(this);

        lblRecordTime = (TextView) findViewById(R.id.textview_time_recorder);


        countTime = new CountDownTimer( Long.MAX_VALUE , 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

                second++;
                lblRecordTime.setText(String.format("%02d:%02d:%02d",
                        second / 3600, (second % 3600) / 60, second % 60));

            }

            @Override
            public void onFinish() {
            }
        };


    }

    private void getWidthHeightScreen(){
        sizeOfScreen = Ulti.getScreenSize(this);
        heightSizeOfTopView = Ulti.getDimenValueFromDimenXML(this, R.dimen.camera_high_size);
        heightSizeOfTopView = Ulti.convertDensityToPixel(this, heightSizeOfTopView);
    }

    private void initCameraView(){
        //set width/height for camera view
        mPreviewTakePicture = new CameraPreview(this, mCurrentCameraID, CameraPreview.LayoutMode.FitToParent, mCurrentFlashMode);
        mPreviewTakePicture.setRecordListener(this);
        handleFlashButtonUI();
        showHideFlashButton();

        RelativeLayout.LayoutParams previewLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        previewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP|RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        previewLayoutParams.height = sizeOfScreen[1];
        previewLayoutParams.width = sizeOfScreen[0];
        rlCameraTakePicture.removeAllViews();
        rlCameraTakePicture.addView(mPreviewTakePicture, 0, previewLayoutParams);
        mPreviewTakePicture.surfaceChanged(null, 0, sizeOfScreen[0], sizeOfScreen[1]);

        if(Constants.ENABLE_ZOOM_FEATURE){
            final ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.OnScaleGestureListener() {
                @Override
                public boolean onScale(ScaleGestureDetector detector) {
                    float mScaleFactor = detector.getCurrentSpan();
                    if(beginZoom < mScaleFactor && currentZoomLevel < mPreviewTakePicture.getMaxZoomLevel()){
                        currentZoomLevel += 2;
                        beginZoom = mScaleFactor;
                        LogUtils.logD(TAG, "Zoom In " + currentZoomLevel);
                        mPreviewTakePicture.zoomLevel(currentZoomLevel);
                    }else if (beginZoom > mScaleFactor && currentZoomLevel > 0) {
                        currentZoomLevel -= 2;
                        beginZoom = mScaleFactor;
                        LogUtils.logD(TAG, "Zoom Out " + currentZoomLevel);
                        mPreviewTakePicture.zoomLevel(currentZoomLevel);
                    }
                    return true;
                }

                @Override
                public boolean onScaleBegin(ScaleGestureDetector detector) {
                    return true;
                }

                @Override
                public void onScaleEnd(ScaleGestureDetector detector) {

                }
            });

            mPreviewTakePicture.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    scaleGestureDetector.onTouchEvent(event);
                    return true;
                }
            });
        }

    }

    private void handleFlashButtonUI(){
        if(mPreviewTakePicture != null){
            mCurrentFlashMode = mPreviewTakePicture.getCurrentFlashMode();
            switch (mCurrentFlashMode) {
                case CameraPreview.FLASH_AUTO:
                    btnFlashMode.setBackgroundResource(R.drawable.flash_button_auto);

                    break;
                case CameraPreview.FLASH_ON:
                    btnFlashMode.setBackgroundResource(R.drawable.flash_button_on);

                    break;
                case CameraPreview.FLASH_OFF:
                    btnFlashMode.setBackgroundResource(R.drawable.flash_button_off);

                    break;

                default:
                    break;
            }
        }
    }

    private void showHideFlashButton(){
        if(mCurrentCameraID == CameraPreview.DEFAULT_CAMERA){
            btnFlashMode.setVisibility(View.VISIBLE);
//            btnSwitchCamera.setBackgroundResource(R.drawable.camera_switch_white);
        }else {
            btnFlashMode.setVisibility(View.GONE);
//            btnSwitchCamera.setBackgroundResource(R.drawable.camera_switch_white);
        }
    }

    private void releaseCameraView(){
        mPreviewTakePicture.stop();
        rlCameraTakePicture.removeView(mPreviewTakePicture); // This is necessary.
        mPreviewTakePicture = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_take_record_camera:
                handleRecordVideo();
                break;

            case R.id.button_flash_mode:
                handleClickFlashModeButton();
                break;
            case R.id.button_switch_camera:
                handleClickSwitchCameraButton();
                break;
            case R.id.button_gallery:
                handleGalleryButton();
                break;
            case R.id.button_audio_mode:
                handleClickOnAudioButton();
                break;
            default:
                break;
        }

    }

    private void handleClickOnAudioButton() {
        if(enableRecordAudio){
            enableRecordAudio = false;
            btnAudio.setBackgroundResource(R.drawable.audio_button_off);
        }else {
            enableRecordAudio = true;
            btnAudio.setBackgroundResource(R.drawable.audio_button_on);
        }
    }

    private void handleGalleryButton() {
        SwitchViewManager.getInstance().gotoHistoryScreen(this);
    }

    private void handleClickFlashModeButton() {
        if(mPreviewTakePicture != null){
            mPreviewTakePicture.toggleSplashMode();
            handleFlashButtonUI();
        }
    }


    boolean recording = false;
    private void handleRecordVideo() {

        try {
            if(mPreviewTakePicture != null){
                if(recording){

                    LogUtils.logI(TAG, "Stop Recording");
                    mPreviewTakePicture.releaseMediaRecorder();

                    SwitchViewManager.getInstance().gotoHistoryScreen(this);

                }else {


                    if(Ulti.checkSDCardFreeSpaceToStartRecording()){
                        LogUtils.logI(TAG, "Start Recording");
//                    mPreviewTakePicture.startRecording(enableRecordAudio);
                        handleRecordingInForeground();
                    }else {
                        AlertHelper.getInstance().showMessageAlert(this, getResources().getString(R.string.storage_full));
                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void handleClickSwitchCameraButton(){
        if(mPreviewTakePicture != null){
            mCurrentCameraID = mPreviewTakePicture.toggleSwitchCamera();
            recording = false;
            stopTimerTask();
            mPreviewTakePicture.releaseMediaRecorder();
            releaseCameraView();
            initCameraView();
            showHideFlashButton();
        }
    }


    protected void handleRecordingInForeground() {
        btnTakeOrRecordCamera.setEnabled(false);
        TimerTask timerTask = new TimerTask() {

            @Override
            public void run() {
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        if(mPreviewTakePicture != null){
                            Log.d(TAG, "Stop recording");
                            mPreviewTakePicture.releaseMediaRecorder();

                            for (int i = 0; i < 100000; i++) {

                            }
                            Log.d(TAG, "Start recording + " + timeToRecord);
                            mPreviewTakePicture.startRecording(timeToRecord, enableRecordAudio);

                        }
                    }
                });

            }
        };

        SafeAppApplication.getTimer().schedule(timerTask, Constants.TIME_DELAY, timeToRecord + Constants.TIME_TO_PENDING);


    }


    @Override
    public void notifyStartRecording() {
        btnTakeOrRecordCamera.setEnabled(true);
        countTime.start();
        btnTakeOrRecordCamera.setBackgroundResource(R.drawable.record_button_active_state);
        recording = true;
        btnGallery.setVisibility(View.GONE);
        btnAudio.setVisibility(View.GONE);
        btnSwitchCamera.setVisibility(View.GONE);


    }

    @Override
    public void notifyStopRecording() {
        btnTakeOrRecordCamera.setEnabled(true);
        second = -1;
        countTime.cancel();
        btnTakeOrRecordCamera.setBackgroundResource(R.drawable.record_button_normal_state);
        btnGallery.setVisibility(View.VISIBLE);
        btnAudio.setVisibility(View.VISIBLE);
        btnSwitchCamera.setVisibility(View.VISIBLE);
        recording = false;

    }


    @Override
    public void singleTouch() {
        Log.d(MainActivity.class.getSimpleName(), "Start Take picture");
    }

    @Override
    public void startLongTouch() {
        Log.d(MainActivity.class.getSimpleName(), "Start recording");
//        if(Ulti.checkSDCardFreeSpaceToStartRecording()){
//            mPreviewTakePicture.startRecording(timeToRecord, enableRecordAudio);
//        }else {
//            AlertHelper.getInstance().showMessageAlert(this, getResources().getString(R.string.storage_full));
//        }

    }

    @Override
    public void stopLongTouch() {
        Log.d(MainActivity.class.getSimpleName(), "Stop recording");
//        mPreviewTakePicture.releaseMediaRecorder();
    }

    @Override
    public void progressIsReset() {

    }
}
