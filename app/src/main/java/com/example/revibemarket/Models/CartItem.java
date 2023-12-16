package com.example.revibemarket.Models;

import com.example.revibemarket.ModelsSingleton.ProductSingleton;
import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartItem {
    private String itemId;
    private String productName;
    private Double price;
    private Double discount;
    private int quantity;
    private String currentUserID;

    private  String SellerID;

    public CartItem() {
    }

    public CartItem(String itemId, String productName, Double price, Double discount, int quantity, String currentUserID, String SellerID) {
        this.itemId = itemId;
        this.productName = productName;
        this.price = price;
        this.discount = discount;
        this.quantity = quantity;
        this.currentUserID = currentUserID;
        this.SellerID = SellerID;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getSellerID() {
        return SellerID;
    }

    public void setSellerID(String sellerID) {
        SellerID = sellerID;
    }

    public String getItemId() {
        return itemId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Double getPrice() {
        return price;
    }

    public Double getDiscount() {
        return discount;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getCurrentUserID() {
        return currentUserID;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setCurrentUserID(String currentUserID) {
        this.currentUserID = currentUserID;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }
}
