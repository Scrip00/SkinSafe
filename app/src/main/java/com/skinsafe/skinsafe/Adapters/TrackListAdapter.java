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

import com.skinsafe.skinsafe.Database.TrackDaoClass;
import com.skinsafe.skinsafe.Database.TrackDatabaseClass;
import com.skinsafe.skinsafe.Database.TrackModel;
import com.skinsafe.skinsafe.R;
import com.skinsafe.skinsafe.TrackDetailsActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrackListAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    ArrayList<Bitmap> image;
    ArrayList<String> time;
    ArrayList<float[]> result;
    ArrayList<String> place;
    ArrayList<String> name;
    ArrayList<Integer> next;
    ArrayList<Boolean> head;
    ArrayList<Integer> keys;
    Boolean isTrack;

    public TrackListAdapter(Context context, ArrayList<Bitmap> image, ArrayList<String> time, ArrayList<float[]> result, ArrayList<String> place, ArrayList<String> name, ArrayList<Integer> next, ArrayList<Boolean> head, ArrayList<Integer> keys, Boolean isTrack) {
        Collections.reverse(image);
        Collections.reverse(time);
        Collections.reverse(result);
        Collections.reverse(place);
        Collections.reverse(name);
        Collections.reverse(next);
        Collections.reverse(head);
        Collections.reverse(keys);
        this.context = context;
        this.image = image;
        this.time = time;
        this.result = result;
        this.place = place;
        this.name = name;
        this.next = next;
        this.head = head;
        this.keys = keys;
        this.isTrack = isTrack;
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
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                Intent intent = new Intent(context, TrackDetailsActivity.class);
                intent.putExtra("ID", keys.get(position));
                if (isTrack) intent.putExtra("list", true);
                context.startActivity(intent);
            }
        });
        deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrackDaoClass database = TrackDatabaseClass.getDatabase(context.getApplicationContext()).getDao();
                int curKey = keys.get(position);;
                if (isTrack) {
                    TrackModel model = database.loadSingle(curKey);
                    if (model.isHead()) {
                        if (model.getNext() != -1) {
                            database.updateHead(true, model.getNext());
                        }
                        database.deleteData(model.getKey());
                    } else if (model.getNext() == -1) {
                        database.updateTale(-1, database.findParent(model.getKey()).getKey());
                        database.deleteData(model.getKey());
                    } else {
                        int nextInt = model.getNext();
                        int prevInt = database.findParent(model.getKey()).getKey();
                        database.updateTale(nextInt, prevInt);
                        database.deleteData(model.getKey());
                    }
                } else {
                    curKey = getHead(curKey);
                    while (database.loadSingle(curKey).getNext() != -1) {
                        curKey = database.loadSingle(curKey).getNext();
                        database.deleteData(curKey);
                    }
                    database.deleteData(curKey);
                }
                image.remove(position);
                time.remove(position);
                result.remove(position);
                place.remove(position);
                name.remove(position);
                next.remove(position);
                head.remove(position);
                keys.remove(position);
                notifyDataSetInvalidated();
            }
        });
        return (row);
    }

    private int getHead(int id) {
        TrackDaoClass dao = TrackDatabaseClass.getDatabase(context).getDao();
        TrackModel model = dao.loadSingle(id);
        while(!model.isHead()) {
            model = dao.findParent(model.getKey());
        }
        return model.getKey();
    }
}
