package com.example.skinsafe;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.skinsafe.Database.HistoryModel;
import com.example.skinsafe.Database.TrackDatabaseClass;
import com.example.skinsafe.Database.TrackModel;

import java.util.ArrayList;

public class TrackFragment extends Fragment {
    ArrayList<Bitmap> image;
    ArrayList<String> time;
    ArrayList<float[]> results;
    ArrayList<String> place;
    ArrayList<String> name;
    ArrayList<Integer> next;
    ArrayList<Boolean> head;
    ArrayList<Integer> keys;
    ListView listView;
    Button createNewTrack;
    View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_track, container, false);

        createNewTrack = rootView.findViewById(R.id.createNewTrack);

        ArrayList<TrackModel> modelList = (ArrayList<TrackModel>) TrackDatabaseClass.getDatabase(getContext().getApplicationContext()).getDao().getAllData();

        image = new ArrayList<>();
        time = new ArrayList<>();
        results = new ArrayList<>();
        place = new ArrayList<>();
        name = new ArrayList<>();
        next = new ArrayList<>();
        head = new ArrayList<>();
        keys = new ArrayList<>();

        for (TrackModel model: modelList) {
            if (model.isHead()) {
                image.add(model.getImage());
                time.add(model.getTime());
                results.add(model.getResults());
                place.add(model.getPlace());
                name.add(model.getName());
                next.add(model.getNext());
                head.add(model.isHead());
                keys.add(model.getKey());
            }
        }

        listView = rootView.findViewById(R.id.list_2);
        listView.setAdapter(new TrackListAdapter(getContext(), image, time, results, place, name, next, head, keys, false));

        createNewTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AdditionActivity.class);
                intent.putExtra("ID", -1);
                getContext().startActivity(intent);
            }
        });

        return rootView;
    }
}
