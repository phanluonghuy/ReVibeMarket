package com.example.revibemarket;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.revibemarket.Adapter.CategoryAdapter;
import com.example.revibemarket.Adapter.ExploreProductAdapter;
import com.example.revibemarket.Models.Product;
import com.example.revibemarket.ModelsSingleton.ProductSingleton;

import java.util.Arrays;
import java.util.List;


public class ExploreFragment extends Fragment implements CategoryAdapter.OnItemClickListener{
    private RecyclerView recyclerCategory;
    private RecyclerView recyclerProduct;
    private ExploreProductAdapter exploreProductAdapter;
    private EditText edtSearch;
    private List<Product> productList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_explore, container, false);
        recyclerCategory = rootView.findViewById(R.id.recyclerCategory);
        recyclerProduct = rootView.findViewById(R.id.recyclerBestDeal);
        edtSearch = rootView.findViewById(R.id.edtSearch);
        edtSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (edtSearch.getRight() - edtSearch.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        exploreProductAdapter.getFilter().filter(edtSearch.getText().toString());
                        return true;
                    }
                }
                return false;
            }
        });

        if (ProductSingleton.getInstance().isModify()) {
            ProductSingleton.getInstance().fetchProductNameAndSKU(new ProductSingleton.DataFetchedListener() {
                @Override
                public void onDataFetched() {
                    productList = ProductSingleton.getInstance().getProductList();
                    exploreProductAdapter.notifyDataSetChanged();

                }
            });
        } else productList = ProductSingleton.getInstance().getProductList();

        setupCategoryRecyclerView();
        setupProductRecyclerView();


        return rootView;
    }


    private void setupCategoryRecyclerView() {
        List<String> categories = Arrays.asList(getResources().getStringArray(R.array.categories_english));
        CategoryAdapter categoryAdapter = new CategoryAdapter(requireActivity(), categories);
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
                exploreProductAdapter.getFilterCategory().filter(selectedCategories.toString());
                // Handle the list of selected categories
                Log.d("CategoryAdapter", "Selected categories: " + selectedCategories.toString());
            }
        });


        recyclerCategory.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerCategory.setAdapter(categoryAdapter);
    }

    private void setupProductRecyclerView() {
        exploreProductAdapter = new ExploreProductAdapter(requireContext(), productList);

        exploreProductAdapter.setOnItemClickListener(new ExploreProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Product product) {
                openDetailPage(product);
            }
        });

        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        recyclerProduct.setLayoutManager(horizontalLayoutManager);
        recyclerProduct.setAdapter(exploreProductAdapter);
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
    public void onItemClick(String category, List<String> selectedCategories) {


    }
}