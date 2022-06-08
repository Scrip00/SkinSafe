package com.skinsafe.skinsafe.MainMenu;

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

import com.skinsafe.skinsafe.Database.TrackDatabaseClass;
import com.skinsafe.skinsafe.Database.TrackModel;
import com.skinsafe.skinsafe.R;
import com.skinsafe.skinsafe.TrackDetailsActivity;
import com.skinsafe.skinsafe.Adapters.TrackListAdapter;

import java.util.ArrayList;

public class TrackFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_track, container, false);
        getActivity().overridePendingTransition(R.anim.fade_out, R.anim.fade_in);

        Button createNewTrack = rootView.findViewById(R.id.createNewTrack);

        ArrayList<TrackModel> modelList = (ArrayList<TrackModel>) TrackDatabaseClass.getDatabase(getContext().getApplicationContext()).getDao().getAllData();

        ArrayList<Bitmap> image = new ArrayList<>();
        ArrayList<String> time = new ArrayList<>();
        ArrayList<float[]> results = new ArrayList<>();
        ArrayList<String> place = new ArrayList<>();
        ArrayList<String> name = new ArrayList<>();
        ArrayList<Integer> next = new ArrayList<>();
        ArrayList<Boolean> head = new ArrayList<>();
        ArrayList<Integer> keys = new ArrayList<>();

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

        ListView listView = rootView.findViewById(R.id.list_2);
        listView.setAdapter(new TrackListAdapter(getContext(), image, time, results, place, name, next, head, keys, false));

        createNewTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), TrackDetailsActivity.class);
                intent.putExtra("ID", -1);
                getContext().startActivity(intent);
            }
        });

        return rootView;
    }
}
