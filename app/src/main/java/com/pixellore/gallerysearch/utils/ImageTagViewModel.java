package com.pixellore.gallerysearch.utils;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public class ImageTagViewModel extends AndroidViewModel {

    private ImageTagDatabase imageTagDatabase;

    public ImageTagViewModel(@NonNull Application application) {
        super(application);
        imageTagDatabase = ImageTagDatabase.getDatabase(application);
    }

    public Flowable<List<ImageTag>> getAll() {
        return imageTagDatabase.imageTagDao().getAll();
    }

    public Flowable<ImageTag> findByImageId(String mediaStoreId) {
        return imageTagDatabase.imageTagDao().findByImageId(mediaStoreId);
    }

    public Completable insertImageTagRow(final ImageTag imageTag) {
        return imageTagDatabase.imageTagDao().insertImageTagRow(imageTag);
    }

    public Completable deleteImageTagRow(final ImageTag imageTag) {
        return imageTagDatabase.imageTagDao().deleteImageTagRow(imageTag);
    }

    public Completable deleteAll(){
        return imageTagDatabase.imageTagDao().deleteAll();
    }

}
