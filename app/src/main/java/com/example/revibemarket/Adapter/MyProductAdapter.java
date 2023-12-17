package com.example.revibemarket.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.revibemarket.Models.Product;
import com.example.revibemarket.ModelsSingleton.CartSession;
import com.example.revibemarket.ModelsSingleton.ProductSingleton;
import com.example.revibemarket.ModelsSingleton.UserSession;
import com.example.revibemarket.R;

import java.util.List;
import java.util.stream.Collectors;

public class MyProductAdapter extends RecyclerView.Adapter<MyProductAdapter.ViewHolder> {
    private List<Product> productList;

    public MyProductAdapter() {
        this.productList = ProductSingleton.getInstance().getProductList()
                .stream()
                .filter(product -> product.getUserID().equals(UserSession.getInstance().getId()))
                .collect(Collectors.toList());

    }
    @NonNull
    @Override
    public MyProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item_layout, parent, false);
        return new MyProductAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyProductAdapter.ViewHolder holder, int position) {
            holder.productNameTextView.setText(productList.get(position).getProductName());
            Glide.with(holder.imageView.getContext())
                .load(productList.get(position).getProductType().getImages().get(0))
                .error(R.drawable.sofa_cut)
                .override(330, 330)
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView productNameTextView;
        TextView tvPrice,textViewDiscount,tvPriceAfter;
        TextView tvProductQuantity;
        Button btnRemove;
        ImageView imageView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            imageView = itemView.findViewById(R.id.imgProduct);
        }
    }
}
