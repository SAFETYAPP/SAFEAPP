package com.viewnine.safeapp.view;

import android.app.Activity;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.viewnine.safeapp.manager.VideoQueueManager;
import com.viewnine.safeapp.model.VideoObject;
import com.viewnine.safeapp.ulti.Constants;
import com.viewnine.safeapp.ulti.LogUtils;
import com.viewnine.safeapp.ulti.Ulti;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

/**
 * This class assumes the parent layout is RelativeLayout.LayoutParams.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback, PreviewCallback {
    private static boolean DEBUGGING = true;
    private static final String LOG_TAG = CameraPreview.class.getName();
    private static final String CAMERA_PARAM_ORIENTATION = "orientation";
    private static final String CAMERA_PARAM_LANDSCAPE = "landscape";
    private static final String CAMERA_PARAM_PORTRAIT = "portrait";
    protected Activity mActivity;
    private SurfaceHolder surfaceHolder;
    protected Camera camera;
    protected List<Size> mPreviewSizeList;
    protected List<Size> mPictureSizeList;
    protected Camera.Size mPreviewSize;
    protected Camera.Size mPictureSize;
    private int mSurfaceChangedCallDepth = 0;
    private int mCameraId = 0;
    private LayoutMode mLayoutMode;
    private int mCenterPosX = -1;
    private int mCenterPosY;
    private String TAG = CameraPreview.class.getName();
    public static final int FLASH_AUTO = 0;
    public static final int FLASH_ON = 1;
    public static final int FLASH_OFF = 2;
    private int mCurrentFlashMode = FLASH_AUTO;
    public static final int DEFAULT_CAMERA = 0;
    public static final int FRONT_CAMERA = 1;

    private String fileName = Constants.EMPTY_STRING;
    private VideoObject videoObject;

    public MediaRecorder mediaRecorder = new MediaRecorder();

    public int getCameraId() {
        return mCameraId;
    }

    public void setCameraId(int mCameraId) {
        this.mCameraId = mCameraId;
    }

    public int getCurrentFlashMode() {
        return mCurrentFlashMode;
    }

    public void setCurrentFlashMode(int currentFlashMode) {
        this.mCurrentFlashMode = currentFlashMode;
    }

    public CameraPreview(Activity activity, int cameraId, LayoutMode mode, int currentFlashMode) {
        super(activity); // Always necessary
        mActivity = activity;
        mLayoutMode = mode;
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mCurrentFlashMode = currentFlashMode;
        camera = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            if (Camera.getNumberOfCameras() > cameraId) {
                mCameraId = cameraId;
            } else {
                mCameraId = DEFAULT_CAMERA;
            }
        } else {
            mCameraId = DEFAULT_CAMERA;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            camera = Camera.open(mCameraId);
        } else {
            camera = Camera.open();
        }
        Camera.Parameters cameraParams = camera.getParameters();
        mPreviewSizeList = cameraParams.getSupportedPreviewSizes();
        mPictureSizeList = cameraParams.getSupportedPictureSizes();
    }

    public void takePicture(PictureCallback pictureCallback){
        if(camera != null){
            try {
                camera.takePicture(null, null, pictureCallback);
            } catch (Exception e) {
                LogUtils.logI(TAG, "Take picture exception: " + e.toString());
            }

        }
    }

    /////////////////////////////////////////////////////////

    public void startRecording()
    {

        try {
            Ulti.createFolder(Constants.VIDEO_FOLDER);
            long time = Calendar.getInstance().getTimeInMillis();
            fileName = Constants.VIDEO_FOLDER + Constants.PREFIX_VIDEO_NAME + time + Constants.VIDEO_TYPE;

            mediaRecorder = new MediaRecorder();  // Works well
            camera.unlock();

            mediaRecorder.setCamera(camera);

            mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

            mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
            mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
            mediaRecorder.setOutputFile("/sdcard/safeapp/abc.mp4");

            mediaRecorder.prepare();
            mediaRecorder.start();
//
//
            videoObject = new VideoObject();
            videoObject.setId(Constants.PREFIX_VIDEO_ID + time);
            videoObject.setVideoUrl(fileName);
            videoObject.setTime(time);





        }catch (Exception e){
            e.printStackTrace();
            Ulti.deleteFile(fileName);
        }


    }


    public void releaseMediaRecorder(){
        if (mediaRecorder != null && camera != null) {
            mediaRecorder.reset();   // clear recorder configuration
            mediaRecorder.release(); // release the recorder object
            mediaRecorder = null;
            camera.lock();           // lock camera for later use
        }

        if(videoObject != null && !videoObject.getVideoUrl().isEmpty()){
            LogUtils.logD(TAG, "Save video starting...");
            VideoQueueManager.getInstance(mActivity).addVideoInQueue(videoObject, true);
        }else {
            LogUtils.logD(TAG, "Fail to save video");
        }
    }





    //////////////////////////////////////////////////////////

    public int toggleSwitchCamera(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            if (Camera.getNumberOfCameras() > DEFAULT_CAMERA) {
                if(mCameraId == DEFAULT_CAMERA){
                    mCameraId = FRONT_CAMERA;
                }else {
                    mCameraId = DEFAULT_CAMERA;
                }
            } else {
                mCameraId = DEFAULT_CAMERA;
            }
        } else {
            mCameraId = DEFAULT_CAMERA;
        }
        return mCameraId;
    }

    public int toggleSplashMode(){
        if (mCurrentFlashMode < FLASH_OFF) {
            mCurrentFlashMode++;
        } else {
            mCurrentFlashMode = FLASH_AUTO;
        }
        setFlashMode();
        return mCurrentFlashMode;
    }

    private void setFlashMode(){
        if(camera != null && isSupportFlash(camera)){
            Camera.Parameters parameters = camera.getParameters();
            String flashModeStr = Parameters.FLASH_MODE_AUTO;
            switch (mCurrentFlashMode) {
                case FLASH_AUTO:
                    flashModeStr = Parameters.FLASH_MODE_AUTO;
                    break;
                case FLASH_ON:
                    flashModeStr = Parameters.FLASH_MODE_ON;
                    break;
                case FLASH_OFF:
                    flashModeStr = Parameters.FLASH_MODE_OFF;
                    break;
                default:
                    flashModeStr = Parameters.FLASH_MODE_AUTO;
                    mCurrentFlashMode = FLASH_AUTO;
                    break;
            }

            parameters.setFlashMode(flashModeStr);
            camera.setParameters(parameters);
        }

    }

    public void resetFlashModeToAuto(){
        mCurrentFlashMode = FLASH_AUTO;
        setFlashMode();
    }

    PreviewReadyCallback mPreviewReadyCallback = null;

    public static enum LayoutMode {
        FitToParent, // Scale to the size that no side is larger than the parent
        NoBlank // Scale to the size that no side is smaller than the parent
    };

    public interface PreviewReadyCallback {
        public void onPreviewReady();
    }

    /**
     * State flag: true when surface's layout size is set and surfaceChanged()
     * process has not been completed.
     */
    protected boolean mSurfaceConfiguring = false;


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera.setPreviewCallback(this);
            camera.setPreviewDisplay(surfaceHolder);
        } catch (IOException e) {
            camera.release();
            camera = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mSurfaceChangedCallDepth++;
        doSurfaceChanged(width, height);
        mSurfaceChangedCallDepth--;
    }

    private void doSurfaceChanged(int width, int height) {
        camera.stopPreview();

        Camera.Parameters cameraParams = camera.getParameters();
        boolean portrait = isPortrait();

        // The code in this if-statement is prevented from executed again when surfaceChanged is
        // called again due to the change of the layout size in this if-statement.
        if (!mSurfaceConfiguring) {
            Camera.Size previewSize = determinePreviewSize(portrait, width, height);
            Camera.Size pictureSize = determinePictureSize(previewSize);
//            previewSize.width = 352;
//            previewSize.height = 288;
//            pictureSize.width = 352;
//            pictureSize.height = 288;
            if (DEBUGGING) { Log.v(LOG_TAG, "Desired Preview Size - w: " + width + ", h: " + height); }
            mPreviewSize = previewSize;
            mPictureSize = pictureSize;
            mSurfaceConfiguring = adjustSurfaceLayoutSize(previewSize, portrait, width, height);
            // Continue executing this method if this method is called recursively.
            // Recursive call of surfaceChanged is very special case, which is a path from
            // the catch clause at the end of this method.
            // The later part of this method should be executed as well in the recursive
            // invocation of this method, because the layout change made in this recursive
            // call will not trigger another invocation of this method.
            if (mSurfaceConfiguring && (mSurfaceChangedCallDepth <= 1)) {
                return;
            }
        }

        configureCameraParameters(cameraParams, portrait);
        mSurfaceConfiguring = false;

        try {
            camera.startPreview();
        } catch (Exception e) {
            Log.w(LOG_TAG, "Failed to start preview: " + e.getMessage());

            // Remove failed size
            mPreviewSizeList.remove(mPreviewSize);
            mPreviewSize = null;

            // Reconfigure
            if (mPreviewSizeList.size() > 0) { // prevent infinite loop
                surfaceChanged(null, 0, width, height);
            } else {
                Toast.makeText(mActivity, "Can't start preview", Toast.LENGTH_LONG).show();
                Log.w(LOG_TAG, "Gave up starting preview");
            }
        }

        if (null != mPreviewReadyCallback) {
            mPreviewReadyCallback.onPreviewReady();
        }
    }

    /**
     * @param cameraParams
     * @param portrait
     * @param reqWidth must be the value of the parameter passed in surfaceChanged
     * @param reqHeight must be the value of the parameter passed in surfaceChanged
     * @return Camera.Size object that is an element of the list returned from Camera.Parameters.getSupportedPreviewSizes.
     */
    protected Camera.Size determinePreviewSize(boolean portrait, int reqWidth, int reqHeight) {
        // Meaning of width and height is switched for preview when portrait,
        // while it is the same as user's view for surface and metrics.
        // That is, width must always be larger than height for setPreviewSize.
        int reqPreviewWidth; // requested width in terms of camera hardware
        int reqPreviewHeight; // requested height in terms of camera hardware
        if (portrait) {
            reqPreviewWidth = reqHeight;
            reqPreviewHeight = reqWidth;
        } else {
            reqPreviewWidth = reqWidth;
            reqPreviewHeight = reqHeight;
        }

        if (DEBUGGING) {
            Log.v(LOG_TAG, "Listing all supported preview sizes");
            for (Camera.Size size : mPreviewSizeList) {
                Log.v(LOG_TAG, "  w: " + size.width + ", h: " + size.height + ". Ratio: " + (float) (size.width / size.height));
            }
            Log.v(LOG_TAG, "Listing all supported picture sizes");
            for (Camera.Size size : mPictureSizeList) {
                Log.v(LOG_TAG, "  w: " + size.width + ", h: " + size.height);
            }
        }

        // Adjust surface size with the closest aspect-ratio
        float reqRatio = ((float) reqPreviewWidth) / reqPreviewHeight;
        float curRatio, deltaRatio;
        float deltaRatioMin = Float.MAX_VALUE;
        Camera.Size retSize = null;
        for (Camera.Size size : mPreviewSizeList) {
            curRatio = ((float) size.width) / size.height;
            deltaRatio = Math.abs(reqRatio - curRatio);
            if (deltaRatio < deltaRatioMin) {
                deltaRatioMin = deltaRatio;
                retSize = size;
            }
        }

        return retSize;
    }

    protected Camera.Size determinePictureSize(Camera.Size previewSize) {
        Camera.Size retSize = null;
        for (Camera.Size size : mPictureSizeList) {
            if (size.equals(previewSize)) {
                return size;
            }
        }

        if (DEBUGGING) { Log.v(LOG_TAG, "Same picture size not found."); }

        // if the preview size is not supported as a picture size
        float reqRatio = ((float) previewSize.width) / previewSize.height;
        float curRatio, deltaRatio;
        float deltaRatioMin = Float.MAX_VALUE;
        for (Camera.Size size : mPictureSizeList) {
            curRatio = ((float) size.width) / size.height;
            deltaRatio = Math.abs(reqRatio - curRatio);
            if (deltaRatio < deltaRatioMin) {
                deltaRatioMin = deltaRatio;
                retSize = size;
            }
        }

        return retSize;
    }

    protected boolean adjustSurfaceLayoutSize(Camera.Size previewSize, boolean portrait,
                                              int availableWidth, int availableHeight) {
        float tmpLayoutHeight, tmpLayoutWidth;
        if (portrait) {
            tmpLayoutHeight = previewSize.width;
            tmpLayoutWidth = previewSize.height;
        } else {
            tmpLayoutHeight = previewSize.height;
            tmpLayoutWidth = previewSize.width;
        }

        float factH, factW, fact;
        factH = availableHeight / tmpLayoutHeight;
        factW = availableWidth / tmpLayoutWidth;
        if (mLayoutMode == LayoutMode.FitToParent) {
            // Select smaller factor, because the surface cannot be set to the size larger than display metrics.
            if (factH < factW) {
                fact = factH;
            } else {
                fact = factW;
            }
        } else {
            if (factH < factW) {
                fact = factW;
            } else {
                fact = factH;
            }
        }

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)this.getLayoutParams();

        int layoutHeight = (int) (tmpLayoutHeight * fact);
        int layoutWidth = (int) (tmpLayoutWidth * fact);
        if (DEBUGGING) {
            Log.v(LOG_TAG, "Preview Layout Size - w: " + layoutWidth + ", h: " + layoutHeight);
            Log.v(LOG_TAG, "Scale factor: " + fact);
        }

        boolean layoutChanged;
        if ((layoutWidth != this.getWidth()) || (layoutHeight != this.getHeight())) {
            layoutParams.height = layoutHeight;
            layoutParams.width = layoutWidth;
            if (mCenterPosX >= 0) {
                layoutParams.topMargin = mCenterPosY - (layoutHeight / 2);
                layoutParams.leftMargin = mCenterPosX - (layoutWidth / 2);
            }
            this.setLayoutParams(layoutParams); // this will trigger another surfaceChanged invocation.
            layoutChanged = true;
        } else {
            layoutChanged = false;
        }

        return layoutChanged;
    }

    /**
     * @param x X coordinate of center position on the screen. Set to negative value to unset.
     * @param y Y coordinate of center position on the screen.
     */
    public void setCenterPosition(int x, int y) {
        mCenterPosX = x;
        mCenterPosY = y;
    }

    protected void configureCameraParameters(Camera.Parameters cameraParams, boolean portrait) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) { // for 2.1 and before
            if (portrait) {
                cameraParams.set(CAMERA_PARAM_ORIENTATION, CAMERA_PARAM_PORTRAIT);
            } else {
                cameraParams.set(CAMERA_PARAM_ORIENTATION, CAMERA_PARAM_LANDSCAPE);
            }
        } else { // for 2.2 and later
            int angle;
            Display display = mActivity.getWindowManager().getDefaultDisplay();
            switch (display.getRotation()) {
                case Surface.ROTATION_0: // This is display orientation
                    angle = 90; // This is camera orientation
                    break;
                case Surface.ROTATION_90:
                    angle = 0;
                    break;
                case Surface.ROTATION_180:
                    angle = 270;
                    break;
                case Surface.ROTATION_270:
                    angle = 180;
                    break;
                default:
                    angle = 90;
                    break;
            }
            Log.v(LOG_TAG, "angle: " + angle);
            camera.setDisplayOrientation(angle);
        }

        setCameraSizeAndPictureSize(cameraParams);

        String flashModeStr = Parameters.FLASH_MODE_AUTO;
        switch (mCurrentFlashMode) {
            case FLASH_AUTO:
                flashModeStr = Parameters.FLASH_MODE_AUTO;
                break;
            case FLASH_ON:
                flashModeStr = Parameters.FLASH_MODE_ON;
                break;
            case FLASH_OFF:
                flashModeStr = Parameters.FLASH_MODE_OFF;
                break;
            default:
                flashModeStr = Parameters.FLASH_MODE_AUTO;
                mCurrentFlashMode = FLASH_AUTO;
                break;
        }

        if(mCameraId == DEFAULT_CAMERA && isSupportFlash(camera)){
            cameraParams.setFlashMode(flashModeStr);
        }

        camera.setParameters(cameraParams);
    }

    private void setCameraSizeAndPictureSize(Camera.Parameters cameraParams){
//      cameraParams.setPreviewSize(mPreviewSize.width, mPreviewSize.height);

        Size sizeOfCamera = null;

        for (int i = 0; i < cameraParams.getSupportedPreviewSizes().size(); i++) {
            Size previewSize = cameraParams.getSupportedPreviewSizes().get(i);
            boolean foundEqualSize = false;
            for (int j = 0; j < cameraParams.getSupportedPictureSizes().size(); j++) {
                Size pictureSize = cameraParams.getSupportedPictureSizes().get(j);
                if(previewSize.width == pictureSize.width && previewSize.height == pictureSize.height){
                    sizeOfCamera = previewSize;
                    foundEqualSize = true;
                    break;
                }
            }

            if(foundEqualSize) break;
        }

        if(sizeOfCamera == null){
            for (int i = 0; i < cameraParams.getSupportedPictureSizes().size(); i++) {
                if(cameraParams.getSupportedPictureSizes().get(i).width <= mPreviewSize.width){
                    sizeOfCamera = cameraParams.getSupportedPictureSizes().get(i);
                    break;
                }
            }
        }

        cameraParams.setPreviewSize(sizeOfCamera.width, sizeOfCamera.height);
        cameraParams.setPictureSize(sizeOfCamera.width, sizeOfCamera.height);

        if (DEBUGGING) {
//             Log.v(LOG_TAG, "Preview Actual Size - w: " + mPreviewSize.width + ", h: " + mPreviewSize.height);
            Log.v(LOG_TAG, "Picture Actual Size - w: " + sizeOfCamera.width + ", h: " + sizeOfCamera.height);
        }
    }

    private boolean isSupportFlash(Camera camera){
        boolean isSupport = false;
        List<String> listFlashMode = camera.getParameters().getSupportedFlashModes();
        if(listFlashMode != null && listFlashMode.size() > 0){

            for (int i = 0; i < listFlashMode.size(); i++) {
                if(listFlashMode.get(i).equalsIgnoreCase("on")){
                    isSupport = true;
                }
            }
        }else {
            isSupport = false;
        }

        return isSupport;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stop();
    }

    public void stop() {
        if (null == camera) {
            return;
        }
        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    public boolean isPortrait() {
        return (mActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
    }

    public void setOneShotPreviewCallback(PreviewCallback callback) {
        if (null == camera) {
            return;
        }
        camera.setOneShotPreviewCallback(callback);
    }

    public void setPreviewCallback(PreviewCallback callback) {
        if (null == camera) {
            return;
        }
        camera.setPreviewCallback(callback);
    }

    public Camera.Size getPreviewSize() {
        return mPreviewSize;
    }

    public void setOnPreviewReady(PreviewReadyCallback cb) {
        mPreviewReadyCallback = cb;
    }

    public void resetCam() {
        camera.startPreview();
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
//		// File name of the image that we just took.
//					String fileName = "IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()).toString() + ".jpg";
//
//					// Creating the directory where to save the image. Sadly in older
//					// version of Android we can not get the Media catalog name
//					File mkDir = new File(Constants.SDROOT, Constants.CAMERA_DIR);
//					if(!mkDir.exists()){
//						mkDir.mkdirs();
//					}
//
//					// Main file where to save the data that we recive from the camera
//					File pictureFile = new File(Constants.SDROOT, Constants.CAMERA_DIR + fileName);
//
//					try {
//						FileOutputStream fos = new FileOutputStream(pictureFile);
//						fos.write(data);
//						fos.close();
//						
//					} catch (Exception e) {
//						Log.d("DG_DEBUG", "Error accessing file: " + e.getMessage());
//					}

    }

    //	/ decode Y, U, and V values on the YUV 420 buffer described as YCbCr_422_SP by Android
    // David Manpearl 081201
    public void decodeYUV(int[] out, byte[] fg, int width, int height)
            throws NullPointerException, IllegalArgumentException {
        int sz = width * height;
        if (out == null)
            throw new NullPointerException("buffer out is null");
        if (out.length < sz)
            throw new IllegalArgumentException("buffer out size " + out.length
                    + " < minimum " + sz);
        if (fg == null)
            throw new NullPointerException("buffer 'fg' is null");
        if (fg.length < sz)
            throw new IllegalArgumentException("buffer fg size " + fg.length
                    + " < minimum " + sz * 3 / 2);
        int i, j;
        int Y, Cr = 0, Cb = 0;
        for (j = 0; j < height; j++) {
            int pixPtr = j * width;
            final int jDiv2 = j >> 1;
            for (i = 0; i < width; i++) {
                Y = fg[pixPtr];
                if (Y < 0)
                    Y += 255;
                if ((i & 0x1) != 1) {
                    final int cOff = sz + jDiv2 * width + (i >> 1) * 2;
                    Cb = fg[cOff];
                    if (Cb < 0)
                        Cb += 127;
                    else
                        Cb -= 128;
                    Cr = fg[cOff + 1];
                    if (Cr < 0)
                        Cr += 127;
                    else
                        Cr -= 128;
                }
                int R = Y + Cr + (Cr >> 2) + (Cr >> 3) + (Cr >> 5);
                if (R < 0)
                    R = 0;
                else if (R > 255)
                    R = 255;
                int G = Y - (Cb >> 2) + (Cb >> 4) + (Cb >> 5) - (Cr >> 1)
                        + (Cr >> 3) + (Cr >> 4) + (Cr >> 5);
                if (G < 0)
                    G = 0;
                else if (G > 255)
                    G = 255;
                int B = Y + Cb + (Cb >> 1) + (Cb >> 2) + (Cb >> 6);
                if (B < 0)
                    B = 0;
                else if (B > 255)
                    B = 255;
                out[pixPtr++] = 0xff000000 + (B << 16) + (G << 8) + R;
            }
        }

    }
}
