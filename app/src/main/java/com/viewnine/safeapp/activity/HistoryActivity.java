package com.viewnine.safeapp.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.viewnine.safeapp.Adapter.VideoAdapter;
import com.viewnine.safeapp.manager.HistoryManager;
import com.viewnine.safeapp.manager.SwitchViewManager;
import com.viewnine.safeapp.model.VideoObject;
import com.viewnine.safeapp.ulti.AlertHelper;
import com.viewnine.safeapp.ulti.Constants;
import com.viewnine.safeapp.ulti.LogUtils;

import java.util.ArrayList;
import java.util.Calendar;

import in.srain.cube.views.GridViewWithHeaderAndFooter;

/**
 * Created by user on 4/24/15.
 */
public class HistoryActivity extends ParentActivity implements AbsListView.OnScrollListener{
    ArrayList<VideoObject> listVideos;
    String lastVideoId = Constants.EMPTY_STRING;
    VideoAdapter videoAdapter;
    private GridViewWithHeaderAndFooter gridviewVideos;
    private ListView listviewVideos;
    private LinearLayout lnTabBar;
    private Button btnGrid;
    private Button btnList;
    private Button btnRecord;
    private static final int GRID_TAB = 0;
    private static final int LIST_TAB = 1;
    private int currentTab = GRID_TAB;
    private ViewGroup mFooterListView;
    private ViewGroup mFooterViewGridView;
    private boolean needToShowLoadMore = true;
    private TextView txtLoadMore;
    private ImageView imgGrid;
    private ImageView imgList;
    private RelativeLayout rlGrid;
    private RelativeLayout rlList;
    private SaveVideoReceiver saveVideoReceiver;
    private String TAG = HistoryActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupViews();
        initData();
    }

    private void setupViews() {
        addChidlView(R.layout.history);
        addSettingButton();
        addGoToShareButton();
        addVideoNumber(0);


        gridviewVideos = (GridViewWithHeaderAndFooter) findViewById(R.id.griview_history);
        listviewVideos = (ListView) findViewById(R.id.listview_history);
        gridviewVideos.setOnScrollListener(this);
        listviewVideos.setOnScrollListener(this);
        addFooterView();
        lnTabBar = (LinearLayout) findViewById(R.id.linearlayout_tabbar);
        btnGrid = (Button) findViewById(R.id.button_grid);
        btnList = (Button) findViewById(R.id.button_list);
        btnRecord = (Button) findViewById(R.id.button_start_record_foreground_video);
        imgList = (ImageView) findViewById(R.id.imageview_list);
        imgGrid = (ImageView) findViewById(R.id.imageview_grid);
        rlList = (RelativeLayout) findViewById(R.id.relativelayout_list);
        rlGrid = (RelativeLayout) findViewById(R.id.relativelayout_grid);
        rlList.setOnClickListener(this);
        rlGrid.setOnClickListener(this);
        btnGrid.setOnClickListener(this);
        btnList.setOnClickListener(this);
        btnRecord.setOnClickListener(this);
        handleClickTabBar(currentTab);

        videoAdapter = new VideoAdapter(this);
        gridviewVideos.setAdapter(videoAdapter);
        listviewVideos.setAdapter(videoAdapter);



    }

    private void registerVideoReceiver(){
        saveVideoReceiver = new SaveVideoReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTION_BROADCAST_RECIVER_VIDEO);
        registerReceiver(saveVideoReceiver, intentFilter);
    }

    private void unregisterVideoReceiver(){
        unregisterReceiver(saveVideoReceiver);
    }

    private void initData(){
        listVideos = new ArrayList<VideoObject>();

        getListVideo(Calendar.getInstance().getTimeInMillis(), true);

    }

    private void getListVideo(long time, boolean isShowLoadingPopup){

        HistoryManager.getInstance().getListVideos(this, time, isShowLoadingPopup, new HistoryManager.IGetVideoListener() {
            @Override
            public void listVideos(ArrayList<VideoObject> listVideo, int totalVideos) {

                handleDataAfterGetListVideo(listVideo, totalVideos);
            }

            @Override
            public void error(int errorCode) {
                AlertHelper.getInstance().showMessageAlert(HistoryActivity.this, getString(R.string.error));
            }
        });
    }

    private void handleDataAfterGetListVideo(ArrayList<VideoObject> listVideosTmp, int totalVideos){
        if (listVideosTmp != null && listVideosTmp.size() > 0) {
            if(videoAdapter.isEmpty()){
                listVideos.clear();
            }
            listVideos.addAll(listVideosTmp);
            videoAdapter.setListVideos(listVideos);
        }
        addVideoNumber(totalVideos);
        if(listVideos.size() == totalVideos){
            needToShowLoadMore = false;
            mFooterListView.setVisibility(View.INVISIBLE);
            mFooterViewGridView.setVisibility(View.INVISIBLE);
        }else {
            needToShowLoadMore = true;
            mFooterListView.setVisibility(View.VISIBLE);
            mFooterViewGridView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.relativelayout_grid:
            case R.id.button_grid:
                if(currentTab != GRID_TAB){
                    handleClickTabBar(GRID_TAB);
                }
                break;
            case R.id.relativelayout_list:
            case R.id.button_list:
                if(currentTab != LIST_TAB){
                    handleClickTabBar(LIST_TAB);
                }
                break;
            case R.id.button_start_record_foreground_video:
                handleClickRecordVideo();
                break;
            default:
        }

    }

    private void handleClickRecordVideo() {
        SwitchViewManager.getInstance().gotoRecordForegroundVideoScreen(this);
    }

    private void handleClickTabBar(int currentTab) {
        this.currentTab = currentTab;

        gridviewVideos.setVisibility(View.GONE);
        listviewVideos.setVisibility(View.GONE);
        switch (this.currentTab){
            case GRID_TAB:
                gridviewVideos.setVisibility(View.VISIBLE);
                imgGrid.setVisibility(View.VISIBLE);
                imgList.setVisibility(View.INVISIBLE);
                break;
            case LIST_TAB:
                listviewVideos.setVisibility(View.VISIBLE);
                imgGrid.setVisibility(View.INVISIBLE);
                imgList.setVisibility(View.VISIBLE);
                break;
            default:
        }

    }

    private void addFooterView() {
        final LayoutInflater layoutInflater = LayoutInflater.from(this);
        mFooterListView = (ViewGroup) layoutInflater.inflate(
                R.layout.footer_loadmore, null);
        listviewVideos.addFooterView(mFooterListView);


        mFooterViewGridView = (ViewGroup) layoutInflater.inflate(
                R.layout.footer_loadmore, null);
        gridviewVideos.addFooterView(mFooterViewGridView);
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        int lastVisiblePositionItemOfListView = listviewVideos.getLastVisiblePosition();
        int lastVisiablePositionItemOfGridView = gridviewVideos.getLastVisiblePosition();
        boolean needToGetVideos = needToShowLoadMore && listVideos != null && listVideos.size() > 0 && ((lastVisiblePositionItemOfListView == totalItemCount - 1) || (lastVisiablePositionItemOfGridView == totalItemCount - 1));
        if(needToGetVideos){
            long time = listVideos.get(listVideos.size() - 1).getTime();
            getListVideo(time, false);
        }

    }

    @Override
    protected void onResume() {
        registerVideoReceiver();
        super.onResume();
    }

    @Override
    protected void onStop() {
        unregisterVideoReceiver();
        super.onStop();
    }

    private class SaveVideoReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtils.logI(TAG, "Received notification signal: video is saved");
            initData();
        }
    }
}
