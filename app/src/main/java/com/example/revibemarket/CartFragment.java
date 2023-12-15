package com.example.revibemarket;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.revibemarket.Adapter.CartItemAdapter;
import com.example.revibemarket.Models.CartItem;
import com.example.revibemarket.Models.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {

    private RecyclerView recyclerView;
    private CartItemAdapter cartItemAdapter;
    private List<CartItem> cartItemList;
    private TextView tvTotalPrice;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        recyclerView = view.findViewById(R.id.reycylerCartItem);
        tvTotalPrice = view.findViewById(R.id.tvTotalPrice);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        cartItemList = new ArrayList<>();
        cartItemAdapter = new CartItemAdapter(cartItemList);
        recyclerView.setAdapter(cartItemAdapter);


        fetchCartItems();

        return view;
    }

    private void fetchCartItems() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final double[] total = {0};
        if (currentUser != null) {
            DatabaseReference cartsRef = FirebaseDatabase.getInstance().getReference().child("carts");

            cartsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    cartItemList.clear();
                    total[0] = 0;

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        CartItem cartItem = snapshot.getValue(CartItem.class);
                        if (cartItem != null) {
                            cartItemList.add(cartItem);
                            total[0] += cartItem.getQuantity() * cartItem.getPrice();
                        }
                    }

                    Log.d("CartFragment", "fetchCartItems: " + cartItemList.size());

                    tvTotalPrice.setText(String.format("$%.2f", total[0]));
                    cartItemAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("CartFragment", "Failed to read value.", databaseError.toException());
                }
            });
        }
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
