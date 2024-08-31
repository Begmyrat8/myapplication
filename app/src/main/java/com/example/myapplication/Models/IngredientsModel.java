package com.example.myapplication.Models;

public class IngredientsModel {
    int id, dessert_id;
    String title;
    double price;
    double value, gram_price;
    int kg;



    public IngredientsModel(int id, String title, double price, double value, double gram_price, int kg, int dessert_id) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.value = value;
        this.gram_price = gram_price;
        this.kg = kg;
        this.dessert_id =dessert_id;

    }

    public int getId() {
        return id;
    }

    public int getDessert_id() {
        return dessert_id;
    }

    public void setDessert_id(int dessert_id) {
        this.dessert_id = dessert_id;
    }

    public double getGram_price() {
        return gram_price;
    }

    public double getValue() {
        return value;
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


    public void setValue(double value) {
        this.value = value;
    }

    public int getKg() {
        return kg;
    }

    public void setKg(int kg) {
        this.kg = kg;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}