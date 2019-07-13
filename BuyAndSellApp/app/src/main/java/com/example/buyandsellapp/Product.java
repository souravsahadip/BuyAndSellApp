package com.example.buyandsellapp;

public class Product {
    private double price;
    private String productName;
    private String condition;
    private String uid;
    private  String category;

    public String getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public String getCondition() {
        return condition;
    }

    public String getUid() {
        return uid;
    }

    public String getProductName() {
        return productName;
    }

    public Product() {
    }

    public Product( String productName,double price, String condition, String uid, String category) {
        this.price = price;
        this.productName = productName;
        this.condition = condition;
        this.uid = uid;
        this.category = category;
    }
}
