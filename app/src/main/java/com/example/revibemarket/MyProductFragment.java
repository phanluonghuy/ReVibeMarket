package com.example.revibemarket;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.revibemarket.Adapter.MyProductAdapter;
import com.example.revibemarket.ModelsSingleton.ProductSingleton;


public class MyProductFragment extends Fragment {

    private RecyclerView recyclerProduct;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_product, container, false);
        recyclerProduct = view.findViewById(R.id.recyclerMyProduct);

        MyProductAdapter myProductAdapter = new MyProductAdapter();
        recyclerProduct.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        recyclerProduct.setAdapter(myProductAdapter);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("onPause","onPause");
        ProductSingleton.getInstance().fetchProductNameAndSKU(new ProductSingleton.DataFetchedListener() {
            @Override
            public void onDataFetched() {

            }
        });
    }
}