package com.example.revibemarket;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.revibemarket.Adapter.CategoryAdapter;
import com.example.revibemarket.Adapter.HomeProductAdapter;
import com.example.revibemarket.Models.Product;
import com.example.revibemarket.ModelsSingleton.ProductSingleton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
    private LottieAnimationView lottieAnimationView;
    private ExploreFragment exploreFragment = new ExploreFragment();

    private Button buttonShowNow;
    private TextView textViewSeeAll;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerCategory = rootView.findViewById(R.id.recyclerCategory);
        recyclerProduct = rootView.findViewById(R.id.recyclerBestDeal);
        buttonShowNow = rootView.findViewById(R.id.buttonShowNow);
        lottieAnimationView = rootView.findViewById(R.id.lottieAnimationViewHome);
        edtSearch = rootView.findViewById(R.id.edtSearch);
        textViewSeeAll = rootView.findViewById(R.id.textViewSeeAll);
        setupCategoryRecyclerView();
        setupProductRecyclerView();

        edtSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (edtSearch.getRight() - edtSearch.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        homeProductAdapter.getFilter().filter(edtSearch.getText().toString());
                        return true;
                    }
                }
                return false;
            }
        });

        buttonShowNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadExploreFragment();
            }
        });

        textViewSeeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadExploreFragment();
            }
        });



        if (ProductSingleton.getInstance().getProductList().size()==0) {
            recyclerProduct.setVisibility(View.GONE);
            lottieAnimationView.setVisibility(View.VISIBLE);
            ProductSingleton.getInstance().fetchProductNameAndSKU(new ProductSingleton.DataFetchedListener() {
                @Override
                public void onDataFetched() {
                    productList = ProductSingleton.getInstance().getProductList();
                    homeProductAdapter.notifyDataSetChanged();
                    recyclerProduct.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            lottieAnimationView.setVisibility(View.GONE);
                        }
                    },300);


                }
            });
        }

        if (ProductSingleton.getInstance().isModify()) {
            recyclerProduct.setVisibility(View.GONE);
            lottieAnimationView.setVisibility(View.VISIBLE);
            ProductSingleton.getInstance().fetchProductNameAndSKU(new ProductSingleton.DataFetchedListener() {
                @Override
                public void onDataFetched() {
                    productList = ProductSingleton.getInstance().getProductList();
                    homeProductAdapter.notifyDataSetChanged();
                    recyclerProduct.setVisibility(View.VISIBLE);
                    lottieAnimationView.setVisibility(View.GONE);
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

    private void loadExploreFragment() {
        FragmentManager fragmentManager = getFragmentManager();

        if (fragmentManager != null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.replace(R.id.container, exploreFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

    private void setupCategoryRecyclerView() {
        List<String> categories = Arrays.asList(getResources().getStringArray(R.array.categories_english));
        CategoryAdapter categoryAdapter = new CategoryAdapter(requireActivity(), categories);
        recyclerCategory.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerCategory.setAdapter(categoryAdapter);
        categoryAdapter.setOnItemClickListener(new CategoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String category, List<String> selectedCategories) {
                // Handle item click and selection state here
                if (selectedCategories.isEmpty()) {
                    // Item is selected
                    Log.d("CategoryAdapter", "Selected category: " + category);
                } else {
                    // Item is not selected
                    Log.d("CategoryAdapter", "Deselected category: " + category);
                }
                homeProductAdapter.getFilterCategory().filter(selectedCategories.toString());
                // Handle the list of selected categories
                Log.d("CategoryAdapter", "Selected categories: " + selectedCategories.toString());
            }
        });
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
}