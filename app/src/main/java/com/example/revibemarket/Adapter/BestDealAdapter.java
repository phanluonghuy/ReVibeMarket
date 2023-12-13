package com.example.revibemarket.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.revibemarket.Models.BestDealItem;
import com.example.revibemarket.Models.Product;
import com.example.revibemarket.R;

import java.util.List;

public class BestDealAdapter extends RecyclerView.Adapter<BestDealAdapter.ViewHolder> {
    private List<Product> productList;

    public BestDealAdapter(Context context, List<Product> productList) {
        this.productList = productList;
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

//        Log.d("BestDealAdapter", "Binding item: " + item.getProductType().);

        holder.titleTextView.setText(item.getProductTitle());


        holder.priceTextView.setText(String.valueOf(item.getProductType().getPrice()));


//        Picasso.get().load(item.getImageURL()).into(holder.imageView);
//        Glide.with(holder.imageView.getContext())
//                .load(mImageUris.get(position))
//                .into(holder.imageView);
    }



    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void updateData(List<Product> newItems) {
        Log.d("BestDealAdapter", "Updating data with new items: " + newItems.size());

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

