package com.example.shopping;

import android.graphics.drawable.Drawable;

class items {
    String price, name, image;

    public items(String price, String name, String  image) {
        this.price = price;
        this.name = name;
        this.image = image;
    }

    public items() {
    }


    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
