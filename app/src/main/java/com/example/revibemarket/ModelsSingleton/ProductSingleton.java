package com.example.revibemarket.ModelsSingleton;

import com.example.revibemarket.Models.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductSingleton {
    private static ProductSingleton instance;
    private List<Product> productList;

    private ProductSingleton() {
        // Initialize the productList
        productList = new ArrayList<>();
    }

    public static synchronized ProductSingleton getInstance() {
        if (instance == null) {
            instance = new ProductSingleton();
        }
        return instance;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

}
