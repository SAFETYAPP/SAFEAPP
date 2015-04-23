package com.viewnine.safeapp.manager;

import android.content.Context;

import com.viewnine.safeapp.database.VideoDBAdapter;
import com.viewnine.safeapp.model.VideoObject;
import com.viewnine.safeapp.ulti.BaseAsyncTaskV2;
import com.viewnine.safeapp.ulti.Constants;

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

    public void getListVideos(Context context, String lastVideoId, IGetVideoListener iGetVideoListener){
        new GetVideoAsync(context, lastVideoId, iGetVideoListener).execute();
    }

    private class GetVideoAsync extends BaseAsyncTaskV2{
        Context context;
        String lastVideoId;
        IGetVideoListener iGetVideoListener;
        ArrayList<VideoObject> listVideos;
        public GetVideoAsync(Context context, String lastVideoId, IGetVideoListener iGetVideoListener){
            super(context);
            this.context = context;
            this.lastVideoId = lastVideoId;
            this.iGetVideoListener = iGetVideoListener;
        }
        private GetVideoAsync(Context context){
            super(context);

        }

        @Override
        protected Integer doInBackground(Void... params) {

            VideoDBAdapter videoDBAdapter = new VideoDBAdapter(context);
            listVideos = videoDBAdapter.getAllVideos();
            videoDBAdapter = null;
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
                    iGetVideoListener.listVideos(listVideos, listVideos.size());
                    break;
                case Constants.ERROR:
                default:
                    iGetVideoListener.error(result);

            }
        }
    }
}
