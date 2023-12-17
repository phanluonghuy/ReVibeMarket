package com.example.revibemarket;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.revibemarket.Adapter.DetailImageAdapter;
import com.example.revibemarket.Adapter.SnapHelperOneByOne;
import com.example.revibemarket.Models.CartItem;
import com.example.revibemarket.Models.Product;
import com.example.revibemarket.ModelsSingleton.CartSession;
import com.example.revibemarket.ModelsSingleton.ProductSingleton;
import com.example.revibemarket.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.List;

public class DetailActivity extends AppCompatActivity {
    Button btnBack;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentUserId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        btnBack = findViewById(R.id.btnBack);

        fetchCurrentUserInformation();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String productSku = bundle.getString("productSku");
            if (!TextUtils.isEmpty(productSku)) {
                initializeViews(productSku);
            } else {
                showToast("Product SKU is null");
                finish();
            }
        } else {
            showToast("Bundle is null");
            finish();
        }

        btnBack.setOnClickListener(v -> finish());
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initializeViews(String productSku) {
        Product product = getProductFromSku(productSku);
        if (product != null) {
            List<String> imageUrls = product.getProductType().getImages();
            Log.d("Detail product", product.getProductName());

            TextView tvProductName = findViewById(R.id.tvProductName);
            TextView tvProductDescription = findViewById(R.id.tvProductDescription);
            TextView tvProductPrice = findViewById(R.id.tvProductPrice);
            TextView tvProductStock = findViewById(R.id.tvProductStock);
            TextView tvSellerAddress = findViewById(R.id.tvSellerAddress);
            TextView tvDiscount = findViewById(R.id.tvProductDiscount);
            EditText edtQuantity = findViewById(R.id.edtQuantity);
            Button btnMinus = findViewById(R.id.btnMinus);
            Button btnPlus = findViewById(R.id.btnPlus);
            Button btnAdd = findViewById(R.id.btnAddtoCart);

            RecyclerView recyclerView = findViewById(R.id.RecyclerDetailImg);
            if (recyclerView != null) {
                DetailImageAdapter detailImageAdapter = new DetailImageAdapter(getApplicationContext(), imageUrls);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
                LinearSnapHelper linearSnapHelper = new SnapHelperOneByOne();
                linearSnapHelper.attachToRecyclerView(recyclerView);
                recyclerView.setAdapter(detailImageAdapter);
                detailImageAdapter.notifyDataSetChanged();
            } else {
                showToast("RecyclerView is null");
            }

            setTextViewValue(tvProductName, product.getProductName(), "Product Name");
            setTextViewValue(tvProductDescription, product.getProductType().getDescription(), "This is a detailed description of the product.");

            String priceText = String.format("Price: $%.2f", product.getProductType().getPrice());
            setTextViewValue(tvProductPrice, priceText, "");

            setTextViewValue(tvProductStock, "Stock: " + product.getProductType().getStock(), "Stock:");

            String userID = product.getUserID();
            fetchUserAddress(userID, tvSellerAddress);

            setTextViewValue(tvDiscount, String.format("Discount: %.1f%%", product.getProductType().getDiscount()), "No Discount");

            btnMinus.setOnClickListener(v -> decrementQuantity(edtQuantity));

            btnPlus.setOnClickListener(v -> incrementQuantity(edtQuantity, product.getProductType().getStock()));

            int newQuantity = getQuantityFromEditText(edtQuantity, product.getProductType().getStock());
            edtQuantity.setText(String.valueOf(newQuantity));

            btnAdd.setOnClickListener(v -> {
                int quantity = getQuantityFromEditText(edtQuantity, product.getProductType().getStock());
                double price = product.getProductType().getPrice();
                double discount = product.getProductType().getDiscount();
                String productName = tvProductName.getText().toString();
                CartItem cartItem = new CartItem(productSku, productName, price, discount, quantity, currentUserId, product.getUserID());

                CartSession.getInstance().getCartItemList().stream()
                        .filter(item -> item.getItemId().equals(cartItem.getItemId()))
                        .findFirst()
                        .ifPresent(cartItem1 -> cartItem1.setQuantity(cartItem1.getQuantity() + cartItem.getQuantity()));

                if (CartSession.getInstance().getCartItemList().stream()
                        .noneMatch(item -> item.getItemId().equals(cartItem.getItemId()))) {
                    CartSession cartSession = CartSession.getInstance();
                    cartSession.addCartItem(cartItem, product.getProductType().getImages().get(0));
                }
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("cart_session", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Gson gson = new Gson();
                String cartItemsJson = gson.toJson(CartSession.getInstance().getCartItemList());
                String imagesUrlJson = gson.toJson(CartSession.getInstance().getImagesUrl());
                editor.putString("cart_items", cartItemsJson);
                editor.putString("images_url", imagesUrlJson);
                editor.apply();

                showToast("Product added to cart successful");
            });
        } else {
            showToast("Product is null");
            finish();
        }
    }

    private Product getProductFromSku(String productSku) {
        if (TextUtils.isEmpty(productSku)) {
            showToast("Product SKU is empty");
            finish();
            return null;
        }

        Product product = ProductSingleton.getInstance().getProductList().stream()
                .filter(productDetail -> productSku.equals(productDetail.getSku()))
                .findFirst()
                .orElse(null);

        if (product == null) {
            showToast("Product not found");
            finish();
        }

        return product;
    }

    private void setTextViewValue(TextView textView, String value, String defaultValue) {
        if (textView != null) {
            textView.setText(!TextUtils.isEmpty(value) ? value : defaultValue);
        } else {
            showToast(textView.getId() + " is null");
        }
    }

    private void fetchUserAddress(String userID, TextView tvSellerAddress) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users").child(userID);

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String address = dataSnapshot.child("address").getValue(String.class);

                    String fullAddress = "Address: " + (!TextUtils.isEmpty(address) ? address : "");
                    setTextViewValue(tvSellerAddress, fullAddress, "Address: ");
                } else {
                    showToast("User not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showToast("Error fetching user information");
            }
        });
    }

    private void decrementQuantity(EditText edtQuantity) {
        int currentQuantity = getQuantityFromEditText(edtQuantity);
        int newQuantity = Math.max(currentQuantity - 1, 1);
        edtQuantity.setText(String.valueOf(newQuantity));
    }

    private void incrementQuantity(EditText edtQuantity, int stock) {
        int currentQuantity = getQuantityFromEditText(edtQuantity);
        int newQuantity = Math.min(currentQuantity + 1, stock);
        edtQuantity.setText(String.valueOf(newQuantity));
    }

    private int getQuantityFromEditText(EditText edtQuantity) {
        try {
            return Integer.parseInt(edtQuantity.getText().toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private int getQuantityFromEditText(EditText edtQuantity, int stock) {
        try {
            int quantity = Integer.parseInt(edtQuantity.getText().toString());
            return Math.max(1, Math.min(quantity, stock));
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    private void fetchCurrentUserInformation() {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User currentUser = dataSnapshot.getValue(User.class);

                    if (currentUser != null) {
                        currentUserId = currentUser.getId();
                    }
                } else {
                    showToast("User not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showToast("Error fetching user information");
            }
        });
    }
}
