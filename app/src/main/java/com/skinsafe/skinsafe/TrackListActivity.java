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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_list);
        ArrayList<TrackModel> modelList = new ArrayList<>();

        ArrayList<Bitmap> image = new ArrayList<>();
        ArrayList<String> time = new ArrayList<>();
        ArrayList<float[]> results = new ArrayList<>();
        ArrayList<String> place = new ArrayList<>();
        ArrayList<String> name = new ArrayList<>();
        ArrayList<Integer> next = new ArrayList<>();
        ArrayList<Boolean> head = new ArrayList<>();
        ArrayList<Integer> keys = new ArrayList<>();

        int id = getIntent().getIntExtra("ID", -1);

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

        ListView listView = findViewById(R.id.trackActivityList);
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