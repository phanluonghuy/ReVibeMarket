package com.example.revibemarket.Models;

import java.util.List;

public class Order {
    private String customerId;
    private String paymentMethod;
    private String paymentStatus;
    private String date;
    private String status;
    private double totalCost;
    private List<OrderItem> items;
    private ShippingInfo shipping;

    public Order() {
        // Default constructor required for Firebase
    }

    public Order(String customerId, String paymentMethod, String paymentStatus, String date, String status, double totalCost, List<OrderItem> items, ShippingInfo shipping) {
        this.customerId = customerId;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.date = date;
        this.status = status;
        this.totalCost = totalCost;
        this.items = items;
        this.shipping = shipping;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public ShippingInfo getShipping() {
        return shipping;
    }

    public void setShipping(ShippingInfo shipping) {
        this.shipping = shipping;
    }
}
