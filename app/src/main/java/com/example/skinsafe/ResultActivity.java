package com.example.skinsafe;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.skinsafe.Database.HistoryDatabaseClass;
import com.example.skinsafe.Database.HistoryModel;
import com.example.skinsafe.Database.TrackDatabaseClass;
import com.example.skinsafe.Database.TrackModel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultActivity extends AppCompatActivity {
    ImageView photoImage;
    TextView mainDig, title, secondDig0, secondDig1, secondDig2, secondDig3, secondDig4;
    boolean save;
    int track;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        photoImage = findViewById(R.id.photoImage);
        mainDig = findViewById(R.id.mainDig);
        title = findViewById(R.id.title);
        secondDig0 = findViewById(R.id.secondDig0);
        secondDig1 = findViewById(R.id.secondDig1);
        secondDig2 = findViewById(R.id.secondDig2);
        secondDig3 = findViewById(R.id.secondDig3);
        secondDig4 = findViewById(R.id.secondDig4);

        Intent intent = getIntent();
        track = intent.getIntExtra("track", -2);
        save = intent.getBooleanExtra("saveOrNot", true);
        float[] output = intent.getFloatArrayExtra("output");
        Bitmap bitMap = (Bitmap) intent.getExtras().get("imageBitmap");
        photoImage.setImageBitmap(bitMap);
        setDiagnoze(output, bitMap);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void setDiagnoze(float[] output, Bitmap bitMap){
        Map<Float, String> digMap = new HashMap<>();
        digMap.put(output[0], "Actinic Keratosis");
        digMap.put(output[1], "Basal Cell Carcinoma");
        digMap.put(output[2], "Melanoma");
        digMap.put(output[3], "Nevus");
        digMap.put(output[4], "Seborrheic Keratosis");
        digMap.put(output[5], "Squamous Cell Carcinoma");
        List<Float> list = new ArrayList<>();
        list.addAll(digMap.keySet());
        Collections.sort(list);
        DecimalFormat df = new DecimalFormat("#.##");
        for (int i = list.size() - 1; i >= 0; i--){
            if (i == list.size() - 1){
                mainDig.setText("Probably it's: " + digMap.get(list.get(i)) + "\nThe chance is: " + df.format(list.get(i) * 100) + "%");
            } else {
                switch (i){
                    case 4:
                        secondDig0.setText(digMap.get(list.get(i)) + " with chance: " + df.format(list.get(i) * 100) + "%" + "\n");
                        break;
                    case 3:
                        secondDig1.setText(digMap.get(list.get(i)) + " with chance: " + df.format(list.get(i) * 100) + "%" + "\n");
                        break;
                    case 2:
                        secondDig2.setText(digMap.get(list.get(i)) + " with chance: " + df.format(list.get(i) * 100) + "%" + "\n");
                        break;
                    case 1:
                        secondDig3.setText(digMap.get(list.get(i)) + " with chance: " + df.format(list.get(i) * 100) + "%" + "\n");
                        break;
                    case 0:
                        secondDig4.setText(digMap.get(list.get(i)) + " with chance: " + df.format(list.get(i) * 100) + "%" + "\n");
                        break;
                }
            }
        }

        mainDig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity.this, JustActivity.class);
                intent.putExtra("type", digMap.get(list.get(5)));
                startActivity(intent);
            }
        });
        secondDig0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity.this, JustActivity.class);
                intent.putExtra("type", digMap.get(list.get(4)));
                startActivity(intent);
            }
        });
        secondDig1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity.this, JustActivity.class);
                intent.putExtra("type", digMap.get(list.get(3)));
                startActivity(intent);
            }
        });
        secondDig2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity.this, JustActivity.class);
                intent.putExtra("type", digMap.get(list.get(2)));
                startActivity(intent);
            }
        });
        secondDig3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity.this, JustActivity.class);
                intent.putExtra("type", digMap.get(list.get(1)));
                startActivity(intent);
            }
        });
        secondDig4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity.this, JustActivity.class);
                intent.putExtra("type", digMap.get(list.get(0)));
                startActivity(intent);
            }
        });
        if (save){
            if (track == -2) {
                Log.d("LMAO", String.valueOf(track));
                Log.d("LMAO", "SAVED TO HISTORY");
                saveToHistoryDatabase(output, bitMap);
            } else {
                Log.d("LMAO", String.valueOf(track));
                Log.d("LMAO", "SAVED TO TRACK");
                saveToTrackDatabase(output, bitMap);
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    private void saveToHistoryDatabase(float[] output, Bitmap bitMap){
        HistoryModel model = new HistoryModel();
        model.setImage(bitMap);
        model.setTime(Calendar.getInstance().getTime().toString());
        model.setResults(output);
        HistoryDatabaseClass.getDatabase(getApplicationContext()).getDao().insertAllData(model);
    }

    private void saveToTrackDatabase(float[] output, Bitmap bitMap){
        TrackModel model = new TrackModel();
        model.setImage(bitMap);
        model.setTime(Calendar.getInstance().getTime().toString());
        model.setResults(output);
        if (track == -1) {
            model.setHead(true);
        } else {
            model.setHead(false);
            // TODO update previous node
        }
        model.setNext(-1);
        model.setName((String) getIntent().getExtras().get("name"));
        model.setPlace((String) getIntent().getExtras().get("place"));
        int newId = (int) TrackDatabaseClass.getDatabase(getApplicationContext()).getDao().insertData(model);
        TrackDatabaseClass.getDatabase(this).getDao().updateTale(newId, findTale(track));
    }

    private int findTale(int id) {
        if (id == -1) {
            return -1;
        }
        TrackModel model = TrackDatabaseClass.getDatabase(this).getDao().loadSingle(id);
        while (model.getNext() != -1) {
            model = TrackDatabaseClass.getDatabase(this).getDao().loadSingle(model.getNext());
        }
        return model.getKey();
    }
}