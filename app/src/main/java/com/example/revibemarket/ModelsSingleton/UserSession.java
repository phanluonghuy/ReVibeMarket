package com.example.revibemarket.ModelsSingleton;

import android.graphics.Bitmap;

import com.example.revibemarket.Models.User;

public class UserSession extends User {
    private static UserSession instance;

    public static synchronized UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }
    private Bitmap image;

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public void setUser(User user) {
        this.setId(user.getId());
        this.setName(user.getName());
        this.setEmail(user.getEmail());
        this.setAddress(user.getAddress());
        this.setPhone(user.getPhone());
    }
}
