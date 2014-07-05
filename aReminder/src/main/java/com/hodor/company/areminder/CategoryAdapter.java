package com.hodor.company.areminder;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by toni on 5/07/14.
 */
public class CategoryAdapter extends ArrayAdapter<int[]> {

    private List<int[]> mCategories;

    public CategoryAdapter(Context context, List<int[]> values) {
        super(context, R.layout.category_item, values);
        this.mCategories = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if(convertView==null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.category_item, parent, false);
        }

        ((TextView)rowView.findViewById(R.id.text)).setText(this.mCategories.get(position)[0]);
        ((ImageView)rowView.findViewById(R.id.picture)).setImageResource(this.mCategories.get(position)[1]);
        if (position>0) {
            ((ImageView) rowView.findViewById(R.id.picture)).setColorFilter(Utils.getGrayScaleFilter());
            rowView.setAlpha(0.4f);
        }

        return rowView;
    }

    @Override
    public long getItemId(int position) {
        return this.mCategories.get(position)[0];
    }
}
