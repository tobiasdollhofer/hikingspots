package de.ur.hikingspots.MainActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import de.ur.hikingspots.AddActivity;
import de.ur.hikingspots.Authentication.LoginActivity;
import de.ur.hikingspots.Constants;
import de.ur.hikingspots.DataStorage.DownloadSpot;
import de.ur.hikingspots.Map.MapsActivity;
import de.ur.hikingspots.R;
import de.ur.hikingspots.Settings.SettingsActivity;
import de.ur.hikingspots.Spot;

public class MainActivity extends AppCompatActivity implements DeleteDialogFragment.DeleteDialogFragmentListener {

    private FirebaseAuth mAuth;
    //private Button addButton, openMap, logoutButton;
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
        checkPreferences();
        //setupClickListener();
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
        /*addButton = findViewById(R.id.add_Button);
        openMap = findViewById(R.id.openMap);
        logoutButton = findViewById(R.id.logout_Button);*/
        listView = findViewById(R.id.list_view);
        spotList = new ArrayList<Spot>();
        adapter = new PersonalAdapter(this, spotList, mAuth.getCurrentUser());
        listView.setAdapter(adapter);
        registerForContextMenu(listView);
    }

    /*private void setupClickListener(){
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddActivityForNewSpot();
            }
        });


        openMap.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                openMaps();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
    }*/

    private void checkPreferences(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        ConnectivityManager connectionManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectionManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        //download own spots only in wifi
        if (sharedPreferences.getBoolean(getString(R.string.preference_key_wlan_own), Constants.PREFERENCE_DEFAULT_WLAN_OWN) && wifi.isConnected()){
            if (sharedPreferences.getBoolean(getString(R.string.preference_key_download_own), Constants.PREFERENCE_DEFAULT_DOWNLOAD_OWN)){
                downloadOwnSpots();
            }
        }
        if (sharedPreferences.getBoolean(getString(R.string.preference_key_wlan_external), Constants.PREFERENCE_DEFAULT_WLAN_EXTERNAL) && wifi.isConnected()){
            if (sharedPreferences.getBoolean(getString(R.string.preference_key_download_external), Constants.PREFERENCE_DEFAULT_DOWNLOAD_EXTERNAL)){
                dowloadExternalSpots();
            }
        }
        if (!sharedPreferences.getBoolean(getString(R.string.preference_key_wlan_own), Constants.PREFERENCE_DEFAULT_WLAN_OWN)){
            if (sharedPreferences.getBoolean(getString(R.string.preference_key_download_own), Constants.PREFERENCE_DEFAULT_DOWNLOAD_OWN)){
                downloadOwnSpots();
            }
        }
        if (!sharedPreferences.getBoolean(getString(R.string.preference_key_wlan_external), Constants.PREFERENCE_DEFAULT_WLAN_EXTERNAL) && wifi.isConnected()){
            if (sharedPreferences.getBoolean(getString(R.string.preference_key_download_external), Constants.PREFERENCE_DEFAULT_DOWNLOAD_EXTERNAL)){
                dowloadExternalSpots();
            }
        }
    }

    private void downloadOwnSpots(){
        //TODO: download own spots
    }

    private void dowloadExternalSpots(){
        //TODO: download spot form other users
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.floating_menu_listview, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int positionOfSpot = info.position;
        switch (item.getItemId()) {
            case R.id.menu_edit_option:
                openAddActivityToEdit(positionOfSpot);
                return true;

            case R.id.menu_delete_option:
                openDeleteDialog(positionOfSpot);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    private void openDeleteDialog(int position){
        if (mAuth.getCurrentUser().getUid().equals(spotList.get(position).getFirebaseUID())) {
            DialogFragment dialog = DeleteDialogFragment.newInstance(position, spotList.get(position).getSpotName());
            dialog.show(getSupportFragmentManager(), Constants.DIALOG_TAG_DELETE);
        }
        else {
            Toast.makeText(getApplicationContext(), getString(R.string.toast_not_your_spot_delete), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDialogPositiveClick(int position){
        spotList.remove(position);
        adapter.notifyDataSetChanged();
        //TODO: implement delete function
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_maps:
                openMaps();
                return true;

            case R.id.menu_item_new_spot:
                openAddActivityForNewSpot();
                return true;

            case R.id.menu_item_logout:
                logout();
                return true;

            case R.id.menu_item_settings:
                openSetting();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openSetting(){
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    private void logout(){
        mAuth.signOut();
        updateUI(mAuth.getCurrentUser());
    }

    private void openMaps(){
        Intent goToMap = new Intent(MainActivity.this, MapsActivity.class);
        goToMap.putParcelableArrayListExtra("spot", spotList);
        startActivity(goToMap);
    }

    private void openAddActivityToEdit(int positionOfSpot){
        if (mAuth.getCurrentUser().getUid().equals(spotList.get(positionOfSpot).getFirebaseUID())) {
            Intent goToAddActivityEditSpot = new Intent(MainActivity.this, AddActivity.class);
            goToAddActivityEditSpot.putExtra(Constants.KEY_EDIT_SPOT, spotList.get(positionOfSpot));
            spotList.remove(positionOfSpot);
            startActivityForResult(goToAddActivityEditSpot, Constants.REQUEST_CODE_FOR_ADD_ACTIVITY);
        }
        else {
            Toast.makeText(getApplicationContext(), getString(R.string.toast_not_your_spot_edit), Toast.LENGTH_SHORT).show();
        }
    }

    private void openAddActivityForNewSpot(){
        Intent goToAddActivityCreateSpot = new Intent(MainActivity.this, AddActivity.class);
        startActivityForResult(goToAddActivityCreateSpot, Constants.REQUEST_CODE_FOR_ADD_ACTIVITY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == Constants.REQUEST_CODE_FOR_ADD_ACTIVITY && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            Spot newSpot = (Spot) extras.getParcelable(Constants.KEY_RESULT_SPOT);
            spotList.add(newSpot);
            //new UploadSpot().execute(newSpot);
            adapter.notifyDataSetChanged();
        }
    }
}
