package com.example.revibemarket.Models;

public class ShippingInfo {
    private String address;
    private String origin;
    private String carrier;
    private String tracking;

    public ShippingInfo() {
    }

    public ShippingInfo(String address, String origin, String carrier, String tracking) {
        this.address = address;
        this.origin = origin;
        this.carrier = carrier;
        this.tracking = tracking;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
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
