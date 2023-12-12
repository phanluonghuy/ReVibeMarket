package com.example.revibemarket.LoginRegister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.revibemarket.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    EditText edtEmail, edtPassword;
    Button btnLogin;
    TextView btnBackToRegister,buttonForgot;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }


        mAuth = FirebaseAuth.getInstance();
        edtEmail = findViewById(R.id.editTextEmail);
        edtPassword = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.buttonLogin);
        btnBackToRegister = findViewById(R.id.buttonRegister);
        buttonForgot = findViewById(R.id.buttonForgot);

        btnBackToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
                finish();
            }
        });

        buttonForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, ForgotPassword.class);
                startActivity(intent);
            }
        });

        String TAG = "Login";
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edtEmail.getText().toString();
                String password = edtPassword.getText().toString();
                if (!Patterns.EMAIL_ADDRESS.matcher(edtEmail.getText().toString()).matches())
                {
                    edtEmail.setError("Invalid email address");
                    return;
                }
                if (edtPassword.length() == 0) {
                    edtPassword.setError("Password is required");
                    return;
                }
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
//                                    Log.d(TAG, "signInWithEmail:success");
                                    Toast.makeText(Login.this, "Login successful!",
                                            Toast.LENGTH_SHORT).show();
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Intent intent = new Intent(Login.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                    //updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(Login.this, "Login failed!",
                                            Toast.LENGTH_SHORT).show();
//                                    updateUI(null);
                                }
                            }
                        });
            }
        });
    }
}