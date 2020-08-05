package com.example.shopping;

class transaction {
    String date, price, name, image, status;

    public transaction(String date, String price, String name, String image, String status) {
        this.date = date;
        this.price = price;
        this.name = name;
        this.image = image;
        this.status = status;
    }

    public transaction() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
