package com.example.revibemarket.LoginRegister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.revibemarket.AddFragment;
import com.example.revibemarket.CartFragment;
import com.example.revibemarket.ExploreFragment;
import com.example.revibemarket.HomeFragment;
import com.example.revibemarket.R;
import com.example.revibemarket.Settings.ProfileFragment;
import com.example.revibemarket.Settings.SettingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment = new HomeFragment();
    CartFragment cartFragment = new CartFragment();
    AddFragment addFragment = new AddFragment();
    ExploreFragment exploreFragment = new ExploreFragment();
    ProfileFragment profileFragment = new ProfileFragment();
    SettingFragment settingFragment = new SettingFragment();

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

    }
}