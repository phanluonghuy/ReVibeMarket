package com.example.revibemarket.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.revibemarket.Models.CartItem;
import com.example.revibemarket.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder> {

    private final List<CartItem> cartItemList;
    private OnRemoveItemClickListener onRemoveItemClickListener;

    public CartItemAdapter(List<CartItem> cartItemList, OnRemoveItemClickListener listener) {
        this.cartItemList = cartItemList;
        this.onRemoveItemClickListener = listener;
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

        if (cartItem != null) {
            if (holder.tvPrice != null) {
                holder.tvPrice.setText(String.valueOf(cartItem.getPrice() + " $"));
            }

            if (holder.productNameTextView != null) {
                holder.productNameTextView.setText(cartItem.getProductName());
            }

            if (holder.tvProductQuantity != null) {
                holder.tvProductQuantity.setText(String.valueOf(cartItem.getQuantity()));
            }

//            holder.btnRemove.setOnClickListener(v -> {
//                if (onRemoveItemClickListener != null) {
//                    onRemoveItemClickListener.onRemoveItemClick(position, cartItem.getItemId());
//
//                    DatabaseReference cartItemRef = FirebaseDatabase.getInstance().getReference()
//                            .child("carts")
//                            .child(cartItem.getItemId());
//
//                    cartItemRef.removeValue()
//                            .addOnCompleteListener(task -> {
//                                if (task.isSuccessful()) {
//
//                                    ((Activity) holder.itemView.getContext()).runOnUiThread(() -> {
//                                        cartItemList.remove(position);
//                                        notifyItemRemoved(position);
//                                        notifyItemRangeChanged(position, cartItemList.size());
//                                    });
//                                } else {
//
//                                }
//                            });
//                }
//            });
        }
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public interface OnRemoveItemClickListener {
        void onRemoveItemClick(int position, String itemId);
    }

    public void setOnRemoveItemClickListener(OnRemoveItemClickListener listener) {
        this.onRemoveItemClickListener = listener;
    }

    public class CartItemViewHolder extends RecyclerView.ViewHolder {
        TextView productNameTextView;
        TextView tvPrice;
        TextView tvProductQuantity;
        Button btnRemove;

        public CartItemViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvProductQuantity = itemView.findViewById(R.id.tvProductQuantity);
            btnRemove = itemView.findViewById(R.id.btnRemove);

            btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cartItemList.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                }
            });
        }
    }
}
