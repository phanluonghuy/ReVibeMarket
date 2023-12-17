package com.example.revibemarket.Models;

import java.util.UUID;

public class ShippingInfo {
    private String shippingID;
    private String address;
    private String carrier;
    private String tracking;

    public ShippingInfo() {
    }

    public ShippingInfo(String shippingID, String address, String carrier, String tracking) {
        this.shippingID = shippingID;
        this.address = address;
        this.carrier = carrier;
        this.tracking = tracking;
    }

    public String getShippingID() {
        return shippingID;
    }

    public void setShippingID(String shippingID) {
        this.shippingID = shippingID;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getTracking() {
        return tracking;
    }

    public void setTracking(String tracking) {
        this.tracking = tracking;
    }
}
