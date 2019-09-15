package de.ur.hikingspots.Authentication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.ur.hikingspots.MainActivity.MainActivity;
import de.ur.hikingspots.R;

public class LoginFragment extends Fragment{

    private FirebaseAuth mAuth;
    private EditText signinEmail;
    private EditText signinPassword;
    private TextView loginMessage;
    private Button signinButton;
    private ImageView logo;

    public LoginFragment(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_signin, container, false);
        signinEmail = layout.findViewById(R.id.loginEmail);
        signinPassword = layout.findViewById(R.id.loginPassword);
        loginMessage = layout.findViewById(R.id.loginMessage);
        signinButton = layout.findViewById(R.id.signinButton);
        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn(signinEmail.getText().toString(), signinPassword.getText().toString());
            }
        });
        logo = layout.findViewById(R.id.loginLogo);
        mAuth = FirebaseAuth.getInstance();
        return layout;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }


    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(getContext(), MainActivity.class);
            getActivity().startActivity(intent);
        }
    }

    /**
     * From: https://github.com/firebase/quickstart-android/blob/0f17b14ce785cf2b77e358154d987ecc30269616/auth/app/src/main/java/com/google/firebase/quickstart/auth/java/EmailPasswordActivity.java
     * @param email E-Mail-Address for Login
     * @param password Password for Login
     */
    private void signIn(String email, String password) {
        if (!validateForm()) {
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            loginMessage.setText(R.string.authFailed);
                            loginMessage.setVisibility(View.VISIBLE);
                            updateUI(null);
                        }
                    }
                });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = signinEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            signinEmail.setError(getString(R.string.requiredField));
            valid = false;
        } else {
            signinEmail.setError(null);
        }

        String password = signinPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            signinPassword.setError(getString(R.string.requiredField));
            valid = false;
        } else {
            signinPassword.setError(null);
        }

        return valid;
    }

}
