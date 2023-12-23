package com.example.revibemarket;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.revibemarket.Adapter.MyProductAdapter;
import com.example.revibemarket.ModelsSingleton.ProductSingleton;
import com.example.revibemarket.ModelsSingleton.UserSession;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class MyProductFragment extends Fragment {

    private RecyclerView recyclerProduct;
    private TextView totalProduct;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_product, container, false);
        recyclerProduct = view.findViewById(R.id.recyclerMyProduct);
        totalProduct = view.findViewById(R.id.tv_totalProduct);

        MyProductAdapter myProductAdapter = new MyProductAdapter();
        recyclerProduct.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerProduct.setAdapter(myProductAdapter);
        totalProduct.setText(myProductAdapter.getItemCount() + "");

        return view;
    }

}