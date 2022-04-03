package com.example.skinsafe;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import static com.example.skinsafe.MainActivity.REQUEST_IMAGE_CAPTURE;
import static com.example.skinsafe.ScanFragment.PICK_IMAGE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import java.util.List;
import java.util.Map;

public class TrackFragment extends Fragment {
    private Button button;
    private ListView list;
    private ArrayList<String> textMatch, textDate, fileName;
    ArrayList<Bitmap> imgs;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_track, container, false);
        button = rootView.findViewById(R.id.buttonAdd);
        list = (ListView) rootView.findViewById(R.id.list_2);
        imgs = new ArrayList<>();
        textMatch = new ArrayList<>();
        textDate = new ArrayList<>();
        fileName = new ArrayList<>();
        File temp = new File(getContext().getFilesDir().getPath());

        for (File file: temp.listFiles()){
            if (file.getName().split("\\.")[0].split("_")[0].equals("track") && file.getName().split("\\.")[0].split("_").length > 1){
                Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
                imgs.add(bitmap);  //his_1
                List<String> tempList = new ArrayList<>(readFromFile());
                fileName.add(file.getName());
                for (int i = 0; i < tempList.size(); i++){
                    String[] dat = tempList.get(i).split(",");

                    if (dat[6].equals(file.getName())){
                        float[] fl = new float[6];
                        for (int j = 0; j < 6; j++){
                            fl[j] = Float.parseFloat(dat[j]);
                        }
                        String match = "";
                        Map<Float, String> digMap = new HashMap<>();
                        digMap.put(fl[0], "Actinic Keratosis");
                        digMap.put(fl[1], "Basal Cell Carcinoma");
                        digMap.put(fl[2], "Melanoma");
                        digMap.put(fl[3], "Nevus");
                        digMap.put(fl[4], "Seborrheic Keratosis");
                        digMap.put(fl[5], "Squamous Cell Carcinoma");
                        List<Float> list = new ArrayList<>();
                        list.addAll(digMap.keySet());
                        Collections.sort(list, null);
                        match += "It probably was " + digMap.get((list.get(list.size() - 1)))+ "\nPlace: " + dat[8];
                        textDate.add(dat[7]);
                        textMatch.add(match);
                        break;
                    }
                }
            }
        }

        list.setAdapter(new listAdapter(textMatch, textDate, imgs, this.getContext(), fileName));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AdditionActivity.class);
                //intent.putExtra("output", out);
                //intent.putExtra("imageBitmap", imageBitmap);
                startActivity(intent);

            }
        });
        return rootView;
    }
    private List<String> readFromFile() {
        List<String> ret = new ArrayList<>();
        try {
            InputStream inputStream = getContext().openFileInput("config_track.txt");
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


    class listAdapter extends BaseAdapter {
        ArrayList<String> textMatch, textDate, fileName;
        Context context;
        LayoutInflater inflater;
        ArrayList<Bitmap> imgs;

        public listAdapter() {
            textMatch = null;
            textDate = null;
            imgs = null;
        }

        public listAdapter(ArrayList<String> textViewMatch, ArrayList<String> textViewDate, ArrayList<Bitmap> imgs, Context context, ArrayList<String> fileName) {
            this.textMatch = textViewMatch;
            this.fileName = fileName;
            this.textDate = textViewDate;
            this.context = context;
            this.imgs = imgs;
        }

        public int getCount() {
            // TODO Auto-generated method stub
            return imgs.size();
        }

        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row;
            row = inflater.inflate(R.layout.track_list, parent, false);
            TextView textViewMatch, textViewDate;
            ;
            ImageView imgView, deleteView, changeView;
            imgView = (ImageView) row.findViewById(R.id.imgViewShow);
            textViewMatch = (TextView) row.findViewById(R.id.textViewMatch);
            textViewDate = (TextView) row.findViewById(R.id.textViewDate);
            deleteView = (ImageView) row.findViewById(R.id.imageViewDelete);
            changeView = (ImageView) row.findViewById(R.id.imageViewEdit);
            imgView.setImageBitmap(imgs.get(position));
            textViewMatch.setText(textMatch.get(position));
            textViewDate.setText(textDate.get(position));
            changeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), AdditionActivity.class);
                    intent.putExtra("ID", fileName.get(position).split("\\.")[0].split("_")[1]);
                    //intent.putExtra("imageBitmap", imageBitmap);
                    startActivity(intent);
                    notifyDataSetChanged();
                }
            });
            deleteView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteOne(fileName.get(position));
                    fileName.remove(position);
                    imgs.remove(position);
                    textMatch.remove(position);
                    textDate.remove(position);
                    notifyDataSetInvalidated();
                }
            });
            return (row);
        }
    }
        private void deleteOne(String fileName){
            File temp = new File(getContext().getFilesDir().getPath());
            for (File i: temp.listFiles()){
                if (i.getName().equals(fileName)){
                    i.delete();
                }
            }
            List<String> list = readFromFile();
            for (int i = 0; i < list.size(); i++){
                if (list.get(i).split(",")[6].equals(fileName)){
                    list.remove(i);
                }
            }
            String tempp = "";
            for (int i = 0; i < list.size(); i++){
                tempp += list.get(i) + "\n";
            }
            writeToFile(tempp);
        }
        public void writeToFile(String data) {
            try {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getContext().openFileOutput("config_track.txt", MODE_PRIVATE));
                outputStreamWriter.write(data);
                outputStreamWriter.close();
            }
            catch (IOException e) {
                Log.e("Exception", "File write failed: " + e.toString());
            }
        }



}
