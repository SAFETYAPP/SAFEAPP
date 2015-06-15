package com.viewnine.nuttysnap.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by user on 4/21/15.
 */
@Table(name = "VIDEOS")
public class VideoObject extends Model implements  Parcelable{

    @Column(name = "videoId")
    String videoId;
    @Column(name = "videoUrl")
    String videoUrl;
    @Column(name = "imageLink")
    String imageLink;
    @Column(name = "time")
    long time;

    @Column(name = "physicalAddress")
    String physicalAddress;

    boolean isSelected;

    @Column(name = "isAddedWatermark")
    int isAddedWatermark;

    @Column(name = "cameraMode")
    int cameraMode;

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

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String id) {
        this.videoId = id;
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

    public int isAddedWatermark() {
        return isAddedWatermark;
    }

    public void setIsAddedWatermark(int isAddedWatermark) {
        this.isAddedWatermark = isAddedWatermark;
    }

    public String getPhysicalAddress() {
        return physicalAddress;
    }

    public void setPhysicalAddress(String physicalAddress) {
        this.physicalAddress = physicalAddress;
    }

    public int getCameraMode() {
        return cameraMode;
    }

    public void setCameraMode(int cameraMode) {
        this.cameraMode = cameraMode;
    }

    public static final Parcelable.Creator<VideoObject> CREATOR = new Creator<VideoObject>() {
        @Override
        public VideoObject createFromParcel(Parcel source) {
            VideoObject videoObject = new VideoObject();
            videoObject.videoId = source.readString();
            videoObject.videoUrl = source.readString();
            videoObject.imageLink = source.readString();
            videoObject.time = source.readLong();
            videoObject.isAddedWatermark = source.readInt();
            videoObject.cameraMode = source.readInt();
            videoObject.physicalAddress = source.readString();

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
        parcel.writeString(videoId);
        parcel.writeString(videoUrl);
        parcel.writeString(imageLink);
        parcel.writeLong(time);
        parcel.writeInt(isAddedWatermark);
        parcel.writeInt(cameraMode);
        parcel.writeString(physicalAddress);
//        parcel.writeTypedList(Tags);
//        parcel.writeTypedList(listMajor);
    }
}
