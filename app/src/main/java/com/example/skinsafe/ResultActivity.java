package com.example.skinsafe;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultActivity extends AppCompatActivity {
    ImageView photoImage;
    TextView mainDig, title, secondDig0, secondDig1, secondDig2, secondDig3, secondDig4;
    boolean save;
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
            saveToFile(output, bitMap);
        }

    }

    private void saveToFile(float[] output, Bitmap bitMap){
        String name = "his_" + String.valueOf(savedCount()) + ".png";
        try {
            FileOutputStream out = openFileOutput(name, MODE_PRIVATE);
            bitMap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
            List<String> list = new ArrayList<>(readFromFile());
            String temp = "";
            for (int i = 0; i < list.size(); i++){
                temp += list.get(i) + "\n";
            }
            Date currentTime = Calendar.getInstance().getTime();
            temp += output[0] + "," + output[1] + "," + output[2] + "," + output[3] + "," + output[4] + "," + output[5] + "," + name + "," + currentTime;
            writeToFile(temp);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void writeToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("config_history.txt", MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }


    private List<String> readFromFile() {

        List<String> ret = new ArrayList<>();

        try {
            InputStream inputStream = openFileInput("config_history.txt");

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

    private int savedCount(){
        File temp = new File(this.getFilesDir().getPath());
        int c = 0;
        for (File file: temp.listFiles()){
            if (file.getName().split("\\.")[0].split("_")[0].equals("his")){
                c++;  //his_1
            }
        }
        return c;
    }

}