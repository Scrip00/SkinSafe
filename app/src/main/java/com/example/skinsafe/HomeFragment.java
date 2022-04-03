package com.example.skinsafe;

import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.image.TensorImage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {
    private Button button;
    private ImageView humanView, anterior_torso, head_neck, lateral_torso, lower_extremity, oral_genital, palms_soles, upper_extremity;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        humanView = rootView.findViewById(R.id.humanView);
        //humanView.setVisibility(View.INVISIBLE);
        anterior_torso = rootView.findViewById(R.id.anterior_torso);
        head_neck = rootView.findViewById(R.id.head_neck);
        lateral_torso = rootView.findViewById(R.id.lateral_torso);
        lower_extremity = rootView.findViewById(R.id.lower_extremity);
        oral_genital = rootView.findViewById(R.id.oral_genital);
        palms_soles = rootView.findViewById(R.id.palms_soles);
        upper_extremity = rootView.findViewById(R.id.upper_extremity);

        deleteRecursive(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES));

        List<String> list= readFromFile();
        Set<String> set = new HashSet<>();
        for (int i = 0; i < list.size(); i++){
            if (!list.get(i).split(",")[8].equals(null) && !list.get(i).split(",")[8].equals("")){
                set.add(list.get(i).split(",")[8]);
            }
        }
        List<String> names = new ArrayList<>(Arrays.asList("Anterior torso", "Head/neck", "Lateral torso", "Lower extremity", "Oral/genital", "Palms/soles", "Posterior torso", "Upper extremity"));
        try {
            for(String str: set){
                if (names.contains(str)){
                    switch (str){
                        case "Anterior torso":
                        case "Posterior torso":
                            InputStream ims = getContext().getAssets().open("Anterior torso.png");
                            Drawable d = Drawable.createFromStream(ims, null);
                            anterior_torso.setImageDrawable(d);
                            ims.close();
                            break;
                        case "Head/neck":
                            ims = getContext().getAssets().open("Head_neck.png");
                            d = Drawable.createFromStream(ims, null);
                            head_neck.setImageDrawable(d);
                            ims.close();
                            break;
                        case "Lateral torso":
                            ims = getContext().getAssets().open("Lateral torso.png");
                            d = Drawable.createFromStream(ims, null);
                            lateral_torso.setImageDrawable(d);
                            ims.close();
                            break;
                        case "Lower extremity":
                            ims = getContext().getAssets().open("Lower extremity.png");
                            d = Drawable.createFromStream(ims, null);
                            lower_extremity.setImageDrawable(d);
                            ims.close();
                            break;
                        case "Oral/genital":
                            ims = getContext().getAssets().open("Oral_genital.png");
                            d = Drawable.createFromStream(ims, null);
                            oral_genital.setImageDrawable(d);
                            ims.close();
                            break;
                        case "Palms/soles":
                            ims = getContext().getAssets().open("Palms_soles.png");
                            d = Drawable.createFromStream(ims, null);
                            palms_soles.setImageDrawable(d);
                            ims.close();
                            break;
                        case "Upper extremity":
                            ims = getContext().getAssets().open("Upper extremity.png");
                            d = Drawable.createFromStream(ims, null);
                            upper_extremity.setImageDrawable(d);
                            ims.close();
                            break;
                    }

                }
            }
            InputStream ims = getContext().getAssets().open("human.png");
            Drawable d = Drawable.createFromStream(ims, null);
            humanView.setImageDrawable(d);
            ims.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rootView;
    }

    void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }

    private List<String> readFromFile() {
        List<String> ret = new ArrayList<>();
        try {
            InputStream inputStream = getActivity().openFileInput("config_track.txt");
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    ret.add(receiveString);
                }

                inputStream.close();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        return ret;
    }
}
