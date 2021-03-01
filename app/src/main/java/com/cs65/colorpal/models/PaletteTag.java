package com.cs65.colorpal.models;

import androidx.palette.graphics.Palette;

public class PaletteTag {
    private String text;

    public PaletteTag(String text){
        this.text = text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
