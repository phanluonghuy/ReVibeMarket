package com.example.revibemarket;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.revibemarket.Models.User;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileFragment extends Fragment {
    ImageView imageView,btnBack;
    FloatingActionButton floatingActionButton;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    TextView txtHeaderName;
    EditText txtEmail,txtPhone,txtAddress,txtFullName;
    Button btnEdit;
    Uri uri = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        imageView = view.findViewById(R.id.imageViewProfile);
        floatingActionButton = view.findViewById(R.id.floatingActionButtonUpload);
        txtHeaderName = view.findViewById(R.id.textViewName);
        txtEmail = view.findViewById(R.id.editTextEmail);
        txtPhone = view.findViewById(R.id.editTextPhone);
        txtAddress = view.findViewById(R.id.editTextAddress);
        txtFullName = view.findViewById(R.id.editTextName);
        btnEdit = view.findViewById(R.id.buttonUpdate);
        btnBack = view.findViewById(R.id.buttonBackSetting);

        setEnable(false);

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

        txtFullName.setText(user.getDisplayName());
        txtHeaderName.setText(user.getDisplayName());

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user_ = snapshot.getValue(User.class);
                txtEmail.setText(user_.getEmail());
                txtPhone.setText(user_.getPhone());
                txtAddress.setText(user_.getAddress());
                setEnable(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ImagePicker.with(ProfileFragment.this)
                        .compress(1024*1024)
                        .crop()
                        .maxResultSize(380,380)
                        .start();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToSettingFragment();
            }
        });


        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Patterns.EMAIL_ADDRESS.matcher(txtEmail.getText().toString()).matches())
                {
                    txtEmail.setError("Invalid email address");
                    return;
                }
                if (uri != null) {
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference("Users/" + user.getUid());
                    storageReference.putFile(uri);
                }
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(txtFullName.getText().toString())
                        .build();

                user.updateEmail(txtEmail.getText().toString());

                user.updateProfile(profileUpdates);
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
                 User updateUser = new User(user.getUid(),txtFullName.getText().toString(),txtEmail.getText().toString(),txtAddress.getText().toString(),txtPhone.getText().toString());
                 userRef.child(user.getUid()).setValue(updateUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                     @Override
                     public void onComplete(@NonNull Task<Void> task) {
                         Toast.makeText(getContext(), "Your profile is updated", Toast.LENGTH_SHORT).show();
                        backToSettingFragment();
                     }
                 });
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uri = data.getData();
        imageView.setImageURI(uri);

    }
    private void backToSettingFragment()
    {
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null && fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {
            Toast.makeText(getContext(), "Can not go back", Toast.LENGTH_SHORT).show();
        }
    }
    private void setEnable(boolean b) {
        txtFullName.setFocusableInTouchMode(b);
        txtFullName.setFocusable(b);
        txtPhone.setFocusableInTouchMode(b);
        txtPhone.setFocusable(b);
        txtAddress.setFocusableInTouchMode(b);
        txtAddress.setFocusable(b);
        txtEmail.setFocusableInTouchMode(b);
        txtEmail.setFocusable(b);
    }
}