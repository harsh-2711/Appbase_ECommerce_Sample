package com.beingdev.magicprint.models;

public class SearchItemModel {

    String item;
    String image;
    String description;
    float price;

    public SearchItemModel(String item) {
        this.item = item;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }
}