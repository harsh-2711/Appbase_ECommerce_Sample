package com.beingdev.magicprint.models;

import java.io.Serializable;

public class SearchItemModel implements Serializable {

    int id;
    String item;
    String image;
    String description;
    float price;

    public SearchItemModel(int id, String item, String image, String description, float price) {
        this.id = id;
        this.item = item;
        this.image = image;
        this.description = description;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
