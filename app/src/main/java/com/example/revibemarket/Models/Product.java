package com.example.revibemarket.Models;

import java.util.ArrayList;
import java.util.Date;

public class Product {

    private String category;
    private String sku;
    private String productName;
    private String productTitle;
    private String UserID;
    private Product_Type productType;
    private Date created;
    private ArrayList<String> channels;

    public Product() {
    }

    public Product(String productName, String productTitle,Product_Type product_type, String category, Date created, ArrayList<String> channels, String sku, String UserID) {
        this.productName = productName;
        this.productTitle = productTitle;
        this.productType = product_type;
        this.category = category;
        this.created = created;
        this.channels = channels;
        this.sku = sku;
        this.UserID = UserID;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public Product_Type getProductType() {
        return productType;
    }

    public void setProductType(Product_Type productType) {
        this.productType = productType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public ArrayList<String> getChannels() {
        return channels;
    }

    public void setChannels(ArrayList<String> channels) {
        this.channels = channels;
    }
}