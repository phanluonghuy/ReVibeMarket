package com.example.revibemarket.Models;

public class OrderItem {

    private String sellerID;
    private String sku;
    private int quantity;
    private String productTitle;
    private double price;
    private double discount;
    private double total;

    public OrderItem() {
    }

    public OrderItem(String sellerID, String sku, int quantity, String productTitle, double price, double discount, double total) {
        this.sellerID = sellerID;
        this.sku = sku;
        this.quantity = quantity;
        this.productTitle = productTitle;
        this.price = price;
        this.discount = discount;
        this.total = total;
    }


    public String getSellerID() {
        return sellerID;
    }

    public void setSellerID(String sellerID) {
        this.sellerID = sellerID;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
