package com.example.revibemarket;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.revibemarket.Adapter.MyOrderAdapter;
import com.example.revibemarket.Adapter.MyProductAdapter;
import com.example.revibemarket.ModelsSingleton.UserSession;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MyOrderFragment extends Fragment {

    MyOrderAdapter myOrderAdapter;
    RecyclerView recyclerMyOrder;
    TextView tv_totalOrder;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_order, container, false);

        recyclerMyOrder = view.findViewById(R.id.rv_my_orders);
        tv_totalOrder = view.findViewById(R.id.tv_totalOrder);

        myOrderAdapter = new MyOrderAdapter();
        recyclerMyOrder.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        recyclerMyOrder.setAdapter(myOrderAdapter);

        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference();
        Query orderQuery = orderRef.child("orders").orderByChild("customerId").equalTo(UserSession.getInstance().getId());

        orderQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long totalOrders = dataSnapshot.getChildrenCount();
                tv_totalOrder.setText(totalOrders+ "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });



        return view;
    }
}