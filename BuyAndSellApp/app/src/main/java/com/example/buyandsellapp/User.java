package com.example.buyandsellapp;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;
// [START blog_user_class]

@IgnoreExtraProperties
public class User {

    public String username;
    public String email;
    public String fullName;
    public String district;
    public Date dateOfBirth;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email, String fullName, String district, Date dateOfBirth) {
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.district = district;
        this.dateOfBirth = dateOfBirth;
    }
}