package com.example.revibemarket.ModelsSingleton;

import com.example.revibemarket.Models.CartItem;

import java.util.ArrayList;
import java.util.List;

public class CartSession {

    public static CartSession instance;
    private List<CartItem> cartItemList;
    private List<String> imagesUrl;
    private CartSession() {
        cartItemList = new ArrayList<>();
        imagesUrl = new ArrayList<>();
    }
    public static synchronized CartSession getInstance() {
        if (instance==null) {
            instance = new CartSession();
        }
        return instance;
    }
    public List<CartItem> getCartItemList() {
        return cartItemList;
    }
    public List<String> getImagesUrl() {return  imagesUrl;}

    public void setCartItemList(List<CartItem> cartItemList) {
        this.cartItemList = cartItemList;
    }

    public void setImagesUrl(List<String> imagesUrl) {
        this.imagesUrl = imagesUrl;
    }

    public void addCartItem(CartItem cartItem, String img) {
        cartItemList.add(cartItem);
        imagesUrl.add(img);
    }
    public void clearAllCartItem() {
        cartItemList.clear();
        imagesUrl.clear();

    }
    public void deleteCartItem(int position)
    {
        cartItemList.remove(position);
        imagesUrl.remove(position);
    }
}
