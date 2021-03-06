package com.darragh.musicalnotepad.Login_Register;

import java.util.HashMap;
import java.util.Objects;

public class User {

    public String username,email,profilePhoto;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public HashMap<String,Object> toMap(){
        HashMap<String,Object> map = new HashMap<>();
        map.put("username",username);
        map.put("email",email);
        return map;
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

}