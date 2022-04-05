package com.example.skinsafe.Database;

import android.graphics.Bitmap;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TrackDaoClass {

    @Insert
    long insertData(TrackModel model);

    //Select All Data
    @Query("select * from user")
    List<TrackModel> getAllData();

    @Query("select * from user WHERE `key`=:key")
    TrackModel loadSingle(int key);

    @Query("select * from user WHERE next=:next")
    TrackModel findParent(int next);

    //Delete Data
    @Query("delete from user where `key`= :key")
    void deleteData(int key);

    //Update Data
    @Query("update user SET image= :image, time =:time, results =:results, place =:place, name =:name, next =:next, head =:head where `key`= :key")
    void updateData(Bitmap image, String time, float[] results, String place, String name, int next, Boolean head, int key);

    @Query("update user SET place =:place, name =:name where `key`= :key")
    void updateData(String place, String name, int key);

    @Query("update user SET next =:next where `key`= :key")
    void updateTale(int next, int key);

    @Query("update user SET head =:head where `key`= :key")
    void updateHead(Boolean head, int key);
}