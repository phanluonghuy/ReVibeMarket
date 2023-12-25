package com.example.revibemarket.Settings;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.revibemarket.LoginRegister.Login;
import com.example.revibemarket.ModelsSingleton.UserSession;
import com.example.revibemarket.MyOrderFragment;
import com.example.revibemarket.MyProductFragment;
import com.example.revibemarket.R;
import com.example.revibemarket.SoldFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class SettingFragment extends Fragment {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    ImageView imageView;
    TextView textViewName;
    TextView editProfile,editPassword,editProduct,editOrder,editLanguage,editSold;
    ProfileFragment profileFragment = new ProfileFragment();
    ChangePasswordFragment changePasswordFragment = new ChangePasswordFragment();
    MyProductFragment myProductFragment = new MyProductFragment();
    MyOrderFragment myOrderFragment = new MyOrderFragment();
    LanguageFragment languageFragment = new LanguageFragment();
    SoldFragment soldFragment = new SoldFragment();
    TextView btnLogout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        imageView = view.findViewById(R.id.profileCircleImageView);
        textViewName = view.findViewById(R.id.usernameTextView);
        editProfile = view.findViewById(R.id.profile);
        editPassword = view.findViewById(R.id.editPassword);
        editProduct = view.findViewById(R.id.product);
        editOrder = view.findViewById(R.id.orders);
        editLanguage = view.findViewById(R.id.editLanguage);
        editSold = view.findViewById(R.id.soldProduct);
        btnLogout = view.findViewById(R.id.btnLogout);



        if (!user.isAnonymous()) {
            textViewName.setText(UserSession.getInstance().getName());
            imageView.setImageBitmap(UserSession.getInstance().getImage());
        }

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.isAnonymous()) {
                    Toast.makeText(getContext(),"User need to login!",Toast.LENGTH_SHORT).show();
                    return;
                }
                FragmentManager fragmentManager = getFragmentManager();

                if (fragmentManager != null) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    fragmentTransaction.replace(R.id.container, profileFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }
        });

        editProduct.setOnClickListener(e ->{
            if (user.isAnonymous()) {
                Toast.makeText(getContext(),"User need to login!",Toast.LENGTH_SHORT).show();
                return;
            }
            FragmentManager fragmentManager = getFragmentManager();

            if (fragmentManager != null) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.replace(R.id.container, myProductFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        editOrder.setOnClickListener(e -> {
            if (user.isAnonymous()) {
                Toast.makeText(getContext(),"User need to login!",Toast.LENGTH_SHORT).show();
                return;
            }
            FragmentManager fragmentManager = getFragmentManager();

            if (fragmentManager != null) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.replace(R.id.container, myOrderFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        editSold.setOnClickListener(e ->{
            if (user.isAnonymous()) {
                Toast.makeText(getContext(),"User need to login!",Toast.LENGTH_SHORT).show();
                return;
            }
            FragmentManager fragmentManager = getFragmentManager();

            if (fragmentManager != null) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.replace(R.id.container, soldFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        editPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.isAnonymous()) {
                    Toast.makeText(getContext(),"User need to login!",Toast.LENGTH_SHORT).show();
                    return;
                }
                FragmentManager fragmentManager = getFragmentManager();

                if (fragmentManager != null) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    fragmentTransaction.replace(R.id.container, changePasswordFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }
        });

        editLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                if (fragmentManager != null) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    fragmentTransaction.replace(R.id.container, languageFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getContext(),"Log out successful",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), Login.class);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
//        reloadProfile();
    }

    private void reloadProfile() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Users/" + user.getUid());

        long maxDownloadSize = 1024 * 1024; // 1 MB

        storageReference.getBytes(maxDownloadSize)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        imageView.setImageBitmap(bitmap);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                    }
                });
        textViewName.setText(user.getDisplayName());
    }
}