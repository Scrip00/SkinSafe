package com.example.skinsafe;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.skinsafe.Database.DatabaseClass;
import com.example.skinsafe.Database.UserModel;

import java.util.ArrayList;

public class HistoryFragment extends Fragment {
    ArrayList<String> time;
    ArrayList<Bitmap> image;
    ArrayList<float[]> results;
    ArrayList<Integer> next;
    ArrayList<Integer> keys;
    ListView listView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArrayList<UserModel> modelList = (ArrayList<UserModel>) DatabaseClass.getDatabase(getContext().getApplicationContext()).getDao().getAllData();
        // TODO сделать исключение для трекеров

        time = new ArrayList<>();
        image = new ArrayList<>();
        results = new ArrayList<>();
        next = new ArrayList<>();
        keys = new ArrayList<>();

        for (UserModel model: modelList) {
            time.add(model.getTime());
            image.add(model.getImage());
            results.add(model.getResults());
            next.add(model.getNext());
            keys.add(model.getKey());
        }

        View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        listView = rootView.findViewById(R.id.list_1);
        listView.setAdapter(new ListAdapter(getContext(), image, time, results, next, keys));
        return rootView;
    }
}
