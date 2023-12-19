package com.example.revibemarket.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.revibemarket.Models.CartItem;
import com.example.revibemarket.Models.Product;
import com.example.revibemarket.ModelsSingleton.CartSession;
import com.example.revibemarket.ModelsSingleton.ProductSingleton;
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
        holder.tvPrice.setText("$"+cartItem.getPrice());
        holder.tvPrice.setPaintFlags(holder.tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


        holder.productNameTextView.setText(cartItem.getProductName());
        holder.tvProductQuantity.setText(cartItem.getQuantity()+"");
        holder.textViewDiscount.setText("-" + cartItem.getDiscount() + "%");
        holder.tvPriceAfter.setText("$" + String.format("%.2f",cartItem.getPrice()*((100-cartItem.getDiscount())/100)));
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

    public void clearCart() {
        cartSession.clearAllCartItem();
        notifyDataSetChanged();
    }

    public class CartItemViewHolder extends RecyclerView.ViewHolder {
        TextView productNameTextView;
        TextView tvPrice,textViewDiscount,tvPriceAfter;
        TextView tvProductQuantity;
        Button btnRemove;
        ImageView imageView,btnPlus,btnMinus;

        public CartItemViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.tv_cart_product_name);
            tvPrice = itemView.findViewById(R.id.tv_product_cart_price);
            tvPriceAfter = itemView.findViewById(R.id.tv_product_cart_price_total);
            tvProductQuantity = itemView.findViewById(R.id.tv_quantity);
            btnPlus = itemView.findViewById(R.id.img_plus);
            btnMinus = itemView.findViewById(R.id.img_minus);
            textViewDiscount = itemView.findViewById(R.id.tv_product_discount);
            imageView = itemView.findViewById(R.id.img_cart_product);

            btnPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int max_product = ProductSingleton.getInstance().getProductList()
                            .stream()
                            .filter(product -> product.getSku().equals(cartSession.getCartItemList().get(getAdapterPosition()).getItemId()))
                            .findFirst()
                            .get().getProductType().getStock();
                    int current_product = cartSession.getCartItemList().get(getAdapterPosition()).getQuantity();
                    if (current_product==max_product) {
                        Toast.makeText(v.getContext(),"Limit product",Toast.LENGTH_SHORT).show();
                    } else {
                        cartSession.getCartItemList().get(getAdapterPosition()).setQuantity(current_product + 1);
                        notifyDataSetChanged();
                        SharedPreferences sharedPreferences = itemView.getContext().getSharedPreferences("cart_session", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        Gson gson = new Gson();
                        String cartItemsJson = gson.toJson(CartSession.getInstance().getCartItemList());
                        String imagesUrlJson = gson.toJson(CartSession.getInstance().getImagesUrl());
                        editor.putString("cart_items", cartItemsJson);
                        editor.putString("images_url", imagesUrlJson);
                        editor.apply();
                    }
                }

            });
            btnMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    int current_product = cartSession.getCartItemList().get(getAdapterPosition()).getQuantity();
                    if (current_product==1) {
                        Toast.makeText(v.getContext(),position + "", Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());

                        builder.setMessage("Do you want to remove this product?");

                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                cartSession.deleteCartItem(getAdapterPosition());
                                notifyDataSetChanged();
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
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancels the dialog.
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else {
                        cartSession.getCartItemList().get(getAdapterPosition()).setQuantity(current_product - 1);
                        notifyDataSetChanged();
                        SharedPreferences sharedPreferences = itemView.getContext().getSharedPreferences("cart_session", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        Gson gson = new Gson();
                        String cartItemsJson = gson.toJson(CartSession.getInstance().getCartItemList());
                        String imagesUrlJson = gson.toJson(CartSession.getInstance().getImagesUrl());
                        editor.putString("cart_items", cartItemsJson);
                        editor.putString("images_url", imagesUrlJson);
                        editor.apply();
                    }
                }

            });

//            btnRemove.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    cartSession.getCartItemList().remove(getAdapterPosition());
//                    cartSession.getImagesUrl().remove(getAdapterPosition());
//                    notifyItemRemoved(getAdapterPosition());
//                    SharedPreferences sharedPreferences = itemView.getContext().getSharedPreferences("cart_session", Context.MODE_PRIVATE);
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    Gson gson = new Gson();
//                    String cartItemsJson = gson.toJson(CartSession.getInstance().getCartItemList());
//                    String imagesUrlJson = gson.toJson(CartSession.getInstance().getImagesUrl());
//                    editor.putString("cart_items", cartItemsJson);
//                    editor.putString("images_url", imagesUrlJson);
//                    editor.apply();
//                }
//            });

        }

    }

}