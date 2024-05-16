package com.example.myapplication.Models;

public class DessertModel {
    int id;
    String title;
    double sum;
    double weight;
    double dessert_size;
    double portion;
    double portion_size;
    double portion_price;
    byte [] image;
    private boolean liked;
    public DessertModel(int id, String title, double sum, double weight, byte[] image, double dessert_size, double portion, double portion_size, double portion_price){
        this.id = id;
        this.title = title;
        this.sum = sum;
        this.weight = weight;
        this.image = image;
        this.dessert_size = dessert_size;
        this.portion = portion;
        this.portion_size = portion_size;
        this.portion_price = portion_price;

    }
    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
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

    public byte[] getImage() {
        return image;
    }

    public double getPortion(){
        return portion;
    }

    public double getPortion_size(){
        return portion_size;
    }

    public double getDessert_size(){
        return dessert_size;
    }

    private boolean isBookmarked;
    public boolean isBookmarked() {
        return isBookmarked;
    }

    public void setBookmarked(boolean bookmarked) {
        isBookmarked = bookmarked;
    }

}
