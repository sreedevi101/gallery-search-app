package com.pixellore.gallerysearch.utils;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Custom class for holding data of images
 * */
public class Image implements Parcelable {

    private String mImageName;
    private String mImagePath;
    private String mImageSize;
    private String mImageId;
    private String mImageFolderPath;
    private String mImageCreatedDate;
    private String mImageModifiedDate;
    private String mImageDescription;
    private int mImageFavourite;

    public Image() {

    }

    public Image(String imageName, String imagePath, String imageSize, String imageId, String imageFolderPath,
                 String imageCreatedDate, String imageModifiedDate,
                 String imageDescription, int isFavourite) {
        mImageName = imageName;
        mImagePath = imagePath;
        mImageSize = imageSize;
        mImageId = imageId;
        mImageFolderPath = imageFolderPath;
        mImageCreatedDate = imageCreatedDate;
        mImageModifiedDate = imageModifiedDate;
        mImageDescription = imageDescription;
        mImageFavourite = isFavourite;
    }

    protected Image(Parcel in) {
        mImageName = in.readString();
        mImagePath = in.readString();
        mImageSize = in.readString();
        mImageId = in.readString();
        mImageFolderPath = in.readString();
        mImageCreatedDate = in.readString();
        mImageModifiedDate = in.readString();
        mImageDescription = in.readString();
        mImageFavourite = in.readInt();
    }

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };

    public String getImageName() {
        return mImageName;
    }

    public String getImagePath() {
        return mImagePath;
    }

    public String getImageSize() {
        return mImageSize;
    }

    public String getImageId() {
        return mImageId;
    }

    public String getImageFolderPath() {
        return mImageFolderPath;
    }

    public String getImageCreatedDate() {
        return mImageCreatedDate;
    }

    public String getImageModifiedDate() {
        return mImageModifiedDate;
    }

    public String getImageDescription() {
        return mImageDescription;
    }

    public int getImageFavourite() {
        return mImageFavourite;
    }

    public void setImageName(String imageName) {
        this.mImageName = imageName;
    }

    public void setImagePath(String imagePath) {
        this.mImagePath = imagePath;
    }

    public void setImageSize(String imageSize) {
        this.mImageSize = imageSize;
    }

    public void setImageId(String imageId) {
        this.mImageId = imageId;
    }

    public void setImageFolderPath(String imageFolderPath) {
        this.mImageFolderPath = imageFolderPath;
    }

    public void setImageCreatedDate(String imageCreatedDate) {
        this.mImageCreatedDate = imageCreatedDate;
    }

    public void setImageModifiedDate(String imageModifiedDate) {
        this.mImageModifiedDate = imageModifiedDate;
    }

    public void setImageDescription(String imageDescription) {
        this.mImageDescription = imageDescription;
    }

    public void setImageFavourite(int isImageFavourite) {
        this.mImageFavourite = isImageFavourite;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mImageName);
        parcel.writeString(mImagePath);
        parcel.writeString(mImageSize);
        parcel.writeString(mImageId);
        parcel.writeString(mImageFolderPath);
        parcel.writeString(mImageCreatedDate);
        parcel.writeString(mImageModifiedDate);
        parcel.writeString(mImageDescription);
        parcel.writeInt(mImageFavourite);
    }
}
