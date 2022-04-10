package com.skinsafe.skinsafe.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {
        HistoryModel.class
}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class HistoryDatabaseClass extends RoomDatabase {

    public abstract HistoryDaoClass getDao();
    private static HistoryDatabaseClass instance;

    public static HistoryDatabaseClass getDatabase(final Context context) {
        if (instance == null) {
            synchronized(HistoryDatabaseClass.class) {
                instance = Room.databaseBuilder(context, HistoryDatabaseClass.class, "HISTORY_DATABASE").allowMainThreadQueries().fallbackToDestructiveMigration().build();
            }
        }
        return instance;
    }
}


