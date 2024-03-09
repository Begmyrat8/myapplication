package com.example.myapplication;

public class Model {
    int id;
    String title;
    int kg;
    double price;
    int gram;
    double gram_price;
    double sum;
    int weight;


    public Model(int id, String title, int kg, double price, int gram, double gram_price, double sum, int weight) {
        this.id = id;
        this.title = title;
        this.kg = kg;
        this.price = price;
        this.gram = gram;
        this.gram_price = gram_price;
        this.sum = sum;
        this.weight = weight;

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

    public void setSum(double sum) {
        this.sum = sum;
    }

    public double getSum() {
        return sum;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

}