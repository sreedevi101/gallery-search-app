package com.pixellore.gallerysearch.utils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class saves all the filters set for each search
 * */
public class SearchFilter implements Parcelable {

    // The texts to be searched in the image names
    private String[] mSearchWords;

    // The folder in which the search is to be performed
    private String mFolderName;

    // data path of the folder
    private String mFolderPath;

    // search content - image name,image tags or both
    private String mSearchCategory;

    public SearchFilter(){

    }

    public SearchFilter(String[] searchWords, String folderName, String searchCategory) {
        this.mFolderName = folderName;
        this.mSearchWords = searchWords;
        this.mSearchCategory = searchCategory;
    }

    protected SearchFilter(Parcel in) {
        mSearchWords = in.createStringArray();
        mFolderName = in.readString();
        mFolderPath = in.readString();
        mSearchCategory = in.readString();
    }

    public static final Creator<SearchFilter> CREATOR = new Creator<SearchFilter>() {
        @Override
        public SearchFilter createFromParcel(Parcel in) {
            return new SearchFilter(in);
        }

        @Override
        public SearchFilter[] newArray(int size) {
            return new SearchFilter[size];
        }
    };

    public String getFolderName() {
        return mFolderName;
    }

    public String[] getSearchWords() {
        return mSearchWords;
    }

    public String getFolderPath() {
        return mFolderPath;
    }

    public String getSearchCategory() {
        return mSearchCategory;
    }

    public void setFolderName(String folderName) {
        this.mFolderName = folderName;
    }

    public void setSearchWords(String[] searchWords) {
        this.mSearchWords = searchWords;
    }

    public void setFolderPath(String folderPath) {
        this.mFolderPath = folderPath;
    }

    public void setSearchCategory(String searchCategory) {
        this.mSearchCategory = searchCategory;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(mSearchWords);
        parcel.writeString(mFolderName);
        parcel.writeString(mFolderPath);
        parcel.writeString(mSearchCategory);
    }
}
