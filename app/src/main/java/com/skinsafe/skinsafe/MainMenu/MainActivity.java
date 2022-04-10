package com.skinsafe.skinsafe.MainMenu;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.skinsafe.skinsafe.R;
import com.skinsafe.skinsafe.ResultActivity;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.image.TensorImage;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        bottomNav = findViewById(R.id.upper_navigation);
        bottomNav.setOnItemSelectedListener(navListener);
        Intent intent = getIntent();

        if (intent.getStringExtra("frag") != null && intent.getStringExtra("frag").equals("track")){
            bottomNav.setSelectedItemId(R.id.nav_track);
        }

        if (getIntent().getStringExtra("selectedFragment") != null && getIntent().getStringExtra("selectedFragment").equals("track")) {
            Fragment selectedFragment = new TrackFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener(){
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                        switch (item.getItemId()){
                            case R.id.nav_home:
                                selectedFragment = new HomeFragment();
                                break;
                            case R.id.nav_history:
                                selectedFragment = new HistoryFragment();
                                break;
                            case R.id.nav_scan:
                                selectedFragment = new ScanFragment();
                                break;
                            case R.id.nav_track:
                                selectedFragment = new TrackFragment();
                                break;
                        }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                    return true;
                }
            };
}