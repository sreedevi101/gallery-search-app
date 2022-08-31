package com.pixellore.gallerysearch.utils;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import io.reactivex.Completable;
import io.reactivex.Flowable;

import java.util.List;

/**
 * Data Access Object for the {@link ImageTag} table.
 */
@Dao
public interface ImageTagDao {

    @Query("SELECT * FROM image_tag")
    Flowable<List<ImageTag>> getAll();

    // Get a row based on the MediaStore ID of the image
    @Query("SELECT * FROM image_tag WHERE image_id = :mediaStoreId")
    Flowable<ImageTag> findByImageId(String mediaStoreId);

    // Insert a new image with its tags to the table
    @Insert
    Completable insertImageTagRow(ImageTag imageTag);


    // Delete an image and its tags from the table
    @Delete
    Completable deleteImageTagRow(ImageTag imageTag);


    // Clear all the data (all images and their tags) from the table
    @Query("DELETE FROM image_tag")
    Completable deleteAll();

}
