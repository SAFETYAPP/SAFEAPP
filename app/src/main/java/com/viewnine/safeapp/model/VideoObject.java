package com.viewnine.safeapp.model;

/**
 * Created by user on 4/21/15.
 */
public class VideoObject {

    String id;
    String videoUrl;
    String imageLink;
    long time;

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getImageLink() {
        return imageLink;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
