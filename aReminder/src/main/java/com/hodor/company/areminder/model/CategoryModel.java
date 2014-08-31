package com.hodor.company.areminder.model;

import com.hodor.company.areminder.R;

/**
 * Created by toni on 31/08/14.
 */
public class CategoryModel {
    private int icon = R.drawable.ic_launcher;
    private String name;

    public CategoryModel(String name) {
        this.name = name;
    }

    public CategoryModel(String name, int icon) {
        this.icon = icon;
        this.name = name;
    }


    public String getName() {
        return this.name;
    }

    public int getIcon() {
        return this.icon;
    }
}
