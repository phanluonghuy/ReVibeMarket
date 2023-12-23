package com.example.revibemarket.LoginRegister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

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

    private BottomNavigationView bottomNavigationView;
    private HomeFragment homeFragment = new HomeFragment();
    private CartFragment cartFragment = new CartFragment();
    private AddFragment addFragment = new AddFragment();
    private ExploreFragment exploreFragment = new ExploreFragment();
    private SettingFragment settingFragment = new SettingFragment();

    private FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction().replace(R.id.container,homeFragment).commit();
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);

        // Load sessions
        loadCartSession(this);
        loadUserSession();
    }

    private boolean onNavigationItemSelected(MenuItem item) {
        Fragment selectedFragment = null;

        int itemId = item.getItemId();

        if (itemId == R.id.home) {
            selectedFragment = homeFragment;
        } else if (itemId == R.id.cart) {
            selectedFragment = cartFragment;
        } else if (itemId == R.id.add) {
            if (FirebaseAuth.getInstance().getCurrentUser().isAnonymous()) {
                Toast.makeText(getApplicationContext(), "User needs to login!", Toast.LENGTH_SHORT).show();
                return false;
            }
            selectedFragment = addFragment;
        } else if (itemId == R.id.explore) {
            selectedFragment = exploreFragment;
        } else if (itemId == R.id.profile) {
            selectedFragment = settingFragment;
        }


        if (selectedFragment != null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, selectedFragment)
                    .addToBackStack(null)
                    .commit();
            return true;
        }

        return false;
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
        if (user.isAnonymous()) {
            return;
        }
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
