package com.viewnine.safeapp.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.viewnine.safeapp.Adapter.VideoAdapter;
import com.viewnine.safeapp.manager.HistoryManager;
import com.viewnine.safeapp.manager.SwitchViewManager;
import com.viewnine.safeapp.model.VideoObject;
import com.viewnine.safeapp.ulti.AlertHelper;
import com.viewnine.safeapp.ulti.Constants;

import java.util.ArrayList;

import in.srain.cube.views.GridViewWithHeaderAndFooter;

/**
 * Created by user on 4/24/15.
 */
public class HistoryActivity extends ParentActivity {
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
    private ViewGroup mFooterView;
    private ViewGroup mFooterViewGridView;

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
        addFooterView();
        lnTabBar = (LinearLayout) findViewById(R.id.linearlayout_tabbar);
        btnGrid = (Button) findViewById(R.id.button_grid);
        btnList = (Button) findViewById(R.id.button_list);
        btnRecord = (Button) findViewById(R.id.button_start_record_foreground_video);
        btnGrid.setOnClickListener(this);
        btnList.setOnClickListener(this);
        btnRecord.setOnClickListener(this);
        handleClickTabBar(currentTab);

    }

    private void initData(){
        listVideos = new ArrayList<VideoObject>();
        videoAdapter = new VideoAdapter(this);
        gridviewVideos.setAdapter(videoAdapter);
        listviewVideos.setAdapter(videoAdapter);
        getListVideo();

    }

    private void getListVideo(){

        HistoryManager.getInstance().getListVideos(this, lastVideoId, new HistoryManager.IGetVideoListener() {
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
            lastVideoId = listVideosTmp.get(listVideosTmp.size() - 1).getId();
            if(videoAdapter.isEmpty()){
                listVideos.clear();
            }
            listVideos.addAll(listVideosTmp);
            videoAdapter.setListVideos(listVideos);
        }
        addVideoNumber(totalVideos);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.button_grid:
                if(currentTab != GRID_TAB){
                    handleClickTabBar(GRID_TAB);
                }
                break;
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
                break;
            case LIST_TAB:
                listviewVideos.setVisibility(View.VISIBLE);
                break;
            default:
        }

    }

    private void addFooterView() {
        final LayoutInflater layoutInflater = LayoutInflater.from(this);
        mFooterView = (ViewGroup) layoutInflater.inflate(
                R.layout.footer_loadmore, null);
        listviewVideos.addFooterView(mFooterView);


        mFooterViewGridView = (ViewGroup) layoutInflater.inflate(
                R.layout.footer_loadmore, null);
        gridviewVideos.addFooterView(mFooterViewGridView);
    }


}
