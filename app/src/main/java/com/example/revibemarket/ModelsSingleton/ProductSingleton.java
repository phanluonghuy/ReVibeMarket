package com.example.revibemarket.ModelsSingleton;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.revibemarket.Models.Product;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ProductSingleton {
    private static ProductSingleton instance;
    private List<Product> productList;

    private boolean modify;

    private ProductSingleton() {
        // Initialize the productList
        productList = new ArrayList<>();
        modify = false;

    }

    public static void setInstance(ProductSingleton instance) {
        ProductSingleton.instance = instance;
    }

    public boolean isModify() {
        return modify;
    }

    public void setModify(boolean modify) {
        this.modify = modify;
    }

    public static synchronized ProductSingleton getInstance() {
        if (instance == null) {
            instance = new ProductSingleton();
        }
        return instance;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    public void fetchProductNameAndSKU(DataFetchedListener listener) {
        productList.clear();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("products");

            productsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                        Product product = productSnapshot.getValue(Product.class);
                        if (product != null) {
                            product.getProductType().clearImage();
                            productList.add(product);

                            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                            StorageReference listRef = firebaseStorage.getReference().child("Images_Product/"+ product.getSku());
                            listRef.listAll()
                                    .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                                        @Override
                                        public void onSuccess(ListResult listResult) {
                                            Log.e("ListFiles Product",product.getSku());

                                            for (StorageReference item : listResult.getItems()) {
                                                item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        Log.e("ListFiles", "Success to get download URL: " + uri.toString());
                                                        product.getProductType().addImage(uri.toString());
                                                        listener.onDataFetched();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.e("ListFiles", "Failed to get download URL: " + e.getMessage());
                                                    }
                                                });
                                            }

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            Log.e("ListFiles", "Failed to list files: " + exception.getMessage());
                                        }
                                    });
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

    public interface DataFetchedListener {
        void onDataFetched();
    }

}
