package com.example.myapplication;

public class CategoryModel {
    int id;
    String title;
    int kg, price;


    public CategoryModel(int id, String title, int kg, int price) {
        this.id = id;
        this.title = title;
        this.kg = kg;
        this.price = price;

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

}