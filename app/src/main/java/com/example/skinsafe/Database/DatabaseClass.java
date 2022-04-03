package com.example.skinsafe.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {
        UserModel.class
}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class DatabaseClass extends RoomDatabase {

    public abstract Daoclass getDao();
    private static DatabaseClass instance;

    public static DatabaseClass getDatabase(final Context context) {
        if (instance == null) {
            synchronized(DatabaseClass.class) {
                instance = Room.databaseBuilder(context, DatabaseClass.class, "DATABASE").allowMainThreadQueries().build();
            }
        }
        return instance;
    }
}
