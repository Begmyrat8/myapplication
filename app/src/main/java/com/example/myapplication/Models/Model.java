package com.example.myapplication.Models;

public class Model {
    int id;
    String title;
    int kg;
    double price;
    int gram;
    double gram_price;
    byte [] image;
    int thing;


    public Model(int id, String title, int kg, double price, int gram, double gram_price, byte[] image, int thing) {
        this.id = id;
        this.title = title;
        this.kg = kg;
        this.price = price;
        this.gram = gram;
        this.gram_price = gram_price;
        this.thing = thing;
        this.image = image;

    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getKg() {
        return kg;
    }

    public void setKg(int kg) {
        this.kg = kg;
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

    public void setPrice(int price) {
        this.price = price;
    }

    public int getGram() {
        return gram;
    }

    public void setGram_price(double gram_price) {
        this.gram_price = gram_price;
    }

    public double getGram_price() {
        return gram_price;
    }

    public void setGram(int gram) {
        this.gram = gram;
    }


    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public void setThing(int thing) {
        this.thing = thing;
    }

    public int getThing() {
        return thing;
    }
}