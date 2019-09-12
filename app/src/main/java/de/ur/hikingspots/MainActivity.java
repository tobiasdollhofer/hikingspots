package de.ur.hikingspots;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import de.ur.hikingspots.Authentication.LoginActivity;
import de.ur.hikingspots.DataStorage.UploadSpot;
import de.ur.hikingspots.Map.MapsActivity;

public class MainActivity extends AppCompatActivity implements DeleteDialogFragment.DeleteDialogFragmentListener {

    private FirebaseAuth mAuth;
    private Button addButton, openMap;
    private ListView listView;
    private ArrayList<Spot> spotList;
    private PersonalAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
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


    // Function checks if user is signed in. If user isn't signed in, return to LoginActivity.
    private void updateUI(FirebaseUser user) {
        if (user == null) {
            mAuth.signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    private void setup(){
        addButton = findViewById(R.id.add_Button);
        openMap = findViewById(R.id.openMap);
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
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                DialogFragment dialog = DeleteDialogFragment.newInstance(position, spotList.get(position).getSpotName());
                dialog.show(getSupportFragmentManager(),Constants.DIALOG_TAG_DELETE);
                return false;
            }
        });

        openMap.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent goToMap = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(goToMap);
            }
        });
    }

    @Override
    public void onDialogPositiveClick(int position){
        spotList.remove(position);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if ((requestCode == Constants.REQUEST_CODE_FOR_ADD_ACTIVITY) && (resultCode == RESULT_OK)){
            Bundle extras = data.getExtras();
            Spot newSpot = (Spot) extras.getParcelable(Constants.KEY_RESULT_SPOT);
            spotList.add(newSpot);
            new UploadSpot().execute(newSpot);
            adapter.notifyDataSetChanged();
        }
    }
}
