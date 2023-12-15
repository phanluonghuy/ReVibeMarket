    package com.example.revibemarket;

    import android.app.usage.NetworkStats;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.View;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.annotation.NonNull;
    import androidx.annotation.Nullable;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.recyclerview.widget.LinearLayoutManager;
    import androidx.recyclerview.widget.RecyclerView;

    import com.example.revibemarket.Adapter.DetailImageAdapter;
    import com.example.revibemarket.Adapter.HomeProductAdapter;
    import com.example.revibemarket.Models.CartItem;
    import com.example.revibemarket.Models.Product;
    import com.example.revibemarket.Models.ProductSingleton;
    import com.example.revibemarket.Models.User;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.auth.FirebaseUser;
    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseError;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;
    import com.google.firebase.database.ValueEventListener;
    import com.google.firebase.storage.FirebaseStorage;
    import com.google.firebase.storage.StorageException;
    import com.google.firebase.storage.StorageReference;
    import com.google.gson.Gson;

    import org.w3c.dom.Text;

    import java.util.ArrayList;
    import java.util.List;

    public class DetailActivity extends AppCompatActivity {
        Button btnBack;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String CurrentUserID;
        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_detail);

            btnBack = findViewById(R.id.btnBack);

            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                String productSku = bundle.getString("productSku");
                if (productSku != null) {
                    Product product = ProductSingleton.getInstance().getProductList().stream()
                            .filter(productDetail -> productSku.equals(productDetail.getSku()))
                            .findFirst()
                            .get();
                    List<String> imageUrls = product.getProductType().getImages();
                    Log.d("Detail product",product.getProductName());
                    fetchCurrentUserInformation();
                    TextView tvProductName = findViewById(R.id.tvProductName);
                    TextView tvProductDescription = findViewById(R.id.tvProductDescription);
                    TextView tvProductPrice = findViewById(R.id.tvProductPrice);
                    TextView tvProductStock = findViewById(R.id.tvProductStock);
                    TextView tvSellerAddress = findViewById(R.id.tvSellerAddress);
                    TextView tvDiscount = findViewById(R.id.tvProductDiscount);
                    EditText edtQuantity = findViewById(R.id.edtQuantity);
                    Button btnMinus = findViewById(R.id.btnMinus);
                    Button btnPlus = findViewById(R.id.btnPlus);
                    Button btnAdd = findViewById(R.id.btnAddtoCart);



//                    productList = new ArrayList<>();
//                    homeProductAdapter = new HomeProductAdapter(requireContext(), productList);
//
//                    LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
//
//                    recyclerProduct.setLayoutManager(horizontalLayoutManager);
//                    recyclerProduct.setAdapter(homeProductAdapter);

                    RecyclerView recyclerView = findViewById(R.id.RecyclerDetailImg);
                    DetailImageAdapter detailImageAdapter = new DetailImageAdapter(getApplicationContext(),imageUrls);

                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false));
                    recyclerView.setAdapter(detailImageAdapter);
                    detailImageAdapter.notifyDataSetChanged();

