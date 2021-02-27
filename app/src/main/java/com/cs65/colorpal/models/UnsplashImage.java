package com.cs65.colorpal.models;

public class UnsplashImage {
    private String description;
    private String image_view_url;
    private String image_download_url;
    private String nameOfUser;

    public UnsplashImage(String nameOfUser, String description, String image_view_url, String image_download_url){
        this.description = description;
        this.image_view_url =  image_view_url;
        this.image_download_url = image_download_url;
        this.nameOfUser = nameOfUser;
    }

    public String getDescription() {
        return description;
    }

    public String getNameOfUser() {
        return nameOfUser;
    }

    public String getImage_view_url() {
        return image_view_url;
    }

    public String getImage_download_url() {
        return image_download_url;
    }
}