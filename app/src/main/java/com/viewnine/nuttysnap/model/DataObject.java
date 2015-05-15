package com.viewnine.nuttysnap.model;

import com.viewnine.nuttysnap.ulti.Constants;

/**
 * Created by user on 4/28/15.
 */
public class DataObject {
    private String fromView = Constants.EMPTY_STRING;
    private int typeChange;

    public DataObject(String fromView, int typeChange){
        this.fromView = fromView;
        this.typeChange = typeChange;
    }

    public String getFromView(){return fromView;}
    public int getTypeChange(){return typeChange;}
}
