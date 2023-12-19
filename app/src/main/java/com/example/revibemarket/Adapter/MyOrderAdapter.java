package com.example.revibemarket.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.revibemarket.Models.Order;
import com.example.revibemarket.Models.OrderItem;
import com.example.revibemarket.Models.Product;
import com.example.revibemarket.ModelsSingleton.UserSession;
import com.example.revibemarket.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.ViewHolder> {
    private List<Order> orderList = new ArrayList<>();

    public MyOrderAdapter() {
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference();
        Query orderQuery = orderRef.child("orders").orderByChild("customerId").equalTo(UserSession.getInstance().getId());
        orderQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Product with productTypeSku exists, update it
                    for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                        // Perform the update in the database
                        Order order = productSnapshot.getValue(Order.class);
                        orderList.add(order);
                        notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
        
    }

    @NonNull
    @Override
    public MyOrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.myorder_item_layout, parent, false);
        return new MyOrderAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyOrderAdapter.ViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.textViewTotal.setText("Total : "+ String.format("$%.2f",order.getTotalCost()));
        holder.textViewPaymentStatus.setText(" Payment Status : " + order.getStatus());
        holder.textViewPaymentMethod.setText(" Payment Method : " + order.getPaymentMethod());
        holder.textViewDate.setText("Date ordered : " + order.getDate().toString());
        holder.textViewStatus.setText("Status : " + order.getStatus());
        holder.bind(order.getItems());

    }
    @Override
    public int getItemCount() {
        return orderList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTotal;
        TextView textViewPaymentStatus,textViewPaymentMethod,textViewDate,textViewStatus;

        RecyclerView recyclerOrderProduct;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTotal = itemView.findViewById(R.id.textViewTotalPrice);
            textViewPaymentStatus = itemView.findViewById(R.id.textViewPaymentStatus);
            textViewPaymentMethod = itemView.findViewById(R.id.textViewPaymentMethod);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
            recyclerOrderProduct = itemView.findViewById(R.id.recyclerOrderProduct);
            recyclerOrderProduct.setLayoutManager(new LinearLayoutManager(itemView.getContext(),LinearLayoutManager.VERTICAL,false));
        }

        public void bind(List<OrderItem> orderItemList)
        {
            MyOrderProductAdapter myOrderProductAdapter = new MyOrderProductAdapter(orderItemList);
            recyclerOrderProduct.setAdapter(myOrderProductAdapter);
        }
    }
}
