package com.example.revibemarket;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.revibemarket.Models.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

public class SearchActivity extends AppCompatActivity {
    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("products");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        if (getIntent().hasExtra("searchText")) {
            String searchTextJson = getIntent().getStringExtra("searchText");
            Gson gson = new Gson();
            String searchText = gson.fromJson(searchTextJson, String.class);

            TextView textView = findViewById(R.id.SearchName);
            textView.setText("Searching for: " + searchText);

            searchProduct(searchText.toLowerCase());
        }

        Button btnBack = findViewById(R.id.btnBackSearch);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void searchProduct(String searchText) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean found = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null && product.getProductName().toLowerCase().equals(searchText)) {
                        found = true;
                        Toast.makeText(SearchActivity.this, "Da tim thay " + searchText, Toast.LENGTH_SHORT).show();

                    }
                }

                if (!found) {
                    Toast.makeText(SearchActivity.this, "Khong tim thay " + searchText, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SearchActivity.this, "Failed to retrieve data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}



