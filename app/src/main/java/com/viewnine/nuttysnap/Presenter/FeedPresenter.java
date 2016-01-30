package com.viewnine.nuttysnap.Presenter;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.viewnine.nuttysnap.model.PostObject;
import com.viewnine.nuttysnap.ulti.Constants;
import com.viewnine.nuttysnap.ulti.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by user on 1/27/16.
 */
public class FeedPresenter {

    private static FeedPresenter instance;
    public static FeedPresenter getInstance(){
        if(instance == null){
            synchronized (FeedPresenter.class){
                if(instance == null){
                    instance = new FeedPresenter();
                }
            }
        }

        return instance;
    }

    private FeedPresenter(){}

    public void getPostJson(){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("Test1Key", "Test1Value");
        ParseCloud.callFunctionInBackground(Constants.PARSE_GET_POST_JSON_FUNCTION, params, new FunctionCallback<String>() {
            @Override
            public void done(String results, ParseException e) {
                if(e == null){
                    LogUtils.logD(FeedPresenter.class.getSimpleName(), "Successfull. Result: " + results.toString());
                    getPostArray();
                }else {
                    LogUtils.logD(FeedPresenter.class.getSimpleName(), "Error: " + e.toString());
                }
            }
        });
    }


    public void getPostArray(){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("Test1Key", "Test1Value");
        ParseCloud.callFunctionInBackground(Constants.PARSE_GET_POST_ARRAY_FUNCTION, params, new FunctionCallback<ArrayList<PostObject>>() {
            @Override
            public void done(ArrayList<PostObject> results, ParseException e) {
                if(e == null){
                    LogUtils.logD(FeedPresenter.class.getSimpleName(), "Successfull. Result: " + results.toString());
                }else {
                    LogUtils.logD(FeedPresenter.class.getSimpleName(), "Error: " + e.toString());
                }
            }
        });
    }
}
