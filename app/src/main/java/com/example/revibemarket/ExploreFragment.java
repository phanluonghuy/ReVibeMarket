package com.example.revibemarket;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.revibemarket.Adapter.CategoryAdapter;
import com.example.revibemarket.Adapter.ExploreProductAdapter;
import com.example.revibemarket.Models.Product;
import com.example.revibemarket.ModelsSingleton.ProductSingleton;

import java.util.Arrays;
import java.util.List;


public class ExploreFragment extends Fragment {
    private RecyclerView recyclerCategory;
    private RecyclerView recyclerProduct;
    private ExploreProductAdapter exploreProductAdapter;
    private List<Product> productList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_explore, container, false);
        recyclerCategory = rootView.findViewById(R.id.recyclerCategory);
        recyclerProduct = rootView.findViewById(R.id.recyclerBestDeal);

        productList = ProductSingleton.getInstance().getProductList();

        setupCategoryRecyclerView();
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

}