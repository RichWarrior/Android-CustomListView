package com.faruksahin.customlistview;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Richwarrior on 4.03.2018.
 */

public class custom_ListView extends ArrayAdapter<String>
{
    private final ArrayList<Bitmap> photos;
    private final ArrayList<String> titles;
    private final ArrayList<String> Description;
    private final Activity context;
    public custom_ListView(ArrayList<Bitmap> photo, ArrayList<String> title,ArrayList<String> desc,Activity activity)
    {
        super(activity,R.layout.custom_listview,title);
        this.photos = photo;
        this.titles = title;
        this.Description = desc;
        this.context = activity;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        LayoutInflater infaleter = context.getLayoutInflater();
        View customView = infaleter.inflate(R.layout.custom_listview,null,true);
        ImageView imageView = (ImageView) customView.findViewById(R.id.imageView1);
        TextView txtTitle = (TextView) customView.findViewById(R.id.txtTitle);
        TextView txtDescription = (TextView) customView.findViewById(R.id.txtDescription);
        imageView.setImageBitmap(photos.get(position));
        txtTitle.setText(titles.get(position));
        txtDescription.setText(Description.get(position));
        return customView;
    }
}
