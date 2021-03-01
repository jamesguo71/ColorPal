package com.cs65.colorpal.models;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class ColorPalette {
    private String imageUrl;
    private String username;
    private String userID;
    private Bitmap bitmap;
    private ArrayList<Integer> swatches;

    public void setBitmap (Bitmap bitmap) { this.bitmap = bitmap; }
    public Bitmap getBitmap(){
        return bitmap;
    }

    public void setImageUrl (String imageUrl) { this.imageUrl = imageUrl; }
    public String getImageUrl(){
        return imageUrl;
    }

    public void setSwatches(ArrayList<Integer> swatches){ this.swatches = swatches; }
    public ArrayList<Integer> getSwatches(){ return swatches; }

    public String getUsername() {
        return username;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "ColorPalette{" +
                "imageUrl='" + imageUrl + '\'' +
                ", username='" + username + '\'' +
                ", userID='" + userID + '\'' +
                ", bitmap=" + bitmap +
                ", swatches=" + swatches +
                '}';
    }
}
