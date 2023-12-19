package com.example.revibemarket;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.revibemarket.Adapter.ImageAdapter;
import com.example.revibemarket.Models.Product;
import com.example.revibemarket.Models.Product_Type;
import com.example.revibemarket.ModelsSingleton.ProductSingleton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class EditActivity extends AppCompatActivity {

    private EditText edtProductName, edtProductTitle, edtCreatedDate, edtChannels,
            edtStock, edtPrice, edtDiscount, edtDescription;
    private Spinner spinnerCategory;
    private Button btnSaveAddProduct;
    private DatabaseReference productsReference;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private ImageView addProductImage;
    private List<Uri> uriList = new ArrayList<>();
    private ArrayList<String> images = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private ImageAdapter mImageAdapter;
    private Product product;

    private static final int PICK_IMAGE_MULTIPLE = 1;
    String productTypeSku;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Intent intent = getIntent();
        productTypeSku = intent.getStringExtra("sku");


        initViews();

    }

    private void initViews() {
        edtProductName = findViewById(R.id.edit_productName);
        edtProductTitle = findViewById(R.id.edt_productTitle);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        edtCreatedDate = findViewById(R.id.edt_createdDate);
        edtStock = findViewById(R.id.edt_stock);
        edtPrice = findViewById(R.id.edt_price);
        edtDiscount = findViewById(R.id.edt_discount);
        edtChannels = findViewById(R.id.edt_channels);
        edtDescription = findViewById(R.id.edt_description);
        btnSaveAddProduct = findViewById(R.id.btnSaveAddProduct);
        addProductImage = findViewById(R.id.add_product_image);


        product = ProductSingleton.getInstance().getProductList()
                .stream()
                .filter(product1 -> product1.getSku().equals(productTypeSku))
                .findFirst()
                .get();



        uriList = fetchImage(product);
        mRecyclerView = findViewById(R.id.reycylerViewImg);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mImageAdapter = new ImageAdapter(uriList);
        mRecyclerView.setAdapter(mImageAdapter);

        setupSpinner();
        setupDatePicker();
        setupSaveButton();

        productsReference = FirebaseDatabase.getInstance().getReference().child("products");
        edtProductName.setText(product.getProductName());
        edtProductTitle.setText(product.getProductTitle());
        spinnerCategory.setSelection(((ArrayAdapter<String>)spinnerCategory.getAdapter()).getPosition(product.getCategory()));
        edtCreatedDate.setText(new SimpleDateFormat("MM/dd/yyyy").format(product.getProductType().getCreated()));
        edtStock.setText(product.getProductType().getStock()+"");
        edtPrice.setText(product.getProductType().getPrice()+"");
        edtDiscount.setText(product.getProductType().getDiscount()+"");
        edtChannels.setText(product.getChannels().get(0));
        edtDescription.setText(product.getProductType().getDescription());
    }

    private ArrayList<Uri> fetchImage(Product product) {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference listRef = firebaseStorage.getReference().child("Images_Product/"+ product.getSku());
        ArrayList<Uri> imgUrl = new ArrayList<>();
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
                                    imgUrl.add(uri);
                                    mImageAdapter.notifyDataSetChanged();
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
        return imgUrl;
    }
    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
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
                this,
                android.R.style.Theme_Material_Light_Dialog_MinWidth,
                dateSetListener,
                year, month, day);

        datePickerDialog.show();
    }

    private void setupSaveButton() {
        addProductImage.setOnClickListener(v -> openImagePicker());
        btnSaveAddProduct.setOnClickListener(v -> saveProduct());
    }
    private void openImagePicker() {
//        uriList.clear();

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == Activity.RESULT_OK && data != null) {

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
                mImageAdapter.addImg(tempUriList);
                Log.d("image count", String.valueOf(mImageAdapter.getItemCount()));
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

            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int stockValue = Integer.parseInt(stock);
            double priceValue = Double.parseDouble(price);
            double discountValue = Double.parseDouble(discount);

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid numeric input", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Uri> deleteUri = mImageAdapter.getDeleteUris();

        List<String> urlNew = new ArrayList<>();
        for (Uri url : deleteUri) {
            urlNew.add(extractSkuFromUrl(url.toString()));
        }
        if (!uriList.isEmpty()) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("Images_Product/" + productTypeSku);
            storageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                @Override
                public void onSuccess(ListResult listResult) {
                    List<Task<Void>> deleteTasks = new ArrayList<>();

                    for (StorageReference item : listResult.getItems()) {
                        Log.d("uri download",item.toString());
                        int length = item.toString().length();
                        String s = item.toString().substring(length-1);
                        if (urlNew.stream()
                                .filter(s1 -> s1.equals(s))
                                .findFirst()
                                .isPresent())
                        {
                                        deleteTasks.add(item.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(getApplicationContext(), "delete image", Toast.LENGTH_SHORT).show();
                                            }
                                        }));
                        }
                    }
                    Task<Void> allDeleteTasks = Tasks.whenAll(deleteTasks);

                    allDeleteTasks.addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            for (int i = 0; i < uriList.size(); i++) {
                                String s = uriList.get(i).toString().substring(0,1);
                                if (!uriList.get(i).toString().substring(0,1).equals("h"))
                                    uploadImage(uriList.get(i), i, productTypeSku);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Failed to delete items", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Delete image failed !",Toast.LENGTH_SHORT).show();
                }
            });
        }
        if (uriList.isEmpty()) {
            Toast.makeText(this, "Please select at least one image", Toast.LENGTH_SHORT).show();
            return;
        }
        Product_Type productType = new Product_Type(
                productTypeSku,
                true,
                Integer.parseInt(stock),
                Double.valueOf(price),
                Double.valueOf(discount),
                description,
                images,
                product.getProductType().getCreated(),
                new Date()
        );

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
//
        String userID = currentUser.getUid();

        Product product = new Product(productName, productTitle, productType, spinnerCategory.getSelectedItem().toString(), new ArrayList<>(Collections.singletonList(channels)), productTypeSku, userID);
        DatabaseReference productsReference = FirebaseDatabase.getInstance().getReference();

        // Check if product with the given productTypeSku already exists
        Query productQuery = productsReference.child("products").orderByChild("sku").equalTo(productTypeSku);
        productQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Product with productTypeSku exists, update it
                    for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                        // Perform the update in the database
                        productsReference.child("products").child(productSnapshot.getKey()).setValue(product)
                                .addOnSuccessListener(aVoid -> {
                                    // Update successful
                                    Toast.makeText(getApplicationContext(), "Product updated successfully", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    // Update failed
                                    Toast.makeText(getApplicationContext(), "Failed to update product", Toast.LENGTH_SHORT).show();
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
                Toast.makeText(getApplicationContext(), "Database error", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void uploadImage(Uri imageUri, int i, String productTypeSku) {

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Images_Product/" + productTypeSku + "/" + generateUniqueKey());
        storageReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        images.add(uri.toString());
                        Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                });
    }
        private static String extractSkuFromUrl(String url) {
            String[] parts = url.split("%2F");
            if (parts.length >= 3) {
                return parts[2].split("\\?")[0];
            } else {
                return "";
            }
        }

    private String generateUniqueKey() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "").substring(0, 28);
    }

}