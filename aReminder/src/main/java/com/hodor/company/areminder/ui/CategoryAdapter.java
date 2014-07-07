/**
 aReminder - an Android + Google wear application test for I/O 2014

 Copyright (C) 2014  Toni Martinez / Adam Doan Kim

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.

 */
package com.hodor.company.areminder.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hodor.company.areminder.R;

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
