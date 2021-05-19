package com.example.nxtcommerce.firebase_model;

public class Products {
    private String name, description, image, pid;
    private long price;

    public Products() {

    }

    public Products(String name, String description, long price, String image, String pid) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

}
