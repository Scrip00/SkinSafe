package com.example.skinsafe;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HistoryFragment extends Fragment {
    ListView list;
    ArrayList<Bitmap> imgs;
    ArrayList<String> textMatch, textDate, fileName;
    ArrayList<float[]> dataNN;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //deleteAll();
        dataNN = new ArrayList<float[]>();
        imgs = new ArrayList<>();
        textMatch = new ArrayList<>();
        textDate = new ArrayList<>();
        fileName = new ArrayList<>();
        String nw = "";
        for (String s: readFromFile()){
            nw += s + "\n";
        }

        writeToFile(nw);
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        File temp = new File(getContext().getFilesDir().getPath());
        for (File file: temp.listFiles()){

            if (file.getName().split("\\.")[0].split("_")[0].equals("his") && file.getName().split("\\.")[0].split("_").length > 1){

                Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
                imgs.add(bitmap);  //his_1
                List<String> tempList = new ArrayList<>(readFromFile());

                Log.d("MyApp", String.valueOf(tempList.size()));
                fileName.add(file.getName());
                for (int i = 0; i < tempList.size(); i++){
                    String[] dat = tempList.get(i).split(",");
                    Log.d("MyApp", dat[6]);
                    if (dat[6].equals(file.getName())){
                        float[] fl = new float[6];
                        for (int j = 0; j < 6; j++){
                            fl[j] = Float.parseFloat(dat[j]);
                        }
                        dataNN.add(fl);
                        textDate.add(dat[7]);

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
                        match += "It probably was " + digMap.get(list.get(list.size() - 1)) + "\nClick here to see details";

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            list.sort(null);
                        }
                        DecimalFormat df = new DecimalFormat("#.##");
                        textMatch.add(match);
                        break;
                    }
                }
            }
        }
        Log.d("MyApp", String.valueOf(imgs.size()));

        list = (ListView) rootView.findViewById(R.id.list_1);
        list.setAdapter(new listAdapter(textMatch, textDate, imgs, getContext(), fileName));
        return rootView;
    }

    private void deleteAll(){
        File temp = new File(getContext().getFilesDir().getPath());
        for (File i: temp.listFiles()){
            i.delete();
        }
    }

    private List<String> readFromFile() {
        List<String> ret = new ArrayList<>();
        try {
            InputStream inputStream = getContext().openFileInput("config_history.txt");
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

        public listAdapter(ArrayList<String> textViewMatch, ArrayList<String> textViewDate, ArrayList<Bitmap> imgs,  Context context, ArrayList<String> fileName) {
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
            row = inflater.inflate(R.layout.history_list, parent, false);
            TextView textViewMatch, textViewDate;;
            ImageView imgView, deleteView;
            imgView = (ImageView) row.findViewById(R.id.imgViewShow);
            textViewMatch = (TextView) row.findViewById(R.id.textViewMatch);
            textViewDate = (TextView) row.findViewById(R.id.textViewDate);
            deleteView = (ImageView) row.findViewById(R.id.imageViewDelete);
            imgView.setImageBitmap(imgs.get(position));
            textViewMatch.setText(textMatch.get(position));
            textViewDate.setText(textDate.get(position));
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(getActivity(), ResultActivity.class);
                    intent.putExtra("output", dataNN.get(position));
                    intent.putExtra("imageBitmap", imgs.get(position));
                    intent.putExtra("saveOrNot", false);
                    startActivity(intent);

                }
            });
            deleteView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    deleteOne(fileName.get(position));
                    fileName.remove(position);
                    imgs.remove(position);
                    textMatch.remove(position);
                    textDate.remove(position);
                    dataNN.remove(position);
                    notifyDataSetInvalidated();
                }
            });
            return (row);
        }
    }
    private int savedCount(){ //мб не будет работать
        File temp = new File(getContext().getFilesDir().getPath());
        int c = 0;
        Set<Integer> set = new HashSet<>();
        for (File file: temp.listFiles()){
            Log.d("MyApp", file.getName());
            if (file.getName().split("\\.")[0].split("_")[0].equals("his")){
                c++;  //his_1
                set.add(Integer.valueOf(file.getName().split("\\.")[0].split("_")[1]));
            }
        }
        Log.d("MyApp", String.valueOf(c));
        for (int i = 0; i < c; i++){
            if (!set.contains(i)){
                c = i;
                break;
            }
        }
        return c;
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

    private void writeToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getContext().openFileOutput("config_history.txt", MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
    /*
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

        } catch (ActivityNotFoundException e) {
            // display error state to the user
        }
    }*/
}
