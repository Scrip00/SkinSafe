package com.skinsafe.skinsafe.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {
        TrackModel.class
}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class TrackDatabaseClass extends RoomDatabase {

    public abstract TrackDaoClass getDao();
    private static TrackDatabaseClass instance;

    public static TrackDatabaseClass getDatabase(final Context context) {
        if (instance == null) {
            synchronized(TrackDatabaseClass.class) {
                instance = Room.databaseBuilder(context, TrackDatabaseClass.class, "TRACK_DATABASE").allowMainThreadQueries().fallbackToDestructiveMigration().build();
            }
        }
        return instance;
    }
}


