package com.pixellore.gallerysearch.utils;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/*
* Entity for database to store image ids (MediaStore Image ID) and
* the corresponding image tags added by the user
* */
@Entity(tableName = "image_tag")
public class ImageTag implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "database_id")
    private long id;

    @ColumnInfo(name = "image_id")
    private String imageId;

    @ColumnInfo(name = "tag")
    private String tag;


    public ImageTag(long id, String imageId, String tag) {
        this.id = id;
        this.imageId = imageId;
        this.tag = tag;
    }

    @Ignore
    public ImageTag(String imageId, String tag) {
        this.imageId = imageId;
        this.tag = tag;
    }

    protected ImageTag(Parcel in) {
        id = in.readLong();
        imageId = in.readString();
        tag = in.readString();
    }

    public static final Creator<ImageTag> CREATOR = new Creator<ImageTag>() {
        @Override
        public ImageTag createFromParcel(Parcel in) {
            return new ImageTag(in);
        }

        @Override
        public ImageTag[] newArray(int size) {
            return new ImageTag[size];
        }
    };

    public long getId() {
        return id;
    }

    public String getImageId() {
        return imageId;
    }

    public String getTag() {
        return tag;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(imageId);
        parcel.writeString(tag);
    }
}
