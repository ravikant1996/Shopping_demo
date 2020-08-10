package com.example.shopping;

import android.graphics.drawable.Drawable;

class items {
    String company, name, image;
    long price;

    public items(String company, long price, String name, String image) {
        this.company = company;
        this.price = price;
        this.name = name;
        this.image = image;
    }

    public items() {
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }


    public String getImage() {
        return image;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
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
