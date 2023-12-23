package com.example.revibemarket.LoginRegister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.OAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Login extends AppCompatActivity {
    EditText edtEmail, edtPassword;
    Button btnLogin;
    TextView btnBackToRegister,buttonForgot;
    ImageView btnfacebook,btnGithub,btnAnonymous;
    FirebaseAuth mAuth;
    private static final String PREFS_NAME = "SaveLauguage";
    private static final String LANG_KEY = "language";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setInitialLanguage();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        }


        mAuth = FirebaseAuth.getInstance();
        btnfacebook = findViewById(R.id.btnFacebook);
        btnGithub = findViewById(R.id.btnGithub);
        btnAnonymous = findViewById(R.id.btnAnonymous);
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

        btnGithub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                OAuthProvider.Builder provider = OAuthProvider.newBuilder("github.com");
                // Target specific email with login hint.
                provider.addCustomParameter("login", "");
                // Request read access to a user's email addresses.
                // This must be preconfigured in the app's API permissions.
                List<String> scopes =
                        new ArrayList<String>() {
                            {
                                add("user:email");
                            }
                        };
                provider.setScopes(scopes);

                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

                Task<AuthResult> pendingResultTask = firebaseAuth.getPendingAuthResult();
                if (pendingResultTask != null) {
                    // There's something already here! Finish the sign-in for your user.
                    pendingResultTask
                            .addOnSuccessListener(
                                    new OnSuccessListener<AuthResult>() {
                                        @Override
                                        public void onSuccess(AuthResult authResult) {
                                            Log.d("github success",authResult.toString());
                                            // User is signed in.
                                            // IdP data available in
                                            // authResult.getAdditionalUserInfo().getProfile().
                                            // The OAuth access token can also be retrieved:
                                            // ((OAuthCredential)authResult.getCredential()).getAccessToken().
                                            // The OAuth secret can be retrieved by calling:
                                            // ((OAuthCredential)authResult.getCredential()).getSecret().

                                        }
                                    })
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                           Log.d("github",e.getMessage());
                                        }
                                    });
                } else {
                    // There's no pending result so you need to start the sign-in flow.
                    // See below.
                    firebaseAuth
                            .startActivityForSignInWithProvider(/* activity= */ Login.this, provider.build())
                            .addOnSuccessListener(
                                    new OnSuccessListener<AuthResult>() {
                                        @Override
                                        public void onSuccess(AuthResult authResult) {
                                            // User is signed in.
                                            // IdP data available in
                                            // authResult.getAdditionalUserInfo().getProfile().
                                            // The OAuth access token can also be retrieved:
                                            // ((OAuthCredential)authResult.getCredential()).getAccessToken().
                                            // The OAuth secret can be retrieved by calling:
                                            // ((OAuthCredential)authResult.getCredential()).getSecret().
                                            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            String userID = user.getUid();

                                            User newUser = new User(userID, authResult.getUser().getEmail());
                                            usersRef.child(userID).setValue(newUser);


                                            Intent intent = new Intent(Login.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    })
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Handle failure.
                                            Log.d("github 2",e.getMessage());
                                        }
                                    });
                }

            }
        });
        btnAnonymous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signInAnonymously()
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInAnonymously:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Intent intent = new Intent(Login.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInAnonymously:failure", task.getException());
                                    Toast.makeText(Login.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
    private void setInitialLanguage() {
        String savedLanguage = getCurrentLanguage();
        setLocale(savedLanguage);
    }
    private String getCurrentLanguage() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(PREFS_NAME, getApplicationContext().MODE_PRIVATE);
        return prefs.getString(LANG_KEY, "en"); // Default to English if not set
    }
    private void setLocale(String language) {
        Resources resources = getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(new Locale(language));
        resources.updateConfiguration(configuration, metrics);
    }

}