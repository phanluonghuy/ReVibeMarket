package com.example.revibemarket.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.revibemarket.Models.OrderItem;
import com.example.revibemarket.R;

import java.util.List;

public class MyOrderProductAdapter extends RecyclerView.Adapter<MyOrderProductAdapter.MyViewHolder> {
    List<OrderItem> orderItemList;

    public MyOrderProductAdapter(List<OrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }

    @NonNull
    @Override
    public MyOrderProductAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_order_product_item_layout, parent, false);
        return new MyOrderProductAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyOrderProductAdapter.MyViewHolder holder, int position) {
        holder.textViewProductName.setText(orderItemList.get(position).getProductTitle());
        holder.textViewQuantity.setText("Quantity: " + orderItemList.get(position).getQuantity());
        holder.textViewTotalPrice.setText("Total : " + String.format("$%.2f",orderItemList.get(position).getTotal()));
        holder.textViewDiscount.setText("Discount : " + orderItemList.get(position).getDiscount() + "%");
        holder.textViewPrice.setText("Price : " + String.format("$%.2f",orderItemList.get(position).getPrice()) );
    }

    @Override
    public int getItemCount() {
        return orderItemList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView textViewProductName,textViewQuantity,textViewTotalPrice,textViewDiscount,textViewPrice;
        public MyViewHolder(View view) {
            super(view);
            textViewProductName = view.findViewById(R.id.textViewProductName);
            textViewQuantity = view.findViewById(R.id.textViewQuantity);
            textViewTotalPrice = view.findViewById(R.id.textViewTotalPrice);
            textViewDiscount = view.findViewById(R.id.textViewDiscount);
            textViewPrice = view.findViewById(R.id.textViewPrice);
        }
    }
}
