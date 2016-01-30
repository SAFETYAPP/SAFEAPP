package com.viewnine.nuttysnap.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.viewnine.nuttysnap.ulti.Constants;

/**
 * Created by user on 1/27/16.
 */
@ParseClassName("Feed")
public class PostObject extends ParseObject {

    public String getFeedName(){
        return getString(Constants.PARSE_FEED_NAME);
    }

    public void setFeedName(String feedName){
        put(Constants.PARSE_FEED_NAME, feedName);
    }

    public int getLikeNumber(){
        return getInt(Constants.PARSE_Like_NUMBER);
    }

    public void setLikeNumber(int likeNumber){
        put(Constants.PARSE_Like_NUMBER, likeNumber);
    }

    public ParseFile getImageFile(){
        return getParseFile(Constants.PARSE_IMAGE_FILE);
    }

    public void setImageFile(ParseFile imageFile){
        put(Constants.PARSE_IMAGE_FILE, imageFile);
    }

    public ParseFile getVideoFile(){
        return getParseFile(Constants.PARSE_VIDEO_FILE);
    }

    public void setVideoFile(ParseFile imageFile){
        put(Constants.PARSE_VIDEO_FILE, imageFile);
    }

    public ParseFile getThumbnailFile(){
        return getParseFile(Constants.PARSE_THUMBNAIL);
    }

    public void setThumbnailFile(ParseFile imageFile){
        put(Constants.PARSE_THUMBNAIL, imageFile);
    }


}
