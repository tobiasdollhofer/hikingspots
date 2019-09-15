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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import de.ur.hikingspots.MainActivity;
import de.ur.hikingspots.R;

public class SignupFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private EditText signupEmail;
    private EditText signupPassword;
    private EditText signupPasswordConfirm;
    private EditText signupUsername;
    private Button signUpButton;
    private TextView loginMessage;
    private ImageView logo;

    public SignupFragment(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_signup, null);
        signupEmail = layout.findViewById(R.id.signupEmail);
        signupPassword = layout.findViewById(R.id.signupPassword);
        signupPasswordConfirm = layout.findViewById(R.id.signupPasswordConfirm);
        signupUsername = layout.findViewById(R.id.signupUsername);
        signUpButton = layout.findViewById(R.id.signupButton);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount(signupEmail.getText().toString(), signupPassword.getText().toString());
            }
        });
        loginMessage = layout.findViewById(R.id.loginMessage);
        logo = layout.findViewById(R.id.loginLogo);
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
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
     * From: https://github.com/firebase/quickstart-android/blob/0f17b14ce785cf2b77e358154d987ecc30269616/auth/app
     * @param email
     * @param password
     */
    private void createAccount(String email, String password) {
        if (!validateForm()) {
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(signupUsername.getText().toString()).build();

                            user.updateProfile(profileUpdates);
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            loginMessage.setText(R.string.authFailed);
                            loginMessage.setVisibility(View.VISIBLE);
                            updateUI(null);
                        }
                    }
                });
    }

    /**
     * From: https://github.com/firebase/quickstart-android/blob/0f17b14ce785cf2b77e358154d987ecc30269616/auth/app
     * @return
     */
    private boolean validateForm() {
        boolean valid = true;

        String emailText = signupEmail.getText().toString();
        String passwordText = signupPassword.getText().toString();
        String usernameText = signupUsername.getText().toString();

        if (TextUtils.isEmpty(emailText)) {
            signupEmail.setError(getString(R.string.requiredField));
            valid = false;
        } else {
            signupEmail.setError(null);
        }


        if (TextUtils.isEmpty(passwordText)) {
            signupPassword.setError(getString(R.string.requiredField));
            valid = false;
        } else {
            signupPassword.setError(null);
        }

        String passwordConfirmText = signupPasswordConfirm.getText().toString();
        if (TextUtils.isEmpty(passwordConfirmText)) {
            signupPasswordConfirm.setError(getString(R.string.requiredField));
            valid = false;
        } else {
            signupPasswordConfirm.setError(null);
        }

        //Uses valid variable to check if fields aren't empty
        if(valid && !passwordText.equals(passwordConfirmText)){
            signupPassword.setError(getString(R.string.passwordNotIdentical));
            signupPasswordConfirm.setError(getString(R.string.passwordNotIdentical));
            valid = false;
        }

        if(valid && passwordText.length() < 6){
            signupPassword.setError(getString(R.string.passwordTooShort));
            signupPasswordConfirm.setError(getString(R.string.passwordTooShort));
            valid = false;
        }

        if(usernameText.isEmpty()){
            signupUsername.setError(getString(R.string.noUsernameSet));
            valid = false;
        }
        return valid;
    }

}
