package com.example.skinsafe;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.devs.vectorchildfinder.VectorChildFinder;
import com.devs.vectorchildfinder.VectorDrawableCompat;
import com.example.skinsafe.Database.TrackDaoClass;
import com.example.skinsafe.Database.TrackDatabaseClass;
import com.example.skinsafe.Database.TrackModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HomeFragment extends Fragment {
    ImageView humanSVG;
    View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        deleteRecursive(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES));

        humanSVG = rootView.findViewById(R.id.humanSVG);

        setUpHuman();
        humanSVG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        return rootView;
    }


    void setUpHuman() {
        TrackDaoClass database = TrackDatabaseClass.getDatabase(rootView.getContext()).getDao();
        List<TrackModel> heads = database.getAllParents(true);
        Set<String> paths = new HashSet<>();
        for (TrackModel model: heads) {
            paths.add(model.getPlace());
        }
        int color = Color.RED;
        VectorChildFinder vector = new VectorChildFinder(rootView.getContext(), R.drawable.ic_human_svg, humanSVG);
        if (paths.contains("Anterior torso") || paths.contains("Posterior torso")) {
            VectorDrawableCompat.VFullPath path = vector.findPathByName("torso");
            path.setFillColor(color);
        }
        if (paths.contains("Head/neck")) {
            VectorDrawableCompat.VFullPath path = vector.findPathByName("head");
            path.setFillColor(color);
        }
        if (paths.contains("Lateral torso")) {
            VectorDrawableCompat.VFullPath path = vector.findPathByName("left_side");
            path.setFillColor(color);
            path = vector.findPathByName ("right_side");
            path.setFillColor(color);
        }
        if (paths.contains("Lower extremity")) {
            VectorDrawableCompat.VFullPath path = vector.findPathByName("left_leg");
            path.setFillColor(color);
            path = vector.findPathByName ("right_leg");
            path.setFillColor(color);
        }
        if (paths.contains("Oral/genital")) {
            VectorDrawableCompat.VFullPath path = vector.findPathByName("oral");
            path.setFillColor(color);
            path = vector.findPathByName ("genital");
            path.setFillColor(color);
        }
        if (paths.contains("Palms/soles")) {
            VectorDrawableCompat.VFullPath path = vector.findPathByName("left_palm");
            path.setFillColor(color);
            path = vector.findPathByName ("right_palm");
            path.setFillColor(color);
            path = vector.findPathByName ("left_sole");
            path.setFillColor(color);
            path = vector.findPathByName ("right_sole");
            path.setFillColor(color);
        }
        if (paths.contains("Upper extremity")) {
            VectorDrawableCompat.VFullPath path = vector.findPathByName("left_arm");
            path.setFillColor(color);
            path = vector.findPathByName ("right_arm");
            path.setFillColor(color);
        }
        humanSVG.invalidate();
    }

    void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }
}
