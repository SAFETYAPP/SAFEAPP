package com.viewnine.nuttysnap.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by user on 4/21/15.
 */
public class VideoObject implements  Parcelable{

    String id;
    String videoUrl;
    String imageLink;
    long time;
    boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

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


    public static final Parcelable.Creator<VideoObject> CREATOR = new Creator<VideoObject>() {
        @Override
        public VideoObject createFromParcel(Parcel source) {
            VideoObject videoObject = new VideoObject();
            videoObject.id = source.readString();
            videoObject.videoUrl = source.readString();
            videoObject.imageLink = source.readString();
            videoObject.time = source.readLong();

//            source.readTypedList(user.Tags, Tag.CREATOR);
//            source.readTypedList(user.listMajor, MajorObject.CREATOR);

            return videoObject;
        }

        @Override
        public VideoObject[] newArray(int size) {
            return new VideoObject[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(id);
        parcel.writeString(videoUrl);
        parcel.writeString(imageLink);
        parcel.writeLong(time);
//        parcel.writeTypedList(Tags);
//        parcel.writeTypedList(listMajor);
    }
}
