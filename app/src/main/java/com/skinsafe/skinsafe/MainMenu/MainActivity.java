package com.skinsafe.skinsafe.MainMenu;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.skinsafe.skinsafe.R;

public class MainActivity extends AppCompatActivity {

    private Fragment selectedFragment;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

        selectedFragment = null;

        BottomNavigationView upperNav = findViewById(R.id.upper_navigation);
        upperNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
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
        });

        upperNav.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                switch (upperNav.getSelectedItemId()) {
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
            }
        });

        Intent intent = getIntent();

        if (intent.getStringExtra("frag") != null && intent.getStringExtra("frag").equals("track")) {
            upperNav.setSelectedItemId(R.id.nav_track);
        }

        if (getIntent().getStringExtra("selectedFragment") != null && getIntent().getStringExtra("selectedFragment").equals("track")) {
            Fragment selectedFragment = new TrackFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
        }
    }
}