package com.example.myapplication.Models;

public class Model {
    int id;
    String title;
    double price;
    String units;
    double gram_price;
    byte [] image;
    int value;


    public Model(int id, String title, double price, String units, double gram_price, byte[] image, int value) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.units = units;
        this.gram_price = gram_price;
        this.value = value;
        this.image = image;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPrice() {
        return price;
    }

    public double getGram_price() {
        return gram_price;
    }

    public byte[] getImage() {
        return image;
    }

    public int getValue() {
        return value;
    }

    public String getUnits() {
        return units;
    }

}