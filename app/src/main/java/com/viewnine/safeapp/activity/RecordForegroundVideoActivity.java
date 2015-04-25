package com.viewnine.safeapp.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.viewnine.safeapp.application.SafeAppApplication;
import com.viewnine.safeapp.manager.SwitchViewManager;
import com.viewnine.safeapp.ulti.Constants;
import com.viewnine.safeapp.ulti.LogUtils;
import com.viewnine.safeapp.ulti.Ulti;
import com.viewnine.safeapp.view.CameraPreview;

import java.util.TimerTask;

/**
 * Created by user on 4/19/15.
 */
public class RecordForegroundVideoActivity extends ParentActivity {

    private RelativeLayout rlCameraTakePicture;
    private int heightSizeOfTopView;
    private int[] sizeOfScreen = new int[2];
    private int mCurrentFlashMode = CameraPreview.FLASH_AUTO;
    private int mCurrentCameraID = CameraPreview.DEFAULT_CAMERA;
    private CameraPreview mPreviewTakePicture;
    private Button btnFlashMode;
    private Button btnSwitchCamera;
    private Button btnTakeOrRecordCamera;
    private View btnGallery;


    private static final String TAG = RecordForegroundVideoActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        finish();

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

    }

    private void setupViews() {
        addChidlView(R.layout.record_foreground_video_view);

//        VideoDBAdapter videoDBAdapter = new VideoDBAdapter(this);
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



    }

    private void getWidthHeightScreen(){
        sizeOfScreen = Ulti.getScreenSize(this);
        heightSizeOfTopView = Ulti.getDimenValueFromDimenXML(this, R.dimen.camera_high_size);
        heightSizeOfTopView = Ulti.convertDensityToPixel(this, heightSizeOfTopView);
    }

    private void initCameraView(){
        //set width/height for camera view
        mPreviewTakePicture = new CameraPreview(this, mCurrentCameraID, CameraPreview.LayoutMode.FitToParent, mCurrentFlashMode);
        handleFlashButtonUI();
        showHideFlashButton();

        RelativeLayout.LayoutParams previewLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        previewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP|RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        previewLayoutParams.height = sizeOfScreen[1];
        previewLayoutParams.width = sizeOfScreen[0];
        rlCameraTakePicture.removeAllViews();
        rlCameraTakePicture.addView(mPreviewTakePicture, 0, previewLayoutParams);
        mPreviewTakePicture.surfaceChanged(null, 0, sizeOfScreen[0], sizeOfScreen[1]);
    }

    private void handleFlashButtonUI(){
        if(mPreviewTakePicture != null){
            mCurrentFlashMode = mPreviewTakePicture.getCurrentFlashMode();
            switch (mCurrentFlashMode) {
                case CameraPreview.FLASH_AUTO:
//                    btnFlashMode.setBackgroundResource(R.drawable.flash_auto_white);
                    btnFlashMode.setText("AUTO");
                    break;
                case CameraPreview.FLASH_ON:
//                    btnFlashMode.setBackgroundResource(R.drawable.flash_on_white);
                    btnFlashMode.setText("ON");
                    break;
                case CameraPreview.FLASH_OFF:
//                    btnFlashMode.setBackgroundResource(R.drawable.flash_off_white);
                    btnFlashMode.setText("OFF");
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
                takePicture();
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
            default:
                break;
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
    private void takePicture() {
//        if(mPreviewTakePicture != null){
//            mPreviewTakePicture.takePicture(null);
//        }

        try {
            if(mPreviewTakePicture != null){
                if(recording){
                    recording = false;
                    LogUtils.logI(TAG, "Stop Recording");
//                    stopTimerTask();
                   mPreviewTakePicture.releaseMediaRecorder();

                }else {
                    recording = true;

                    mPreviewTakePicture.startRecording();
//                    handleRecordingInForeground();
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
                            Log.d(TAG, "Start recording");
                            mPreviewTakePicture.startRecording();

                        }

//                        Log.d(TAG, "Start timer");
                    }
                });

            }
        };

        SafeAppApplication.getTimer().schedule(timerTask, Constants.TIME_DELAY, Constants.TIME_TO_RECORDING);


    }


}
