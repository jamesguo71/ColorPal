package com.cs65.colorpal.models;

import java.io.Serializable;

public class User implements Serializable {
    private String uid;
    private String name;
    private String email;

    public User(String name, String email, String uid){
        this.name = name;
        this.email = email;
        this.uid = uid;
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
}
