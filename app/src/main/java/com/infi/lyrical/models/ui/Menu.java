package com.infi.lyrical.models.ui;

import android.support.annotation.DrawableRes;

/**
 * Created by INFIi on 12/1/2017.
 */

public class Menu {
    private String name;
    private @DrawableRes int icon;
    private @DrawableRes int background;

    public Menu(String name, int icon, int background) {
        this.name = name;
        this.icon = icon;
        this.background = background;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getBackground() {
        return background;
    }

    public void setBackground(int background) {
        this.background = background;
    }
}
