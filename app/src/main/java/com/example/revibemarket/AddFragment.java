package com.example.revibemarket;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.revibemarket.Adapter.ImageAdapter;
import com.example.revibemarket.Models.Product;
import com.example.revibemarket.Models.Product_Type;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class AddFragment extends Fragment {

    private EditText edtProductName, edtProductTitle, edtCreatedDate, edtChannels,
            edtStock, edtPrice, edtDiscount, edtDescription;
    private Spinner spinnerCategory;
    private Button btnSaveAddProduct;
    private DatabaseReference productsReference, productTypesReference;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private ImageView addProductImage;
    private List<Uri> uriList = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private ImageAdapter mImageAdapter;

    private static final int PICK_IMAGE_MULTIPLE = 1;

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
        addProductImage = view.findViewById(R.id.add_product_image);


        mRecyclerView = view.findViewById(R.id.reycylerViewImg);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
//
        mImageAdapter = new ImageAdapter(uriList);
        mRecyclerView.setAdapter(mImageAdapter);

        productsReference = FirebaseDatabase.getInstance().getReference().child("products");

        uriList = new ArrayList<>();
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
                android.R.style.Theme_Material_Light_Dialog_MinWidth,
                dateSetListener,
                year, month, day);

        //Objects.requireNonNull(datePickerDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.whiteTextColor)));
        datePickerDialog.show();
    }

    private void setupSaveButton() {
        btnSaveAddProduct.setOnClickListener(v -> saveProduct());
        addProductImage.setOnClickListener(v -> openImagePicker());
    }

    private void openImagePicker() {
        uriList.clear();

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == Activity.RESULT_OK && data != null) {
            boolean isImageProcessing = true;

            ArrayList<Uri> tempUriList = new ArrayList<>();

            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri uri = data.getClipData().getItemAt(i).getUri();
                    tempUriList.add(uri);
                }
            } else if (data.getData() != null) {
                Uri uri = data.getData();
                tempUriList.add(uri);
            }

            if (!tempUriList.isEmpty()) {
                //addProductImage.setImageURI(tempUriList.get(0));

                //uriList.clear();
                uriList.addAll(tempUriList);
                mImageAdapter.notifyDataSetChanged();
                mImageAdapter.addImg(tempUriList);
                Log.d("image count",mImageAdapter.getItemCount()+"");
            }

        }
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
                || TextUtils.isEmpty(createdDate)) {

            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int stockValue = Integer.parseInt(stock);
            double priceValue = Double.parseDouble(price);
            double discountValue = Double.parseDouble(discount);

        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Invalid numeric input", Toast.LENGTH_SHORT).show();
            return;
        }

        String productTypeSku =  generateUniqueKey();
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

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        String userID = currentUser.getUid();

        Product product = new Product(productName, productTitle, productType, spinnerCategory.getSelectedItem().toString(), new ArrayList<>(Collections.singletonList(channels)), productTypeSku, userID);
        DatabaseReference productRef = productsReference.push();
        productRef.setValue(product).addOnSuccessListener(aVoid -> {
                    for (int i = 0; i < uriList.size(); i++) {
                        uploadImage(uriList.get(i),  i, productTypeSku);
                    }
                    Toast.makeText(requireContext(), "Product added successfully", Toast.LENGTH_SHORT).show();
                    uriList.clear();
                    mImageAdapter.notifyDataSetChanged();
                    clearFields();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to add Product", Toast.LENGTH_SHORT).show();
                    clearFields();
                });

//        DatabaseReference productTypeRef = productTypesReference.child(productTypeSku).push();
//        productTypeRef.setValue(productType)
//                .addOnSuccessListener(aVoid -> {
//                    Toast.makeText(requireContext(), "Product Type added successfully", Toast.LENGTH_SHORT).show();
//
//                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
//                    FirebaseUser currentUser = mAuth.getCurrentUser();
//
//                    if (currentUser != null) {
//                        String userID = currentUser.getUid();
//
//                        Product product = new Product(productName, productTitle, productType, spinnerCategory.getSelectedItem().toString(), new Date(), new ArrayList<>(Collections.singletonList(channels)), productTypeSku, userID);
//
////                        ArrayList<String> productTypeList = new ArrayList<>();
////                        productTypeList.add(productTypeSku);
////                        product.setProductType(productTypeList);
//
//                        DatabaseReference productRef = productsReference.push();
//                        productRef.setValue(product)
//                                .addOnSuccessListener(aVoid1 -> {
//                                    Toast.makeText(requireContext(), "Product added successfully", Toast.LENGTH_SHORT).show();
//                                    clearFields();
//                                })
//                                .addOnFailureListener(e -> {
//                                    Toast.makeText(requireContext(), "Failed to add Product", Toast.LENGTH_SHORT).show();
//                                    productTypeRef.removeValue();
//                                });
//
//                        if (uriList != null && !uriList.isEmpty()) {
//                            for (int i = 0; i < uriList.size(); i++) {
//                                uploadImage(uriList.get(i),  i, productTypeSku);
//                            }
//                        } else {
//                            clearFields();
//                        }
//                    } else {
//                        Toast.makeText(requireContext(), "Please log in to add a product", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .addOnFailureListener(e -> {
//                    Toast.makeText(requireContext(), "Failed to add Product Type", Toast.LENGTH_SHORT).show();
//                });
    }

    private void uploadImage(Uri imageUri, int i, String productTypeSku) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Images_Product/" + productTypeSku + "/" + i);
        storageReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    Toast.makeText(requireContext(), "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                });
    }

    private String generateUniqueKey() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "").substring(0, 28);
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
        addProductImage.setImageResource(R.drawable.baseline_add_photo_alternate_24);
    }
}
