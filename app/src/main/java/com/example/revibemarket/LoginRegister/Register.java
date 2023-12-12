package com.example.revibemarket.LoginRegister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.revibemarket.Models.User;
import com.example.revibemarket.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {
    EditText edtEmail, edtPassword, edtConfirmPassword, edtName, edtAddress, edtPhone;
    Button btnRegister;
    TextView btnBackLogin;
    ImageView imageViewLogin;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        edtEmail = findViewById(R.id.editTextEmail);
        edtPassword = findViewById(R.id.editTextPassword);
        edtConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        edtName = findViewById(R.id.editTextName);
        edtPhone = findViewById(R.id.editTextPhone);
        edtAddress = findViewById(R.id.editTextAddress);
        btnRegister = findViewById(R.id.buttonRegister);
        btnBackLogin = findViewById(R.id.buttonBackToLogin);
        imageViewLogin = findViewById(R.id.imageViewLogin);

        View.OnClickListener loginClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
                finish();
            }
        };
        imageViewLogin.setOnClickListener(loginClickListener);
        btnBackLogin.setOnClickListener(loginClickListener);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SuspiciousIndentation")
            @Override
            public void onClick(View v) {
                String name = edtName.getText().toString();
                String email = edtEmail.getText().toString();
                String password = edtPassword.getText().toString();
                String address = edtAddress.getText().toString();
                String phone = edtPhone.getText().toString();

                if (!CheckAllFields()) return;

                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Toast.makeText(Register.this, "Your account is created !",
                                        Toast.LENGTH_SHORT).show();



                                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
                                FirebaseUser user = mAuth.getCurrentUser();
                                String userID = user.getUid();

                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(name)
                                        .build();
                                user.updateProfile(profileUpdates);

                                User newUser = new User(userID, name, email, address, phone);
                                usersRef.child(userID).setValue(newUser);

                                Intent intent = new Intent(Register.this,Login.class);
                                startActivity(intent);
                                finish();
//
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(Register.this, "Authentication failed!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            }

            private boolean CheckAllFields() {
                if (edtName.length() == 0) {
                    edtName.setError("Name is required");
                    return false;
                }

                if (edtPhone.length() == 0) {
                    edtPhone.setError("Phone is required");
                    return false;
                }

                if (edtEmail.length() == 0) {
                    edtEmail.setError("Email is required");
                    return false;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(edtEmail.getText().toString()).matches())
                {
                    edtEmail.setError("Invalid email address");
                    return false;
                }

                if (edtPassword.length() == 0) {
                    edtPassword.setError("Password is required");
                    return false;
                } else if (edtPassword.length() < 8) {
                    edtPassword.setError("Password must be minimum 8 characters");
                    return false;
                }

                if (edtConfirmPassword.length() == 0) {
                    edtConfirmPassword.setError("Confirm password is required");
                    return false;
                }

                if (!edtConfirmPassword.getText().toString().equals(edtPassword.getText().toString())) {
                    edtConfirmPassword.setError("No match!");
                    return false;
                }

                if (edtAddress.length() == 0) {
                    edtAddress.setError("Address is required");
                    return false;
                }


                // after all validation return true.
                return true;
            }
        });

    }
}