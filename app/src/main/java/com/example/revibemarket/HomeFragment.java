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
    private RecyclerView recyclerProduct;
    private BestDealAdapter bestDealAdapter;
    private List<Product> productList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
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
        bestDealAdapter = new BestDealAdapter(requireContext(), productList);
        recyclerProduct.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        recyclerProduct.setAdapter(bestDealAdapter);
    }



    private void fetchProductNameAndSKU() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("products");

            productsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    bestDealItems.clear();
//                    if (dataSnapshot.getChildren() == null) return;
                    for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                        Product product = productSnapshot.getValue(Product.class);
                        if (product != null) {
                            productList.add(product);
//                            String productName = product.getProductName();
//                            String sku = product.getSku();
                            Log.d("Product Name: ", product.getProductName());
                            Log.d("Product Sku: ", product.getSku());
//                            Log.d("Product SKU: ", sku);
                                //fetchPrice(product);

//                            fetchPrice(productName, sku);
                        }
                    }
                    bestDealAdapter.notifyDataSetChanged();
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
        StorageReference storageReference = firebaseStorage.getReference().child("images");

        String imageFileName = "image_0_" + sku;
        StorageReference imageRef = storageReference.child(imageFileName);

        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            String imageUrl = uri.toString();
            Log.d("IMAGE URL: ", imageUrl);
//            updateDataInBestDeal(productName, sku, imageUrl, price);
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

//    private void updateDataInBestDeal(String productName, String sku, String imageUrl, Double price) {
//        BestDealItem newDealItem = new BestDealItem(productName, price, imageUrl, sku);
//        bestDealItems.add(newDealItem);
//
//        Log.d("BestDealItems", "Updated items count: " + bestDealItems.size());
//
//        bestDealAdapter.updateData(bestDealItems);
//        bestDealAdapter.notifyDataSetChanged();
//    }



}
