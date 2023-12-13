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

import com.example.revibemarket.Models.BestDealItem;
import com.example.revibemarket.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BestDealAdapter extends RecyclerView.Adapter<BestDealAdapter.ViewHolder> {
    private List<BestDealItem> bestDealItems;

    public BestDealAdapter(Context context, List<BestDealItem> bestDealItems) {
        this.bestDealItems = bestDealItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_bestdeal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BestDealItem item = bestDealItems.get(position);

        Log.d("BestDealAdapter", "Binding item: " + item.getTitle());

        holder.titleTextView.setText(item.getTitle());

        if (item.getPrice() != null) {
            holder.priceTextView.setText(String.valueOf(item.getPrice()));
        } else {
            holder.priceTextView.setText("");
        }

        Picasso.get().load(item.getImageURL()).into(holder.imageView);
    }



    @Override
    public int getItemCount() {
        return bestDealItems.size();
    }

    public void updateData(List<BestDealItem> newItems) {
        Log.d("BestDealAdapter", "Updating data with new items: " + newItems.size());

        bestDealItems.clear();
        bestDealItems.addAll(newItems);
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

