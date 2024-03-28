package com.example.myapplication.Models;

public class DessertModel {
    int id;
    String title;
    double sum;
    double weight;
    byte [] image;
    public DessertModel(int id, String title, double sum, double weight, byte[] image){
        this.id = id;
        this.title = title;
        this.sum = sum;
        this.weight = weight;
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

    public void setSum(double sum) {
        this.sum = sum;
    }

    public double getSum() {
        return sum;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

}
