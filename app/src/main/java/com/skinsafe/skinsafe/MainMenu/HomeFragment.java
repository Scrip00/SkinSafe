package com.skinsafe.skinsafe.MainMenu;

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.devs.vectorchildfinder.VectorChildFinder;
import com.devs.vectorchildfinder.VectorDrawableCompat;
import com.skinsafe.skinsafe.Database.TrackDaoClass;
import com.skinsafe.skinsafe.Database.TrackDatabaseClass;
import com.skinsafe.skinsafe.Database.TrackModel;
import com.skinsafe.skinsafe.R;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HomeFragment extends Fragment {
    ImageView humanSVG;
    View rootView;
    TextView appName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        deleteRecursive(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES));

        humanSVG = rootView.findViewById(R.id.humanSVG);
        appName = rootView.findViewById(R.id.appNameTextView);

        Shader shader = new LinearGradient(0, 0, 0, appName.getLineHeight() * 1.10f,
                Color.parseColor("#CC3F0C"), Color.parseColor("#EB7B52"), Shader.TileMode.REPEAT);
        appName.getPaint().setShader(shader);

        setUpHuman();
        humanSVG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO finish this one
            }
        });
        return rootView;
    }


    void setUpHuman() {
        TrackDaoClass database = TrackDatabaseClass.getDatabase(rootView.getContext()).getDao();
        List<TrackModel> heads = database.getAllParents(true);
        Set<String> paths = new HashSet<>();
        for (TrackModel model : heads) {
            paths.add(model.getPlace());
        }
        int color = getResources().getColor(R.color.header_text_color);
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
            path = vector.findPathByName("right_side");
            path.setFillColor(color);
        }
        if (paths.contains("Lower extremity")) {
            VectorDrawableCompat.VFullPath path = vector.findPathByName("left_leg");
            path.setFillColor(color);
            path = vector.findPathByName("right_leg");
            path.setFillColor(color);
        }
        if (paths.contains("Oral/genital")) {
            VectorDrawableCompat.VFullPath path = vector.findPathByName("oral");
            path.setFillColor(color);
            path = vector.findPathByName("genital");
            path.setFillColor(color);
        }
        if (paths.contains("Palms/soles")) {
            VectorDrawableCompat.VFullPath path = vector.findPathByName("left_palm");
            path.setFillColor(color);
            path = vector.findPathByName("right_palm");
            path.setFillColor(color);
            path = vector.findPathByName("left_sole");
            path.setFillColor(color);
            path = vector.findPathByName("right_sole");
            path.setFillColor(color);
        }
        if (paths.contains("Upper extremity")) {
            VectorDrawableCompat.VFullPath path = vector.findPathByName("left_arm");
            path.setFillColor(color);
            path = vector.findPathByName("right_arm");
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
