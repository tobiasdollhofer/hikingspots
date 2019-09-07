package de.ur.hikingspots;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import de.ur.hikingspots.Authentication.LoginActivity;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button addButton;
    private ListView listView;
    private ArrayList<Spot> spotList;
    private PersonalAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        setup();
        setupClickListener();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    //TODO: Change If-Structure.
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            mAuth.signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    private void setup(){
        addButton = findViewById(R.id.add_Button);
        listView = findViewById(R.id.list_view);
        spotList = new ArrayList<Spot>();
        adapter = new PersonalAdapter(this, spotList);
        listView.setAdapter(adapter);
    }

    private void setupClickListener(){
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToAddActivity = new Intent(MainActivity.this, AddActivity.class);
                startActivityForResult(goToAddActivity, Constants.REQUEST_CODE_FOR_ADD_ACTIVITY);
            }
        });
        /* TODO: implement functions
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return false;
            }
        });*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if ((requestCode == Constants.REQUEST_CODE_FOR_ADD_ACTIVITY) && (resultCode == RESULT_OK)){
            Bundle extras = data.getExtras();
            Spot newSpot = (Spot) extras.getParcelable(Constants.KEY_RESULT_SPOT);
            spotList.add(newSpot);
            adapter.notifyDataSetChanged();
        }
    }
}
