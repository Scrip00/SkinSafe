package com.example.skinsafe.Database;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user")
public class TrackModel {

    //Primary key
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int key;
    // key, image, time, [6] results, place, name, next
    @ColumnInfo(name = "image")
    private Bitmap image;

    @ColumnInfo(name = "time")
    private String time;

    @ColumnInfo(name = "results")
    private float[] results;

    @ColumnInfo(name = "place")
    private String place;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "next")
    private int next;

    @ColumnInfo(name = "head")
    private Boolean head;

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

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNext() {
        return next;
    }

    public void setNext(int next) {
        this.next = next;
    }

    public Boolean isHead() { return head; }

    public void setHead(Boolean head) {
        this.head = head;
    }
}