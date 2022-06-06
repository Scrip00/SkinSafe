package com.skinsafe.skinsafe.MainMenu;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.skinsafe.skinsafe.Database.HistoryDatabaseClass;
import com.skinsafe.skinsafe.Database.HistoryModel;
import com.skinsafe.skinsafe.Adapters.HistoryListAdapter;
import com.skinsafe.skinsafe.R;

import java.util.ArrayList;

public class HistoryFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArrayList<HistoryModel> modelList = (ArrayList<HistoryModel>) HistoryDatabaseClass.getDatabase(getContext().getApplicationContext()).getDao().getAllData();

        ArrayList<String> time = new ArrayList<>();
        ArrayList<Bitmap> image = new ArrayList<>();
        ArrayList<float[]> results = new ArrayList<>();
        ArrayList<Integer> keys = new ArrayList<>();

        for (HistoryModel model: modelList) {
            time.add(model.getTime());
            image.add(model.getImage());
            results.add(model.getResults());
            keys.add(model.getKey());
        }

        View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        ListView listView = rootView.findViewById(R.id.list_1);
        listView.setAdapter(new HistoryListAdapter(getContext(), image, time, results, keys));
        return rootView;
    }
}
