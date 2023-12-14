package com.example.revibemarket.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.revibemarket.Models.Product;
import com.example.revibemarket.R;

import java.util.List;

public class BestDealAdapter extends RecyclerView.Adapter<BestDealAdapter.ViewHolder> {
    private List<Product> productList;
    private OnItemClickListener onItemClickListener;

    public BestDealAdapter(Context context, List<Product> productList) {
        this.productList = productList;
    }

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_bestdeal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product item = productList.get(position);

        holder.titleTextView.setText(item.getProductName());
        holder.priceTextView.setText(String.valueOf(item.getProductType().getPrice() + " $"));

        List<String> imageUrls = item.getProductType().getImages();

        if (imageUrls != null && !imageUrls.isEmpty()) {
            String imageUrl = imageUrls.get(0);
            Glide.with(holder.imageView.getContext())
                    .load(imageUrl)
                    .into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.sofa_cut);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void updateData(List<Product> newItems) {
        productList.clear();
        productList.addAll(newItems);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView priceTextView;
        public ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.BestDealTiTle);
            priceTextView = itemView.findViewById(R.id.BestDealPrice);
            imageView = itemView.findViewById(R.id.BestDealImg);
        }
    }
}
