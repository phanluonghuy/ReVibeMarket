package com.example.revibemarket.Models;

public class BestDealItem {
    private String title;
    private Double price;
    private String imageURL;
    private String sku;

    public BestDealItem() {
    }

    public BestDealItem(String title, Double price, String imageURL, String sku) {
        this.title = title;
        this.price = price;
        this.imageURL = imageURL;
        this.sku = sku;
    }

    public String getTitle() {
        return title;
    }

    public Double getPrice() {
        return price;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }
}
