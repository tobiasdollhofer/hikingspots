package de.ur.hikingspots.Authentication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

}
