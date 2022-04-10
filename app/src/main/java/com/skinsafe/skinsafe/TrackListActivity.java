package com.skinsafe.skinsafe;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ListView;

import com.skinsafe.skinsafe.Database.TrackDaoClass;
import com.skinsafe.skinsafe.Database.TrackDatabaseClass;
import com.skinsafe.skinsafe.Database.TrackModel;
import com.skinsafe.skinsafe.Adapters.TrackListAdapter;

import java.util.ArrayList;

public class TrackListActivity extends AppCompatActivity {
    ArrayList<Bitmap> image;
    ArrayList<String> time;
    ArrayList<float[]> results;
    ArrayList<String> place;
    ArrayList<String> name;
    ArrayList<Integer> next;
    ArrayList<Boolean> head;
    ArrayList<Integer> keys;
    ListView listView;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_list);
        ArrayList<TrackModel> modelList = new ArrayList<>();

        image = new ArrayList<>();
        time = new ArrayList<>();
        results = new ArrayList<>();
        place = new ArrayList<>();
        name = new ArrayList<>();
        next = new ArrayList<>();
        head = new ArrayList<>();
        keys = new ArrayList<>();

        id = getIntent().getIntExtra("ID", -1);

        id = getHead(id);
        TrackDaoClass dao = TrackDatabaseClass.getDatabase(this).getDao();
        TrackModel model = dao.loadSingle(id);
        while(model.getNext() != -1) {
            modelList.add(model);
            model = dao.loadSingle(model.getNext());
        }
        modelList.add(model);

        for (TrackModel trackModel: modelList) {
            image.add(trackModel.getImage());
            time.add(trackModel.getTime());
            results.add(trackModel.getResults());
            place.add(trackModel.getPlace());
            name.add(trackModel.getName());
            next.add(trackModel.getNext());
            head.add(trackModel.isHead());
            keys.add(trackModel.getKey());
        }

        listView = findViewById(R.id.trackActivityList);
        listView.setAdapter(new TrackListAdapter(this, image, time, results, place, name, next, head, keys, true));
    }

    private int getHead(int id) {
        TrackDaoClass dao = TrackDatabaseClass.getDatabase(this).getDao();
        TrackModel model = dao.loadSingle(id);
        while(!model.isHead()) {
            model = dao.findParent(model.getKey());
        }
        return model.getKey();
    }
}