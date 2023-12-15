package com.example.revibemarket;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.revibemarket.Adapter.CategoryAdapter;
import com.example.revibemarket.Adapter.HomeProductAdapter;
import com.example.revibemarket.Models.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ExploreFragment extends Fragment {
    private RecyclerView recyclerCategory;
    private RecyclerView recyclerProduct;
    private HomeProductAdapter homeProductAdapter;
    private List<Product> productList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_explore, container, false);
        recyclerCategory = rootView.findViewById(R.id.recyclerCategory);
        recyclerProduct = rootView.findViewById(R.id.recyclerBestDeal);

        setupCategoryRecyclerView();
        fetchProductNameAndSKU();
        setupProductRecyclerView();

        return rootView;
    }

    private void setupCategoryRecyclerView() {
        List<String> categories = Arrays.asList(getResources().getStringArray(R.array.categories_english));
        CategoryAdapter categoryAdapter = new CategoryAdapter(requireActivity(), categories);
        recyclerCategory.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerCategory.setAdapter(categoryAdapter);
    }

    private void setupProductRecyclerView() {
        productList = new ArrayList<>();
        homeProductAdapter = new HomeProductAdapter(requireContext(), productList);

        homeProductAdapter.setOnItemClickListener(new HomeProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Product product) {
                openDetailPage(product);
            }
        });

        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerProduct.setLayoutManager(horizontalLayoutManager);
        recyclerProduct.setAdapter(homeProductAdapter);

        fetchProductNameAndSKU();
    }

    private void openDetailPage(Product product) {
        try {
            Intent intent = new Intent(requireContext(), DetailActivity.class);
            Gson gson = new Gson();
            String productJson = gson.toJson(product);
            intent.putExtra("productJson", productJson);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ExploreFragment", "Error opening detail page", e);
        }
    }

    private void fetchProductNameAndSKU() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("products");

            productsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    productList.clear();

                    for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                        Product product = productSnapshot.getValue(Product.class);
                        if (product != null) {
                            productList.add(product);
                        }
                    }
                    homeProductAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("HomeFragment", "Failed to read value.", databaseError.toException());
                }
            });
        }
    }
    private void fetchImage(String productName, String sku, Double price) {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference().child("Images_Product/" + sku + "/" + 0);
        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            String imageUrl = uri.toString();
            for (Product product : productList) {
                if (product != null && product.getSku() != null && product.getProductName().equals(productName)
                        && product.getSku().equals(sku)) {
                    List<String> images = product.getProductType().getImages();
                    if (images != null) {
                        images.add(imageUrl);
                        break;
                    }
                }
            }
            homeProductAdapter.notifyDataSetChanged();
        }).addOnFailureListener(exception -> {
            if (exception instanceof StorageException && ((StorageException) exception).getErrorCode() == StorageException.ERROR_OBJECT_NOT_FOUND) {
                Log.e("fetchImage", "Image not found for " + sku + "/" + 0);
                Toast.makeText(requireContext(), "Image not found", Toast.LENGTH_SHORT).show();
            } else {
                Log.e("fetchImage", "Error fetching image for " + sku + "/" + 0 + ": " + exception.getMessage(), exception);
                Toast.makeText(requireContext(), "Error fetching image", Toast.LENGTH_SHORT).show();
            }
        });
    }
}