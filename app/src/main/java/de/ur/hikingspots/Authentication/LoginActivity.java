package de.ur.hikingspots.Authentication;

import android.content.ComponentName;
import android.content.Intent;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import de.ur.hikingspots.MainActivity;
import de.ur.hikingspots.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private EditText loginEmail;
    private EditText loginPassword;
    private Button loginButton;
    private TextView loginCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar

        setContentView(R.layout.login_layout);

        loginEmail = findViewById(R.id.Login_Email);
        loginEmail.setOnClickListener(this);

        loginPassword = findViewById(R.id.Login_Password);
        loginPassword.setOnClickListener(this);

        loginButton = findViewById(R.id.Login_Button);
        loginButton.setOnClickListener(this);

        loginCreateAccount = findViewById(R.id.Login_CreateNewAccount);
        loginCreateAccount.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.Login_Button){
            signIn(loginEmail.getText().toString(), loginPassword.getText().toString());
        }else if(id == R.id.Login_CreateNewAccount){
            Intent intent = new Intent(this, CreateAccountActivity.class);
            startActivity(intent);
        }
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
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

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(LoginActivity.this, getString(R.string.authFailed),
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        /*if (!task.isSuccessful()) {
                            mStatusTextView.setText(R.string.auth_failed);
                        }*/
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }

    /**
     * From: https://github.com/firebase/quickstart-android/blob/0f17b14ce785cf2b77e358154d987ecc30269616/auth/app/src/main/java/com/google/firebase/quickstart/auth/java/EmailPasswordActivity.java
     * @return Boolean value if Form is filled.
     */
    private boolean validateForm() {
        boolean valid = true;

        String email = loginEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            loginEmail.setError(getString(R.string.requiredField));
            valid = false;
        } else {
            loginEmail.setError(null);
        }

        String password = loginPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            loginPassword.setError(getString(R.string.requiredField));
            valid = false;
        } else {
            loginPassword.setError(null);
        }

        return valid;
    }

}
