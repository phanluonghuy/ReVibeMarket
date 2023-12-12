package com.example.revibemarket;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.revibemarket.Models.Product;
import com.example.revibemarket.Models.Product_Type;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class AddFragment extends Fragment {

    private EditText edtProductName, edtProductTitle, edtCreatedDate, edtChannels,
            edtStock, edtPrice, edtDiscount, edtDescription;
    private Spinner spinnerCategory;
    private Button btnSaveAddProduct;
    private DatabaseReference productsReference, productTypesReference;
    private DatePickerDialog.OnDateSetListener dateSetListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        initViews(view);
        setupSpinner();
        setupDatePicker();
        setupSaveButton();

        return view;
    }

    private void initViews(View view) {
        edtProductName = view.findViewById(R.id.edit_productName);
        edtProductTitle = view.findViewById(R.id.edt_productTitle);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        edtCreatedDate = view.findViewById(R.id.edt_createdDate);
        edtChannels = view.findViewById(R.id.edt_channels);
        edtStock = view.findViewById(R.id.edt_stock);
        edtPrice = view.findViewById(R.id.edt_price);
        edtDiscount = view.findViewById(R.id.edt_discount);
        edtDescription = view.findViewById(R.id.edt_description);
        btnSaveAddProduct = view.findViewById(R.id.btnSaveAddProduct);

        productsReference = FirebaseDatabase.getInstance().getReference().child("products");
        productTypesReference = FirebaseDatabase.getInstance().getReference().child("product_types");
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.categories_english, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    private void setupDatePicker() {
        edtCreatedDate.setInputType(InputType.TYPE_NULL);
        Calendar calendar = Calendar.getInstance();
        edtCreatedDate.setText(new SimpleDateFormat("MM/dd/yyyy").format(calendar.getTime()));

        dateSetListener = (view, year, month, dayOfMonth) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(Calendar.YEAR, year);
            selectedDate.set(Calendar.MONTH, month);
            selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            String selectedDateString = sdf.format(selectedDate.getTime());
            edtCreatedDate.setText(selectedDateString);
        };

        edtCreatedDate.setOnClickListener(v -> showDatePickerDialog());
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                dateSetListener,
                year, month, day);

        Objects.requireNonNull(datePickerDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.show();
    }

    private void setupSaveButton() {
        btnSaveAddProduct.setOnClickListener(v -> saveProduct());
    }

    private void saveProduct() {
        String productName = edtProductName.getText().toString().trim();
        String productTitle = edtProductTitle.getText().toString().trim();
        String stock = edtStock.getText().toString().trim();
        String price = edtPrice.getText().toString().trim();
        String discount = edtDiscount.getText().toString().trim();
        String description = edtDescription.getText().toString().trim();
        String createdDate = edtCreatedDate.getText().toString().trim();
        String channels = edtChannels.getText().toString().trim();

        if (TextUtils.isEmpty(productName) || TextUtils.isEmpty(productTitle)
                || TextUtils.isEmpty(stock) || TextUtils.isEmpty(price)
                || TextUtils.isEmpty(discount) || TextUtils.isEmpty(description)
                || TextUtils.isEmpty(createdDate) || TextUtils.isEmpty(channels)) {

            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int stockValue = Integer.parseInt(stock);
            double priceValue = Double.parseDouble(price);
            double discountValue = Double.parseDouble(discount);
            //... (perform additional checks if needed)
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Invalid numeric input", Toast.LENGTH_SHORT).show();
            return;
        }

        String productTypeSku = UUID.randomUUID().toString();
        Product_Type productType = new Product_Type(
                productTypeSku,
                true,
                Integer.parseInt(stock),
                Double.valueOf(price),
                Double.valueOf(discount),
                description,
                new ArrayList<>(),
                new Date(),
                new Date()
        );

        DatabaseReference productTypeRef = productTypesReference.push();
        productTypeRef.setValue(productType)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireContext(), "Product Type added successfully", Toast.LENGTH_SHORT).show();

                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    FirebaseUser currentUser = mAuth.getCurrentUser();

                    if (currentUser != null) {
                        String userID = currentUser.getUid();

                        Product product = new Product(productName, productTitle, spinnerCategory.getSelectedItem().toString(), new Date(), new ArrayList<>(Collections.singletonList(channels)), productTypeSku, userID);

                        ArrayList<String> productTypeList = new ArrayList<>();
                        productTypeList.add(productTypeSku);
                        product.setProductType(productTypeList);

                        DatabaseReference productRef = productsReference.push();
                        productRef.setValue(product)
                                .addOnSuccessListener(aVoid1 -> {
                                    Toast.makeText(requireContext(), "Product added successfully", Toast.LENGTH_SHORT).show();
                                    clearFields();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(requireContext(), "Failed to add Product", Toast.LENGTH_SHORT).show();
                                    Log.e("AddFragment", "Error adding Product to Firebase", e);

                                    productTypeRef.removeValue();
                                });
                    } else {
                        Toast.makeText(requireContext(), "Please log in to add a product", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to add Product Type", Toast.LENGTH_SHORT).show();
                    Log.e("AddFragment", "Error adding Product Type to Firebase", e);
                });
    }

    private void clearFields() {
        edtProductName.getText().clear();
        edtProductTitle.getText().clear();
        edtCreatedDate.getText().clear();
        edtChannels.getText().clear();
        edtStock.getText().clear();
        edtPrice.getText().clear();
        edtDiscount.getText().clear();
        edtDescription.getText().clear();
    }
}
