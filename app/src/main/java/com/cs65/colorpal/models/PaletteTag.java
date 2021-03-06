package com.cs65.colorpal.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class PaletteTag implements Parcelable {
    private String text;

    public PaletteTag(String text){
        this.text = text;
    }

    public PaletteTag(){}

    protected PaletteTag(Parcel in) {
        text = in.readString();
    }

    public static final Creator<PaletteTag> CREATOR = new Creator<PaletteTag>() {
        @Override
        public PaletteTag createFromParcel(Parcel in) {
            return new PaletteTag(in);
        }

        @Override
        public PaletteTag[] newArray(int size) {
            return new PaletteTag[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text);
    }
}
