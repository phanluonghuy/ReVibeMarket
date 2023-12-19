package com.example.revibemarket;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.revibemarket.Adapter.CartItemAdapter;
import com.example.revibemarket.Models.CartItem;
import com.example.revibemarket.Models.Order;
import com.example.revibemarket.Models.OrderItem;
import com.example.revibemarket.Models.ShippingInfo;
import com.example.revibemarket.Models.User;
import com.example.revibemarket.ModelsSingleton.CartSession;
import com.example.revibemarket.ModelsSingleton.UserSession;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class CartFragment extends Fragment {

    private RecyclerView recyclerView;
    private CartItemAdapter cartItemAdapter;
    private List<CartItem> cartItemList;
    private TextView tvTotalPrice;
    private FirebaseUser currentUser;
    private String SellerAddress;
    private String SellerEmail;
    private Spinner spinnerPaymentMethod;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        recyclerView = view.findViewById(R.id.rv_my_orders);
        tvTotalPrice = view.findViewById(R.id.tv_totalprice);

        tvTotalPrice.setText(String.format("%.2f",cal_totalPrice())+"$");

        spinnerPaymentMethod = view.findViewById(R.id.spinnerPaymentMethod);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        cartItemAdapter = new CartItemAdapter();
        recyclerView.setAdapter(cartItemAdapter);


        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {
                tvTotalPrice.setText(String.format("%.2f",cal_totalPrice())+"$");
            }
        });

        String[] paymentMethods = {"Momo", "ZaloPay", "Credit Card", "COD"};

        ArrayAdapter<String> paymentMethodAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, paymentMethods);
        paymentMethodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerPaymentMethod.setAdapter(paymentMethodAdapter);

        spinnerPaymentMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedPaymentMethod = paymentMethods[position];
                // TODO: Xử lý khi chọn phương thức thanh toán (cập nhật dữ liệu hoặc thực hiện các thao tác khác)
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                showToast("Please select payment method");
            }
        });

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

//        fetchCartItems();
        Button btnBuy = view.findViewById(R.id.btn_Buy);
        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser != null) {
                    saveOrder();
                } else {
                    showToast("User not logged in. Please log in to place an order.");
                }
            }
        });

        return view;
    }

    private void saveOrder() {
        List<OrderItem> orderItems = new ArrayList<>();

        List<CartItem> cartItems = CartSession.getInstance().getCartItemList();
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setSellerID(cartItem.getSellerID());
            orderItem.setSku(cartItem.getItemId());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setProductTitle(cartItem.getProductName());
            orderItem.setPrice(cartItem.getPrice());
            orderItem.setDiscount(cartItem.getDiscount());
            orderItem.setTotal(cartItem.getPrice()*((100-cartItem.getDiscount())/100)*cartItem.getQuantity());
            orderItems.add(orderItem);
        }

        ShippingInfo shippingInfo = new ShippingInfo(generateUniqueKey(),UserSession.getInstance().getAddress(), "Carrier", "ReVibeShipping");

        Order order = new Order(
                currentUser.getUid(),
                spinnerPaymentMethod.getSelectedItem().toString(),
                "Pending",
                getCurrentDate(),
                "Processing",
                cal_totalPrice(),
                orderItems,
                shippingInfo
        );

        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference().child("orders");

        ordersRef.push().setValue(order)
                .addOnSuccessListener(aVoid -> {
                    cartItemAdapter.clearCart();
                    showToast("Order placed successfully");
                })
                .addOnFailureListener(e -> {
                    showToast("Failed to place order");
                });
    }

    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currentDate = new Date();
        return dateFormat.format(currentDate);
    }

    private void clearCart() {
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("carts").child(currentUser.getUid());
        cartRef.removeValue();
    }

    private void fetchUserData(String userId) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User userData = dataSnapshot.getValue(User.class);

                    if (userData != null) {
                        // Đoạn mã fetchUserData không thay đổi
                        // ...
                    } else {
                        showToast("User data is null");
                    }
                } else {
                    showToast("User not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showToast("Error fetching user data: " + databaseError.getMessage());
            }
        });
    }

    private void fetchCartItems() {
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
                            total[0] += (cartItem.getQuantity() * cartItem.getPrice()) * (cartItem.getDiscount() / 100);
                        }
                    }

                    Log.d("CartFragment", "fetchCartItems: " + cartItemList.size());

                    tvTotalPrice.setText(String.format("$%.2f", total[0] + 3));
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

//    @Override
//    public void onRemoveItemClick(int position, String itemId) {
//        if (currentUser != null) {
//            DatabaseReference cartItemRef = FirebaseDatabase.getInstance().getReference()
//                    .child("carts")
//                    .child(currentUser.getUid())
//                    .child(itemId);
//
//            cartItemRef.removeValue()
//                    .addOnCompleteListener(task -> {
//                        if (task.isSuccessful()) {
//                            getActivity().runOnUiThread(() -> {
//                                cartItemList.remove(position);
//                                cartItemAdapter.notifyItemRemoved(position);
//                                cartItemAdapter.notifyItemRangeChanged(position, cartItemList.size());
//                                showToast("Item removed successfully");
//                            });
//                        } else {
//                            showToast("Failed to remove item");
//                        }
//                    });
//        }
//    }cartItem.getPrice()*((100-cartItem.getDiscount())/100)

    private String generateUniqueKey() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "").substring(0, 28);
    }
    private double cal_totalPrice()
    {
        AtomicReference<Double> totalPrice = new AtomicReference<>((double) 0);
        List<CartItem> cartItems = CartSession.getInstance().getCartItemList();
        CartSession.getInstance().getCartItemList().stream()
                .forEach(cartItem -> {
                    totalPrice.updateAndGet(v -> new Double((double) (v + cartItem.getPrice() * (100 - cartItem.getDiscount())/100) * cartItem.getQuantity()));
                });
        return totalPrice.get();
    }
}
