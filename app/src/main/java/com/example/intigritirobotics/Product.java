package com.example.intigritirobotics;

public class Product {
    String id, title;
    int price;
    boolean in_stock;
    float rating;
    String pic_url;

    Product(String id, String title, int price, boolean in_stock) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.in_stock = in_stock;
    }

    Product() {

    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setIn_stock(boolean in_stock) {
        this.in_stock = in_stock;
    }

    public String getPic_url() {
        return pic_url;
    }

    public void setPic_url(String pic_url) {
        this.pic_url = pic_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}