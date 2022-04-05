package com.example.skinsafe.Database;

import android.graphics.Bitmap;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface HistoryDaoClass {

    @Insert
    void insertAllData(HistoryModel model);

    //Select All Data
    @Query("select * from  user")
    List<HistoryModel> getAllData();

    //Delete Data
    @Query("delete from user where `key`= :key")
    void deleteData(int key);

    //Update Data
    @Query("update user SET image= :image, time =:time, results =:results where `key`= :key")
    void updateData(Bitmap image, String time, float[] results, int key);
}