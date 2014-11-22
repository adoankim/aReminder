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

package com.hodor.company.areminder.model;

import com.hodor.company.areminder.R;

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
