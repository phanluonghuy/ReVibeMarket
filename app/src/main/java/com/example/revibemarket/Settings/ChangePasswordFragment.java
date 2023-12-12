package com.example.revibemarket.Settings;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.revibemarket.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordFragment extends Fragment {

    ImageView btnBack;
    TextView txtOldPass,txtNewPass,txtConfirmNewPass;
    Button btnChangePassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);
        btnBack = view.findViewById(R.id.buttonBackSetting);
        txtOldPass = view.findViewById(R.id.editTextOldPass);
        txtNewPass = view.findViewById(R.id.editTextNewPass);
        txtConfirmNewPass = view.findViewById(R.id.editTextConfirmNewPass);
        btnChangePassword = view.findViewById(R.id.buttonChangePassword);


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToSettingFragment();
            }

        });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkValidPassword()) return;
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                AuthCredential credential = EmailAuthProvider
                        .getCredential(user.getEmail(), txtOldPass.getText().toString());
                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    user.updatePassword(txtNewPass.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(getContext(),"Update password successful",Toast.LENGTH_SHORT).show();
                                            backToSettingFragment();
                                        }
                                    });
                                }
                            }
                        });
            }
            private boolean checkValidPassword() {
                if (txtNewPass.length() == 0) {
                    txtNewPass.setError("Password is required");
                    return false;
                } else if (txtNewPass.length() < 8) {
                    txtNewPass.setError("Password must be minimum 8 characters");
                    return false;
                }
                if (txtNewPass.length() == 0) {
                    txtNewPass.setError("Confirm password is required");
                    return false;
                };
                if (!txtConfirmNewPass.getText().toString().equals(txtNewPass.getText().toString())) {
                    txtConfirmNewPass.setError("No match!");
                    return false;
                }
                return true;
            }
        });
        return view;
    }

    private void backToSettingFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null && fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {
            Toast.makeText(getContext(), "Can not go back", Toast.LENGTH_SHORT).show();
        }
    }

}