package de.ur.hikingspots.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.ur.hikingspots.MainActivity;
import de.ur.hikingspots.R;

public class CreateAccountActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private EditText email;
    private EditText password;
    private EditText passwordConfirm;
    private Button signUpButton;
    //private TextView loginCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        setContentView(R.layout.activity_create_account);
        email = findViewById(R.id.Create_Email);
        email.setOnClickListener(this);

        password = findViewById(R.id.Create_Password);
        password.setOnClickListener(this);

        passwordConfirm = findViewById(R.id.Create_Password_Confirm);
        passwordConfirm.setOnClickListener(this);

        signUpButton = findViewById(R.id.CreateAccount_Button);
        signUpButton.setOnClickListener(this);

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
        if(v.getId() == R.id.CreateAccount_Button){
            createAccount(email.getText().toString(), password.getText().toString());
        }
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
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
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(CreateAccountActivity.this, getString(R.string.authFailed),
                                    Toast.LENGTH_SHORT).show();
                            System.out.println(task.getException());
                            updateUI(null);
                        }
                    }
                });
        // [END create_user_with_email]
    }

    /**
     * From: https://github.com/firebase/quickstart-android/blob/0f17b14ce785cf2b77e358154d987ecc30269616/auth/app
     * @return
     */
    private boolean validateForm() {
        boolean valid = true;

        String emailText = email.getText().toString();
        if (TextUtils.isEmpty(emailText)) {
            email.setError(getString(R.string.requiredField));
            valid = false;
        } else {
            email.setError(null);
        }

        String passwordText = password.getText().toString();
        if (TextUtils.isEmpty(passwordText)) {
            password.setError(getString(R.string.requiredField));
            valid = false;
        } else {
            password.setError(null);
        }

        String passwordConfirmText = passwordConfirm.getText().toString();
        if (TextUtils.isEmpty(passwordConfirmText)) {
            passwordConfirm.setError(getString(R.string.requiredField));
            valid = false;
        } else {
            passwordConfirm.setError(null);
        }

        //Uses valid variable to check if fields aren't empty
        if(valid && !passwordText.equals(passwordConfirmText)){
            password.setError(getString(R.string.passwordNotIdentical));
            passwordConfirm.setError(getString(R.string.passwordNotIdentical));
            valid = false;
        }

        if(valid && passwordText.length() < 6){
            password.setError(getString(R.string.passwordTooShort));
            passwordConfirm.setError(getString(R.string.passwordTooShort));
            valid = false;
        }

        return valid;
    }
}
