package com.example.revibemarket.LoginRegister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.revibemarket.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {
    TextView btnBackToLogin,txtHeader;
    ImageView btnBack;
    EditText edtEmail;

    Button btnForgot;
    TextInputLayout textInputEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        btnBackToLogin = findViewById(R.id.buttonBackToLogin);
        btnBack = findViewById(R.id.buttonBack);
        edtEmail = findViewById(R.id.editTextEmail);
        btnForgot = findViewById(R.id.buttonSendEmail);
        textInputEmail = findViewById(R.id.textInputEmail);

        txtHeader = findViewById(R.id.txtHeader);

        View.OnClickListener commonOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        };
        btnBack.setOnClickListener(commonOnClickListener);
        btnBackToLogin.setOnClickListener(commonOnClickListener);

        btnForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString().trim();

                if (edtEmail.length() == 0) {
                    edtEmail.setError("Email is required");
                    return ;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(edtEmail.getText().toString()).matches())
                {
                    edtEmail.setError("Invalid email address");
                    return ;
                }


                FirebaseAuth auth = FirebaseAuth.getInstance();

                auth.sendPasswordResetEmail(email)

                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                   btnForgot.setVisibility(View.GONE);
                                   textInputEmail.setVisibility(View.GONE);
                                   txtHeader.setText("We have sent you instructions to reset your password!");
                                } else {
                                    Toast.makeText(getApplicationContext(), "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
            }
        });



    }
}