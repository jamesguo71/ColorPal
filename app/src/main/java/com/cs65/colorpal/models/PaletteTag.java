package com.cs65.colorpal.models;

public class PaletteTag {
    private String text;

    public PaletteTag(String text){
        this.text = text;
    }

    public PaletteTag(){}

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "text='" + text +
                '}';
    }
}
