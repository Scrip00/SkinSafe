package com.example.skinsafe;

import static com.example.skinsafe.ScanFragment.PICK_IMAGE;
import static com.example.skinsafe.ScanFragment.REQUEST_IMAGE_CAPTURE;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.Image;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.image.TensorImage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AdditionActivity extends AppCompatActivity {
    private Spinner spinner;
    private Button submitBtn, submitChangesBtn, openGalBtn;
    private EditText editTextName;
    private String selected, opttext;
    private Bitmap tempBit;
    private TextView textViewDesc;
    private ImageView photoImage;
    private float[] tempfl;
    private Activity act;
    private int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        selected = "";
        opttext = "";
        id = -1;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addition);
        act = this;
        Intent intent = getIntent();
        if (intent.getExtras() == null){
            id = -1;
        } else {
            id = Integer.valueOf(intent.getExtras().get("ID").toString());
        }
        photoImage = findViewById(R.id.photoImage);
        textViewDesc = findViewById(R.id.textViewDesc);
        submitBtn = findViewById(R.id.submitBtn);
        openGalBtn = findViewById(R.id.submitBtn2);
        submitChangesBtn = findViewById(R.id.submitBtn3);
        editTextName = findViewById(R.id.editTextName);
        spinner = (Spinner) findViewById(R.id.items_spinner);
        List<String> list = new ArrayList<>(Arrays.asList("Anterior torso", "Head/neck", "Lateral torso", "Lower extremity", "Oral/genital", "Palms/soles", "Posterior torso", "Upper extremity"));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        if (id == -1){
            submitChangesBtn.setVisibility(View.INVISIBLE);
        } else {
            List<String> listt = readFromFile();
            for (int i = 0; i < listt.size(); i++){
                if (Integer.valueOf(listt.get(i).split(",")[6].split("\\.")[0].split("_")[1]) == id){
                    File temp = new File(getFilesDir().getPath());
                    for (File file: temp.listFiles()){
                        if (file.getName().split("\\.")[0].split("_")[0].equals("track") && Integer.valueOf(file.getName().split("\\.")[0].split("_")[1]) == id){
                            tempBit = BitmapFactory.decodeFile(file.getPath());
                        }
                    }
                    tempfl = new float[6];
                    for (int k = 0; k < 6; k++){
                        tempfl[k] = Float.parseFloat(listt.get(i).split(",")[k]);
                    }
                    Map<Float, String> digMap = new HashMap<>();
                    digMap.put(tempfl[0], "Actinic Keratosis");
                    digMap.put(tempfl[1], "Basal Cell Carcinoma");
                    digMap.put(tempfl[2], "Melanoma");
                    digMap.put(tempfl[3], "Nevus");
                    digMap.put(tempfl[4], "Seborrheic Keratosis");
                    digMap.put(tempfl[5], "Squamous Cell Carcinoma");
                    List<Float> listNeww = new ArrayList<>();
                    listNeww.addAll(digMap.keySet());
                    Collections.sort(listNeww, null);
                    textViewDesc.setText("When you last scanned, you probably had " + digMap.get(listNeww.get((listNeww.size() - 1))));
                    photoImage.setImageBitmap(tempBit);
                    selected = listt.get(i).split(",")[8];
                    opttext = listt.get(i).split(",")[9];
                    editTextName.setText(opttext);
                    switch (selected){
                        case "Anterior torso":
                            spinner.setSelection(0);
                            break;
                        case "Head/neck":
                            spinner.setSelection(1);
                            break;
                        case "Lateral torso":
                            spinner.setSelection(2);
                            break;
                        case "Lower extremity":
                            spinner.setSelection(3);
                            break;
                        case "Oral/genital":
                            spinner.setSelection(4);
                            break;
                        case "Palms/soles":
                            spinner.setSelection(5);
                            break;
                        case "Posterior torso":
                            spinner.setSelection(6);
                            break;
                        case "Upper extremity":
                            spinner.setSelection(7);
                            break;
                    }
                }
            }

        }
        textViewDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (id != -1){
                    Intent intent = new Intent(act, ResultActivity.class);
                    intent.putExtra("output", tempfl);
                    intent.putExtra("imageBitmap", tempBit);
                    intent.putExtra("saveOrNot", false);
                    startActivity(intent);
                }
            }
        });
        openGalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (String.valueOf(editTextName.getText()).equals("") || String.valueOf(editTextName.getText()).equals(null)){
                    Toast.makeText(act, "Name should not be empty", Toast.LENGTH_LONG).show();
                } else {
                    selected = (String) spinner.getSelectedItem();
                    opttext = String.valueOf(editTextName.getText());
                    openGallery();
                }
            }
        });
        submitChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (String.valueOf(editTextName.getText()).equals("") || String.valueOf(editTextName.getText()).equals(null)){
                    Toast.makeText(act, "Name should not be empty", Toast.LENGTH_LONG).show();
                } else {
                    selected = (String) spinner.getSelectedItem();
                    opttext = String.valueOf(editTextName.getText());
                    saveToFile(tempfl, tempBit, id, false);
                }

            }
        });
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (String.valueOf(editTextName.getText()).equals("") || String.valueOf(editTextName.getText()).equals(null)){
                    Toast.makeText(act, "Name should not be empty", Toast.LENGTH_LONG).show();
                } else {
                    selected = (String) spinner.getSelectedItem();
                    opttext = String.valueOf(editTextName.getText());
                    dispatchTakePictureIntent();
                }
            }
        });
    }
    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

        } catch (ActivityNotFoundException e) {
            // display error state to the user
        }
    }

    private void openGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
            }
            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                Bitmap imageBitmap = BitmapFactory.decodeStream(inputStream);
                newCalcNN(imageBitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            //imageView.setImageBitmap(imageBitmap);
            try {
                newCalcNN(imageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void newCalcNN(Bitmap imgBitmap) throws IOException {
        int width = 200, height = 200;
        Interpreter tflite;
        //Bitmap bitmap = BitmapFactory.decodeStream(getAssets().open("ISIC_0000142.jpg"));
        Bitmap bitmap = imgBitmap.copy(imgBitmap.getConfig(), true);
        bitmap = cropCenter(bitmap);
        bitmap = getResizedBitmap(bitmap, width, height);
        float[][][][] newinp = bitmapToInputArray(bitmap);
        //['actinic keratosis', 'basal cell carcinoma', 'melanoma', 'nevus', 'seborrheic keratosis', 'squamous cell carcinoma']
        float[][] output=new float[1][6];
        try {
            tflite = new Interpreter(loadModelFile());
            TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
            tensorImage.load(bitmap);
            tflite.run(newinp, output);
            //textView.setText(String.format("%.3g", output[0][0]) + "  " + String.format("%.3g", output[0][1]) + "  " + String.format("%.3g", output[0][2]) + "  " + String.format("%.3g", output[0][3]) + "  " + String.format("%.3g", output[0][4]) + "  " + String.format("%.3g", output[0][5]));
            openResultActivity(output, bitmap);
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void openResultActivity(float[][] output, Bitmap imageBitmap) {
        float[] out = new float[6];
        for (int i = 0; i < 6; i++){
            out[i] = output[0][i];
        }

        /*Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("output", out);
        intent.putExtra("imageBitmap", imageBitmap);
        startActivity(intent);*/
        List<String> list = readFromFile();
        if (id == -1){
            saveToFile(out, imageBitmap, savedCount(), true);
        } else {
            saveToFile(out, imageBitmap, 0, false);
        }
    }

    private void saveToFile(float[] output, Bitmap bitMap, int idd, boolean neww){
        String name = "track_" + String.valueOf(idd) + ".png";
        if (neww == true){
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
                temp += output[0] + "," + output[1] + "," + output[2] + "," + output[3] + "," + output[4] + "," + output[5] + "," + name + "," + currentTime + "," + selected + "," + opttext;
                writeToFile(temp);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                FileOutputStream out = openFileOutput(name, MODE_PRIVATE);
                bitMap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.close();
                List<String> list = new ArrayList<>(readFromFile());
                String temp = "";
                for (int i = 0; i < list.size(); i++){
                    if (list.get(i).split(",")[6].split("\\.")[0].split("_")[0].equals("track") && list.get(i).split(",")[6].split("\\.")[0].split("_").length > 1 && Integer.parseInt(list.get(i).split(",")[6].split("\\.")[0].split("_")[1]) == idd){
                        Date currentTime = Calendar.getInstance().getTime();
                        temp += output[0] + "," + output[1] + "," + output[2] + "," + output[3] + "," + output[4] + "," + output[5] + "," + name + "," + currentTime + "," + selected + "," + opttext;
                        if (i != list.size() - 1){
                            temp += "\n";
                        }
                    } else {
                        temp += list.get(i);
                        if (i != list.size() - 1){
                            temp += "\n";
                        }
                    }
                }
                writeToFile(temp);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("frag", "track");
        startActivity(intent);
        finish();

    }

    private int savedCount(){
        File temp = new File(this.getFilesDir().getPath());
        int c = 0;
        Set<Integer> set = new HashSet<>();
        for (File file: temp.listFiles()){
            if (file.getName().split("\\.")[0].split("_")[0].equals("track")){
                c++;  //his_1
                set.add(Integer.valueOf(file.getName().split("\\.")[0].split("_")[1]));
            }
        }
        for (int i = 0; i < c; i++){
            if (!set.contains(i)){
                c = i;
                break;
            }
        }
        return c;
    }

    private void writeToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("config_track.txt", MODE_PRIVATE));
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
            InputStream inputStream = openFileInput("config_track.txt");
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

    private float[][][][] bitmapToInputArray(Bitmap oldbitmap) {
        Bitmap bitmap= oldbitmap;
        bitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
        int batchNum = 0;
        float[][][][] input = new float[1][200][200][3];
        for (int x = 0; x < 200; x++) {
            for (int y = 0; y < 200; y++) {
                int pixel = bitmap.getPixel(x, y);
                input[batchNum][x][y][0] = (Color.red(pixel) - 127) / 128.0f;
                input[batchNum][x][y][1] = (Color.green(pixel) - 127) / 128.0f;
                input[batchNum][x][y][2] = (Color.blue(pixel) - 127) / 128.0f;

            }
        }
        return input;
    }

    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = getAssets().openFd("degree.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel=inputStream.getChannel();
        long startOffset=fileDescriptor.getStartOffset();
        long declareLength=fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startOffset,declareLength);
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    public static Bitmap cropCenter(Bitmap img) {
        Bitmap retImg = img.copy(img.getConfig(), true);
        if (retImg.getHeight() > retImg.getWidth()){
            retImg = Bitmap.createBitmap(retImg, 0, (retImg.getHeight() - retImg.getWidth()) / 2, retImg.getWidth(), retImg.getWidth());
        } else if (retImg.getHeight() < retImg.getWidth()){
            retImg = Bitmap.createBitmap(retImg,(retImg.getWidth() - retImg.getHeight()) / 2, 0, retImg.getHeight(), retImg.getHeight());
        }
        return retImg;
    }
}