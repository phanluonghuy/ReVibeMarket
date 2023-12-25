package com.example.revibemarket;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.revibemarket.Adapter.MyProductAdapter;
import com.example.revibemarket.Adapter.MySoldAdapter;

public class SoldFragment extends Fragment {

    private RecyclerView recyclerProduct;
    private TextView totalProduct;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sold, container, false);

        recyclerProduct = view.findViewById(R.id.recyclerMySoldProduct);
        totalProduct = view.findViewById(R.id.tv_totalProduct);

        MySoldAdapter mySoldAdapter = new MySoldAdapter(totalProduct);
        recyclerProduct.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerProduct.setAdapter(mySoldAdapter);
        return view;
    }
}