package com.example.revibemarket.LoginRegister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;

import com.example.revibemarket.AddFragment;
import com.example.revibemarket.CartFragment;
import com.example.revibemarket.ExploreFragment;
import com.example.revibemarket.HomeFragment;
import com.example.revibemarket.Models.CartItem;
import com.example.revibemarket.Models.User;
import com.example.revibemarket.ModelsSingleton.CartSession;
import com.example.revibemarket.ModelsSingleton.UserSession;
import com.example.revibemarket.R;
import com.example.revibemarket.Settings.ProfileFragment;
import com.example.revibemarket.Settings.SettingFragment;
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
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment = new HomeFragment();
    CartFragment cartFragment = new CartFragment();
    AddFragment addFragment = new AddFragment();
    ExploreFragment exploreFragment = new ExploreFragment();
    ProfileFragment profileFragment = new ProfileFragment();
    SettingFragment settingFragment = new SettingFragment();

    FragmentManager fragmentManager = getSupportFragmentManager();

    private CartSession cartSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        getSupportFragmentManager().beginTransaction().replace(R.id.container,homeFragment).commit();
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment itemSelected = null ;
                if (item.getItemId() == R.id.home) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container,homeFragment).commit();
                    return true;
                } else if (item.getItemId() == R.id.cart) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container,cartFragment).commit();
                    return true;
                } else if (item.getItemId() == R.id.add) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container,addFragment).commit();
                    return true;
                } else if (item.getItemId() == R.id.explore) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container,exploreFragment).commit();
                    return true;
                } else if (item.getItemId() == R.id.profile) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container,settingFragment).commit();
                    return true;
                }
                return false;
            }
        });
        loadCartSession(this);
        loadUserSession();

    }
    public void loadCartSession(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("cart_session", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String cartItemsJson = sharedPreferences.getString("cart_items", null);
        String imagesUrlJson = sharedPreferences.getString("images_url", null);
        if (cartItemsJson != null && imagesUrlJson != null) {
            CartItem[] cartItems = gson.fromJson(cartItemsJson, CartItem[].class);
            String[] images = gson.fromJson(imagesUrlJson, String[].class);
            CartSession.getInstance().setCartItemList(new ArrayList<>(Arrays.asList(cartItems)));
            CartSession.getInstance().setImagesUrl(new ArrayList<>(Arrays.asList(images)));
        }
    }


    private void loadUserSession() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user_ = snapshot.getValue(User.class);
                UserSession.getInstance().setUser(user_);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Users/" + user.getUid());

        long maxDownloadSize = 1024 * 1024; // 1 MB

        storageReference.getBytes(maxDownloadSize)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        UserSession.getInstance().setImage(bitmap);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                    }
                });
    }
}