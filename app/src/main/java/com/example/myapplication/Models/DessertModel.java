package com.example.myapplication.Models;

public class DessertModel {
    int id;
    String title;
    double sum;
    double weight;
    double new_dessert_width;
    double portion;
    double portion_size;
    double portion_price;
    byte [] image;
    private boolean expanded;
    int desserts;
    double coefficient;
    double new_dessert_height;
    double dessert_height;
    double dessert_width;
    String shape_name;

    String new_shape_name;



    public DessertModel(int id, String title, double sum, double weight, byte[] image, double new_dessert_width, double portion, double portion_size, double portion_price, int desserts, double coefficient, double new_dessert_height, double dessert_height, double dessert_width, String shape_name, String new_shape_name){
        this.id = id;
        this.title = title;
        this.sum = sum;
        this.weight = weight;
        this.image = image;
        this.new_dessert_width = new_dessert_width;
        this.portion = portion;
        this.portion_size = portion_size;
        this.portion_price = portion_price;
        this.desserts = desserts;
        this.coefficient = coefficient;
        this.new_dessert_height = new_dessert_height;
        this.dessert_height = dessert_height;
        this.dessert_width = dessert_width;
        this.shape_name = shape_name;
        this.new_shape_name = new_shape_name;

    }
    public boolean isExpanded() {
        return expanded;
    }

    public int getDesserts() {
        return desserts;
    }

    public String getNew_shape_name() {
        return new_shape_name;
    }

    public String getShape_name() {
        return shape_name;
    }

    public double getNew_dessert_height() {
        return new_dessert_height;
    }

    public double getDessert_height() {
        return dessert_height;
    }

    public double getDessert_width() {
        return dessert_width;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
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

    public double getNew_dessert_width(){
        return new_dessert_width;
    }

    private boolean isDeleted;
    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public double getCoefficient() {
        return coefficient;
    }
}
