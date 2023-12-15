// CartItemAdapter.java
package com.example.revibemarket.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.revibemarket.Models.CartItem;
import com.example.revibemarket.R;

import java.util.List;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder> {

    private List<CartItem> cartItemList;

    public CartItemAdapter(List<CartItem> cartItemList) {
        this.cartItemList = cartItemList;
    }

    @NonNull
    @Override
    public CartItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout, parent, false);
        return new CartItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemViewHolder holder, int position) {
        CartItem cartItem = cartItemList.get(position);

        if (holder.productNameTextView == null || holder.quantityTextView == null || holder.Price == null) {
            return;
        }

        // Set the text values
        holder.productNameTextView.setText(cartItem != null ? cartItem.getProductName() : "N/A");
        holder.quantityTextView.setText(cartItem != null ? String.valueOf(cartItem.getQuantity()) : "0");
        holder.Price.setText(cartItem != null ? String.valueOf(cartItem.getPrice()) : "0");
        assert cartItem != null;
        holder.bindData(cartItem);
    }

    @Override
    public int getItemCount() {
        Log.d("CartItemAdapter", "getItemCount: " + cartItemList.size());
        return cartItemList.size();
    }


    public static class CartItemViewHolder extends RecyclerView.ViewHolder {
        TextView productNameTextView;
        TextView Price;
        TextView quantityTextView;

        public CartItemViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.tvProductName);
            Price = itemView.findViewById(R.id.tvPrice);
            quantityTextView = itemView.findViewById(R.id.tvProductQuantity);
        }

        public void bindData(CartItem cartItem) {
            if (cartItem.getProductName() != null) {
                productNameTextView.setText(cartItem.getProductName());
                quantityTextView.setText(String.valueOf(cartItem.getQuantity()));
            } else {
                productNameTextView.setText("N/A");
                quantityTextView.setText("0");
            }
        }
    }
}
