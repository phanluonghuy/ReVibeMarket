package com.example.revibemarket.Models;

import java.util.ArrayList;
import java.util.Date;

public class Product_Type {
    private String sku;
    private boolean available;
    private int stock;
    private Double price;
    private Double discount;
    private String description;

    private ArrayList<String> images;
    private Date created;
    private Date updated;

    public Product_Type() {
    }

    public Product_Type(String sku, boolean available, int stock, Double price, Double discount, String description, ArrayList<String> images, Date created, Date updated) {
        this.sku = sku;
        this.available = available;
        this.stock = stock;
        this.price = price;
        this.discount = discount;
        this.description = description;
        this.images = images;
        this.created = created;
        this.updated = updated;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public Product_Type(boolean available, int stock, Double price, Double discount, String description, ArrayList<String> images, Date created, Date updated) {
        this.available = available;
        this.stock = stock;
        this.price = price;
        this.discount = discount;
        this.description = description;
        this.images = images;
        this.created = created;
        this.updated = updated;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }
}
