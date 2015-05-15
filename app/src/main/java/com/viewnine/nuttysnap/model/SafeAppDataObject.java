package com.viewnine.nuttysnap.model;

import java.util.Observable;

/**
 * Created by user on 4/28/15.
 */
public class SafeAppDataObject extends Observable {

    private VideoObject videoObject;

    public VideoObject getVideoObject(){
        return videoObject;
    }

    public void notifyVideoChanged(String currentView, VideoObject videoObject, int changeType){
        this.videoObject = videoObject;
        setChanged();
        notifyObservers(new DataObject(currentView, changeType));
    }
}
