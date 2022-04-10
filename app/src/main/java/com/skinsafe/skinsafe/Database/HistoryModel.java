package com.skinsafe.skinsafe.Database;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user")
public class HistoryModel {

    //Primary key
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int key;
    // key, image, time, [6] results, next key
    @ColumnInfo(name = "image")
    private Bitmap image;

    @ColumnInfo(name = "time")
    private String time;

    @ColumnInfo(name = "results")
    private float[] results;

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public float[] getResults() {
        return results;
    }

    public void setResults(float[] results) {
        this.results = results;
    }
}