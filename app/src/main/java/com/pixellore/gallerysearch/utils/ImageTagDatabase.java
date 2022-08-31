package com.pixellore.gallerysearch.utils;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {ImageTag.class}, version = 1)
public abstract class ImageTagDatabase extends RoomDatabase {

    private static ImageTagDatabase INSTANCE;

    public abstract ImageTagDao imageTagDao();

    public static ImageTagDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (ImageTagDatabase.class){
                if (INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ImageTagDatabase.class, "ImageTag.db").build();
                }
            }
        }
        return INSTANCE;
    }

}
