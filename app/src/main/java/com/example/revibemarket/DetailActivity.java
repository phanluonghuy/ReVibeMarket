package com.example.revibemarket;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.revibemarket.Adapter.DetailImageAdapter;
import com.example.revibemarket.Models.Product;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {
    Button btnBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        btnBack = findViewById(R.id.btnBack);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String productJson = bundle.getString("productJson");
            if (productJson != null) {
                Gson gson = new Gson();
                Product product = gson.fromJson(productJson, Product.class);

                TextView tvProductName = findViewById(R.id.tvProductName);
                TextView tvProductDescription = findViewById(R.id.tvProductDescription);
                TextView tvProductPrice = findViewById(R.id.tvProductPrice);

                RecyclerView recyclerView = findViewById(R.id.RecyclerDetailImg);
                LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                recyclerView.setLayoutManager(layoutManager);
                DetailImageAdapter imageAdapter = new DetailImageAdapter(this);
                recyclerView.setAdapter(imageAdapter);

                setProductImages(product, imageAdapter);

                setTextViewValue(tvProductName, product.getProductName(), "Product Name");
                setTextViewValue(tvProductDescription, product.getProductType().getDescription(), "This is a detailed description of the product.");

                // Format giá tiền và set giá trị cho TextView
                String priceText = String.format("Price: $%.2f", product.getProductType().getPrice());
                setTextViewValue(tvProductPrice, priceText, "");

            } else {
                showToast("Product JSON is null");
                finish();
            }
        } else {
            showToast("Bundle is null");
            finish();
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void setTextViewValue(TextView textView, String value, String defaultValue) {
        if (textView != null) {
            textView.setText(value != null ? value : defaultValue);
        } else {
            showToast(textView.getId() + " is null");
        }
    }

    private void setProductImages(Product product, DetailImageAdapter imageAdapter) {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        List<String> imageUrls = new ArrayList<>();

        for (int i = 0; i < product.getProductType().getImages().size(); i++) {
            StorageReference storageReference = firebaseStorage.getReference().child("Images_Product/" + product.getSku() + "/" + i);

            storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                Log.d("DetailActivity", "Image URL: " + uri.toString());
                imageUrls.add(uri.toString());
                if (imageUrls.size() == product.getProductType().getImages().size()) {
                    imageAdapter.setImageUrls(imageUrls);
                }
                imageAdapter.notifyDataSetChanged();
            }).addOnFailureListener(exception -> {
                Log.e("DetailActivity", "Error loading image", exception);
                Toast.makeText(this, "Error Load Image", Toast.LENGTH_SHORT).show();
            });

        }
    }
}

