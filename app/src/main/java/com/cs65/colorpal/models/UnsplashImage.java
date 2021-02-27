package com.cs65.colorpal.models;

public class UnsplashImage {
    private String description;
    private String url;
    private String nameOfUser;

    public UnsplashImage(String description, String url, String nameOfUser){
        this.description = description;
        this.url = url;
        this.nameOfUser = nameOfUser;
    }

    public String getDescription() {
        return description;
    }

    public String getNameOfUser() {
        return nameOfUser;
    }

    public String getUrl() {
        return url;
    }
}