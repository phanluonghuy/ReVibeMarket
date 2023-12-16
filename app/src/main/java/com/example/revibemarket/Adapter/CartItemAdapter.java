package com.example.revibemarket.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.revibemarket.Models.CartItem;
import com.example.revibemarket.ModelsSingleton.CartSession;
import com.example.revibemarket.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.List;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder> {
    private CartSession cartSession;

    public CartItemAdapter() {
        cartSession = CartSession.getInstance();

    }

    @NonNull
    @Override
    public CartItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout, parent, false);
        return new CartItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemViewHolder holder, int position) {
        CartItem cartItem = cartSession.getCartItemList().get(position);
        holder.tvPrice.setText(cartItem.getPrice() + " $");
        holder.productNameTextView.setText(cartItem.getProductName());
        holder.tvProductQuantity.setText("Quantity : " + cartItem.getQuantity());
        Glide.with(holder.imageView.getContext())
                .load(cartSession.getImagesUrl().get(position))
                .error(R.drawable.sofa_cut)
                .override(330, 330)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return cartSession.getCartItemList().size();
    }

    public class CartItemViewHolder extends RecyclerView.ViewHolder {
        TextView productNameTextView;
        TextView tvPrice;
        TextView tvProductQuantity;
        Button btnRemove;
        ImageView imageView;

        public CartItemViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvProductQuantity = itemView.findViewById(R.id.tvQuantity);
            btnRemove = itemView.findViewById(R.id.btnRemove);
            imageView = itemView.findViewById(R.id.imgProduct);

            btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cartSession.getCartItemList().remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());

                    SharedPreferences sharedPreferences = itemView.getContext().getSharedPreferences("cart_session", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    Gson gson = new Gson();
                    String cartItemsJson = gson.toJson(CartSession.getInstance().getCartItemList());
                    String imagesUrlJson = gson.toJson(CartSession.getInstance().getImagesUrl());
                    editor.putString("cart_items", cartItemsJson);
                    editor.putString("images_url", imagesUrlJson);
                    editor.apply();
                }
            });
        }
    }
}
