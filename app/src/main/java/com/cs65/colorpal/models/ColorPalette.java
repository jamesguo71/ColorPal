package com.cs65.colorpal.models;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

public class ColorPalette {
    private String imageUrl = "";
    private String username = "";
    private String userId = "";
    private Bitmap bitmap;
    private String downloadUrl = "";
    private String title = "";
    private String docId = "";
    private int privacy = 0;
    private ArrayList<Integer> swatches;
    private ArrayList<PaletteTag> tags;

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

    public String getuserId() {
        return userId;
    }

    public void setuserId(String userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public List<PaletteTag> getTags() {
        return tags;
    }

    public void setTags(ArrayList<PaletteTag> tags) {
        this.tags = tags;
    }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title=title; }

    public String getDocId() { return docId; }

    public void setDocId(String docId) { this.docId = docId; }

    public int getPrivacy() { return privacy; }

    public void setPrivacy(int privacy) { this.privacy=privacy; }

    @Override
    public String toString() {
        return "ColorPalette{" +
                "imageUrl='" + imageUrl + '\'' +
                ", username='" + username + '\'' +
                ", userId='" + userId + '\'' +
                ", bitmap=" + bitmap +
                ", swatches=" + swatches +
                ", tags=" + tags +
                ", title=" + title +
                ", docId=" + docId +
                '}';
    }
}
