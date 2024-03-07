package com.example.myapplication;

public class Model {
    int id;
    String title;
    int kg, price, gram, gram_price;


    public Model(int id, String title, int kg, int price, int gram, int gram_price) {
        this.id = id;
        this.title = title;
        this.kg = kg;
        this.price = price;
        this.gram = gram;
        this.gram_price = gram_price;

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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getGram() {
        return gram;
    }

    public void setGram_price(int gram_price) {
        this.gram_price = gram_price;
    }

    public int getGram_price() {
        return gram_price;
    }

    public void setGram(int gram_price) {
        this.gram_price = gram_price;
    }

}