//                    setProductImages(product, imageAdapter);

                    setTextViewValue(tvProductName, product.getProductName(), "Product Name");
                    setTextViewValue(tvProductDescription, product.getProductType().getDescription(), "This is a detailed description of the product.");

                    String priceText = String.format("Price: $%.2f", product.getProductType().getPrice());
                    setTextViewValue(tvProductPrice, priceText, "");


                    setTextViewValue(tvProductStock, String.valueOf("Stock: " + product.getProductType().getStock()), "Stock:");

                    String userID = product.getUserID();
                    fetchUserAddress(userID, "Address: ", tvSellerAddress);

                    setTextViewValue(tvDiscount, String.format("Discount: %.1f%%", product.getProductType().getDiscount()), "No Discount");
                    btnMinus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            decrementQuantity(edtQuantity);
                        }
                    });

                    btnPlus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            incrementQuantity(edtQuantity, product.getProductType().getStock());
                        }
                    });

                    int newQuantity = getQuantityFromEditText(edtQuantity, product.getProductType().getStock());
                    edtQuantity.setText(String.valueOf(newQuantity));

                    btnAdd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int quantity = getQuantityFromEditText(edtQuantity, product.getProductType().getStock());
                            double price = product.getProductType().getPrice();
                            double discount = product.getProductType().getDiscount();
                            String productName = tvProductName.getText().toString();
                            CartItem cartItem = new CartItem(productName, price, discount, quantity, CurrentUserID);

                            DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("carts");

                            String cartItemKey = cartRef.push().getKey();

                            assert cartItemKey != null;
                            cartRef.child(cartItemKey).child("productName").setValue(cartItem.getProductName());
                            cartRef.child(cartItemKey).child("price").setValue(cartItem.getPrice());
                            cartRef.child(cartItemKey).child("discount").setValue(cartItem.getDiscount());
                            cartRef.child(cartItemKey).child("quantity").setValue(cartItem.getQuantity());
                            cartRef.child(cartItemKey).child("currentUserID").setValue(cartItem.getCurrentUserID());

                            showToast("Product added to cart");
                        }
                    });

                } else {
                    showToast("Product JSON is null");
                    finish();
                }
            } else {
                showToast("Bundle is null");
                finish();
            }

            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        private void showToast(String message) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }

        private void setTextViewValue(TextView textView, String value, String defaultValue) {
            if (textView != null) {
                textView.setText(value != null ? value : defaultValue);
            } else {
                showToast(textView.getId() + " is null");
            }
        }

        private void setProductImages(Product product, DetailImageAdapter imageAdapter) {
            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            List<String> imageUrls = new ArrayList<>();

            for (int i = 0; i < product.getProductType().getImages().size(); i++) {
                StorageReference storageReference = firebaseStorage.getReference().child("Images_Product/" + product.getSku() + "/" + i);

                storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    Log.d("DetailActivity", "Image URL: " + uri.toString());
                    imageUrls.add(uri.toString());
                    if (imageUrls.size() == product.getProductType().getImages().size()) {
                        imageAdapter.setImageUrls(imageUrls);
                    }
                    imageAdapter.notifyDataSetChanged();
                }).addOnFailureListener(exception -> {
                    Log.e("DetailActivity", "Error loading image", exception);
                    Toast.makeText(this, "Error Load Image", Toast.LENGTH_SHORT).show();
                });

            }
        }

        private void fetchUserAddress(String userID, String prefix, TextView tvSellerAddress) {
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users").child(userID);

            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String address = dataSnapshot.child("address").getValue(String.class);

                        // Tạo chuỗi đầy đủ với prefix và address
                        String fullAddress = prefix + (address != null ? address : "");

                        // Truyền giá trị fullAddress cho hàm setTextViewValue
                        setTextViewValue(tvSellerAddress, fullAddress, "Address: ");
                    } else {
                        showToast("User not found");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    showToast("Error fetching user information");
                }
            });
        }
        private void decrementQuantity(EditText edtQuantity) {
            int currentQuantity = getQuantityFromEditText(edtQuantity);
            // Giảm giá trị xuống 1 (nếu làm cho tối đa là 0, bạn có thể thay đổi điều kiện này)
            int newQuantity = Math.max(currentQuantity - 1, 1);
            edtQuantity.setText(String.valueOf(newQuantity));
        }

        private void incrementQuantity(EditText edtQuantity, int stock) {
            int currentQuantity = getQuantityFromEditText(edtQuantity);
            // Tăng giá trị lên 1 (nếu làm cho tối đa là giá trị của stock, bạn có thể thay đổi điều kiện này)
            int newQuantity = Math.min(currentQuantity + 1, stock);
            edtQuantity.setText(String.valueOf(newQuantity));
        }

        private int getQuantityFromEditText(EditText edtQuantity) {
            try {
                // Lấy giá trị hiện tại từ EditText và chuyển đổi sang kiểu int
                return Integer.parseInt(edtQuantity.getText().toString());
            } catch (NumberFormatException e) {
                // Xử lý nếu không thể chuyển đổi
                return 0;
            }
        }

        private int getQuantityFromEditText(EditText edtQuantity, int stock) {
            try {
                // Lấy giá trị hiện tại từ EditText và chuyển đổi sang kiểu int
                int quantity = Integer.parseInt(edtQuantity.getText().toString());
                // Giữ giá trị trong khoảng từ 1 đến stock
                return Math.max(1, Math.min(quantity, stock));
            } catch (NumberFormatException e) {
                // Xử lý nếu không thể chuyển đổi
                return 1;
            }
        }

        private void fetchCurrentUserInformation() {
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());

            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User currentUser = dataSnapshot.getValue(User.class);

                        if (currentUser != null) {
                           CurrentUserID = currentUser.getId();
                        }
                    } else {
                        showToast("User not found");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    showToast("Error fetching user information");
                }
            });
        }


    }

