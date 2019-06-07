package com.harsh.appbase.models;

import java.io.Serializable;
import java.util.ArrayList;

public class SearchItemModel implements Serializable {

    int id;
    String item;
    String image;
    String description;
    float price;
    ArrayList<String> tags, maxHits;

    public SearchItemModel(int id, String item, String image, String description, float price, ArrayList<String> tags,
                           ArrayList<String> maxHits) {
        this.id = id;
        this.item = item;
        this.image = image;
        this.description = description;
        this.price = price;
        this.tags = tags;
        this.maxHits = maxHits;
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

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public ArrayList<String> getMaxHits() {
        return maxHits;
    }

    public void setMaxHits(ArrayList<String> maxHits) {
        this.maxHits = maxHits;
    }
}
