package com.cs65.colorpal.models;

import android.graphics.Bitmap;

public class ColorPalette {
    private String imageUrl;
    private Bitmap bitmap;

    public void setBitmap (Bitmap bitmap) { this.bitmap = bitmap; }
    public Bitmap getBitmap(){
        return bitmap;
    }

    public void setImageUrl (String imageUrl) { this.imageUrl = imageUrl; }
    public String getImageUrl(){
        return imageUrl;
    }

}
