package com.example.revibemarket;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.revibemarket.Adapter.CategoryAdapter;
import com.example.revibemarket.Adapter.HomeProductAdapter;
import com.example.revibemarket.Models.Product;
import com.example.revibemarket.ModelsSingleton.ProductSingleton;
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
import com.google.gson.Gson;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerCategory;
    private RecyclerView recyclerProduct;
    private HomeProductAdapter homeProductAdapter;
    private List<Product> productList = new ArrayList<>();
    private EditText edtSearch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerCategory = rootView.findViewById(R.id.recyclerCategory);
        recyclerProduct = rootView.findViewById(R.id.recyclerBestDeal);
//        ImageButton search = rootView.findViewById(R.id.btnSearch);
        edtSearch = rootView.findViewById(R.id.edtSearch);
        setupCategoryRecyclerView();
//        fetchProductNameAndSKU();
        setupProductRecyclerView();

        Log.d("onCreateView","onCreateView");



        if (ProductSingleton.getInstance().getProductList().size()==0) {
            ProductSingleton.getInstance().fetchProductNameAndSKU(new ProductSingleton.DataFetchedListener() {
                @Override
                public void onDataFetched() {
                    productList = ProductSingleton.getInstance().getProductList();
                    homeProductAdapter.notifyDataSetChanged();
                }
            });
        }

        if (ProductSingleton.getInstance().isModify()) {
            ProductSingleton.getInstance().fetchProductNameAndSKU(new ProductSingleton.DataFetchedListener() {
                @Override
                public void onDataFetched() {
                    productList = ProductSingleton.getInstance().getProductList();
                    homeProductAdapter.notifyDataSetChanged();
                }
            });
        }



        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE) {
                    try {
                        Intent intent = new Intent(requireContext(), SearchActivity.class);
                        Gson gson = new Gson();
                        String searchText = gson.toJson(edtSearch.getText().toString());
                        intent.putExtra("searchText", searchText);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                return false;
            }
        });
        return rootView;
    }

    private void setupCategoryRecyclerView() {
        List<String> categories = Arrays.asList(getResources().getStringArray(R.array.categories_english));
        CategoryAdapter categoryAdapter = new CategoryAdapter(requireActivity(), categories);
        recyclerCategory.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerCategory.setAdapter(categoryAdapter);
    }

    private void setupProductRecyclerView() {
        productList = ProductSingleton.getInstance().getProductList();
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

    }

    private void fetchProductNameAndSKU() {
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
                            fetchImage(product);
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
    private void fetchImage(Product product) {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference listRef = firebaseStorage.getReference().child("Images_Product/"+ product.getSku());
        ArrayList<String> imgUrl = new ArrayList<>();
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
                                    if (product.getProductType().getImages().size()==1) {
                                        homeProductAdapter.notifyDataSetChanged();
                                    }
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
        ProductSingleton.getInstance().setProductList(productList);
    }

    private void openDetailPage(Product product) {
        try {
            Intent intent = new Intent(requireContext(), DetailActivity.class);
            intent.putExtra("productSku", product.getSku());
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ExploreFragment", "Error opening detail page", e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        Log.d("onResume","onResume");
//        ProductSingleton.getInstance().fetchProductNameAndSKU(new ProductSingleton.OnFetchCompleteListener() {
//            @Override
//            public void onFetchComplete() {
//                productList = ProductSingleton.getInstance().getProductList();
//                homeProductAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onFetchError(String errorMessage) {
//
//            }
//        });

    }
}