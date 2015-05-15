package com.viewnine.nuttysnap.manager;

import android.content.Context;

import com.viewnine.nuttysnap.database.VideoDBAdapter;
import com.viewnine.nuttysnap.model.VideoObject;
import com.viewnine.nuttysnap.ulti.BaseAsyncTaskV2;
import com.viewnine.nuttysnap.ulti.Constants;

import java.util.ArrayList;

/**
 * Created by user on 4/24/15.
 */
public class HistoryManager {
    private static HistoryManager ourInstance = new HistoryManager();

    public static HistoryManager getInstance() {
        return ourInstance;
    }

    private HistoryManager() {
    }

    public interface IGetVideoListener{
        public void listVideos(ArrayList<VideoObject> listVideo, int totalVideos);
        public void error(int errorCode);
    }

    public void getListVideos(Context context, long latestVideoTime, boolean isShowLoadingPopup, IGetVideoListener iGetVideoListener){
        new GetVideoAsync(context, latestVideoTime, isShowLoadingPopup, iGetVideoListener).execute();
    }

    private class GetVideoAsync extends BaseAsyncTaskV2{
        Context context;
        long latestVideoTime;
        IGetVideoListener iGetVideoListener;
        ArrayList<VideoObject> listVideos;
        int totalVideo;
        public GetVideoAsync(Context context, long latestVideoTime, boolean isShowLoadingPopup, IGetVideoListener iGetVideoListener){
            super(context);
            needToShowDialog(isShowLoadingPopup);
            this.context = context;
            this.latestVideoTime = latestVideoTime;
            this.iGetVideoListener = iGetVideoListener;
        }
        private GetVideoAsync(Context context){
            super(context);

        }

        @Override
        protected Integer doInBackground(Void... params) {

            VideoDBAdapter videoDBAdapter = new VideoDBAdapter(context);
            listVideos = videoDBAdapter.getListVideosBaseOnTime(latestVideoTime);
            totalVideo = videoDBAdapter.getTotalVideos();
            if(listVideos != null){
                return Constants.OK;
            }
            return Constants.ERROR;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            switch (result){
                case Constants.OK:
                    iGetVideoListener.listVideos(listVideos, totalVideo);
                    break;
                case Constants.ERROR:
                default:
                    iGetVideoListener.error(result);

            }
        }
    }
}
