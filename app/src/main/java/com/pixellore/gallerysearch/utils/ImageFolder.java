package com.pixellore.gallerysearch.utils;

import androidx.annotation.NonNull;

/**
 *
 * {@link ImageFolder} holds information of a folder containing images
 *  on the device external storage
 *
 */
public class ImageFolder {

    /* Path to the folder */
    private String mPath;

    /* Name of the folder */
    private String mFolderName;

    /* Number of images in the folder */
    private int mNumberOfPics = 0;

    /* Name of latest image in the folder (used for representing the folder) */
    private  String mLatestPic;

    /**
     * Create a new ImageFolder object without initializing any state variables
     * */
    public ImageFolder(){

    }

    /**
     *
     * Create a new ImageFolder object by initializing state variables path and folderName
     *
     * @param path is the path to the folder
     * @param folderName is the name of the folder
     *
     * */
    public ImageFolder(String path, String folderName){
        mPath = path;
        mFolderName = folderName;
    }

    /*
     * Return folder path
     * */
    public String getPath(){
        return mPath;
    }

    /*
     * Return folder name
     * */
    public  String getFolderName(){
        return mFolderName;
    }

    /*
     * Return number of pictures in the folder
     * */
    public int getNumberOfPics(){
        return mNumberOfPics;
    }

    /*
     * Return the name of the latest image in the folder
     * */
    public String getLatestPic(){
        return mLatestPic;
    }

    /**
     * Set the data path to the folder
     * */
    public void setPath(String path){
        mPath = path;
    }

    /**
     * Set the folder name
     * */
    public void setFolderName(String folderName) {
        mFolderName = folderName;
    }

    /**
     * Set the name of the last picture in the folder
     * */
    public void setLatestPic(String lastPic) {
        mLatestPic = lastPic;
    }

    /**
     * Count the number of images in the folder by incrementing state variable mNumberOfPics
     * This method is called each time an image if found in the folder
     * */
    public void countPics(){
        mNumberOfPics++;
    }

    /**
     * Overriding the toString method to return the folder names so that the spinner in the
     * search dialog box displays the folder names only
     * */
    @NonNull
    @Override
    public String toString() {
        return mFolderName;
    }
}
