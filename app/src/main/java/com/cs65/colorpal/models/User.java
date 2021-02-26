package com.cs65.colorpal.models;

import android.net.Uri;
import java.io.Serializable;

public class User implements Serializable {
    private String uid;
    private String name;
    private String email;
    private Uri profileImage;

    public User(String name, String email, String uid, Uri image){
        this.name = name;
        this.email = email;
        this.uid = uid;
        this.profileImage = image;
    }

    public String getUid(){
        return uid;
    }

    public String getName(){
        return name;
    }

    public String getEmail(){
        return email;
    }

    public Uri getImage() { return profileImage;}
}
