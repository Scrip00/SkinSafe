package com.skinsafe.skinsafe.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.skinsafe.skinsafe.Database.HistoryDatabaseClass;
import com.skinsafe.skinsafe.R;
import com.skinsafe.skinsafe.ResultActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryListAdapter extends BaseAdapter {
    private final Context context;
    private final ArrayList<String> time;
    private final ArrayList<Bitmap> image;
    private final ArrayList<float[]> result;
    private final ArrayList<Integer> keys;

    public HistoryListAdapter(Context context, ArrayList<Bitmap> image, ArrayList<String> time, ArrayList<float[]> result, ArrayList<Integer> keys) {
        Collections.reverse(image);
        Collections.reverse(time);
        Collections.reverse(result);
        Collections.reverse(keys);
        this.context = context;
        this.image = image;
        this.time = time;
        this.result = result;
        this.keys = keys;
    }

    public int getCount() {
        return image.size();
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

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.history_list, parent, false);
        TextView textViewMatch, textViewDate;
        ImageView imgView, deleteView;
        imgView = row.findViewById(R.id.imgViewShow);
        textViewMatch = row.findViewById(R.id.textViewMatch);
        textViewDate = row.findViewById(R.id.textViewDate);
        deleteView = row.findViewById(R.id.imageViewDelete);
        imgView.setImageBitmap(image.get(position));

        String match = "";
        Map<Float, String> digMap = new HashMap<> ();
        digMap.put(result.get(position)[0], "Actinic Keratosis");
        digMap.put(result.get(position)[1], "Basal Cell Carcinoma");
        digMap.put(result.get(position)[2], "Melanoma");
        digMap.put(result.get(position)[3], "Nevus");
        digMap.put(result.get(position)[4], "Seborrheic Keratosis");
        digMap.put(result.get(position)[5], "Squamous Cell Carcinoma");
        List<Float> list = new ArrayList<> ();
        list.addAll(digMap.keySet());
        Collections.sort(list, null);
        match += "It probably was " + digMap.get(list.get(list.size() - 1)) + "\n\nClick here to see details";

        textViewMatch.setText(match);
        textViewDate.setText(time.get(position));
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ResultActivity.class);
                intent.putExtra("output", result.get(position));
                intent.putExtra("imageBitmap", image.get(position));
                intent.putExtra("saveOrNot", false);
                context.startActivity(intent);
            }
        });
        deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HistoryDatabaseClass.getDatabase(context).getDao().deleteData(keys.get(position));
                image.remove(position);
                time.remove(position);
                result.remove(position);
                keys.remove(position);
                notifyDataSetInvalidated();
            }
        });
        return (row);
    }
}