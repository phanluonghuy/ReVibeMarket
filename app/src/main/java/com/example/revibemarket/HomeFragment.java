package com.example.revibemarket;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.revibemarket.Adapter.BestDealAdapter;
import com.example.revibemarket.Adapter.CategoryAdapter;
import com.example.revibemarket.Models.BestDealItem;
import com.example.revibemarket.Models.Product;
import com.example.revibemarket.Models.Product_Type;
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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerCategory;
    private RecyclerView recyclerBestDeal;
    private BestDealAdapter bestDealAdapter;
    private List<BestDealItem> bestDealItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerCategory = rootView.findViewById(R.id.recyclerCategory);
        recyclerBestDeal = rootView.findViewById(R.id.recyclerCategory);


        setupCategoryRecyclerView();
        setupBestDealRecyclerView();
        fetchProductNameAndSKU();



        return rootView;
    }

    private void setupCategoryRecyclerView() {
        List<String> categories = Arrays.asList(getResources().getStringArray(R.array.categories_english));
        CategoryAdapter categoryAdapter = new CategoryAdapter(requireActivity(), categories);
        recyclerCategory.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerCategory.setAdapter(categoryAdapter);
    }

    private void setupBestDealRecyclerView() {
        bestDealItems = new ArrayList<>();
        bestDealAdapter = new BestDealAdapter(requireContext(), bestDealItems);
        recyclerBestDeal.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerBestDeal.setAdapter(bestDealAdapter);
    }



    private void fetchProductNameAndSKU() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("products");

            productsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    bestDealItems.clear();

                    for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                        Product product = productSnapshot.getValue(Product.class);
                        if (product != null) {
                            String productName = product.getProductName();
                            String sku = product.getSku();
                            Log.d("Product Name: ", productName);
                            Log.d("Product SKU: ", sku);

                            fetchPrice(productName, sku);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("HomeFragment", "Failed to read value.", databaseError.toException());
                }
            });
        }
    }

    private void fetchPrice(String productName, String sku) {
        DatabaseReference productTypesRef = FirebaseDatabase.getInstance().getReference().child("product_types");

        productTypesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot pt : dataSnapshot.getChildren()) {
                    Product_Type product_type = pt.getValue(Product_Type.class);
                    if (product_type != null && sku.equals(product_type.getSku())) {
                        Double price = product_type.getPrice();

                        fetchImage(productName, sku, price);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("HomeFragment", "Failed to read value.", databaseError.toException());
            }
        });
    }

    private void fetchImage(String productName, String sku, Double price) {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference().child("images");

        String imageFileName = "image_0_" + sku;
        StorageReference imageRef = storageReference.child(imageFileName);

        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            String imageUrl = uri.toString();
            Log.d("IMAGE URL: ", imageUrl);
            updateDataInBestDeal(productName, sku, imageUrl, price);
        }).addOnFailureListener(exception -> {
            if (exception instanceof StorageException && ((StorageException) exception).getErrorCode() == StorageException.ERROR_OBJECT_NOT_FOUND) {
                Log.e("fetchImage", "Image not found for " + imageFileName);
                Toast.makeText(requireContext(), "Image not found", Toast.LENGTH_SHORT).show();
            } else {
                Log.e("fetchImage", "Error fetching image for " + imageFileName + ": " + exception.getMessage(), exception);
                Toast.makeText(requireContext(), "Error fetching image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateDataInBestDeal(String productName, String sku, String imageUrl, Double price) {
        BestDealItem newDealItem = new BestDealItem(productName, price, imageUrl, sku);
        bestDealItems.add(newDealItem);

        Log.d("BestDealItems", "Updated items count: " + bestDealItems.size());

        bestDealAdapter.updateData(bestDealItems);
        bestDealAdapter.notifyDataSetChanged();
    }



}
