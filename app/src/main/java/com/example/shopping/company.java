package com.example.shopping;

class company {
    String logo, name;

    public company(String logo, String name) {
        this.logo = logo;
        this.name = name;
    }

    public company() {
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
