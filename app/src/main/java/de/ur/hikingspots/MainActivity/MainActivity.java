package de.ur.hikingspots.MainActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Map;

import de.ur.hikingspots.AddActivity;
import de.ur.hikingspots.Authentication.LoginActivity;
import de.ur.hikingspots.Constants;
import de.ur.hikingspots.Map.MapsActivity;
import de.ur.hikingspots.R;
import de.ur.hikingspots.Settings.SettingsActivity;
import de.ur.hikingspots.Spot;

public class MainActivity extends AppCompatActivity implements DeleteDialogFragment.DeleteDialogFragmentListener {

    private FirebaseAuth mAuth;
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
        checkPreferencesAndDownload();
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
        listView = findViewById(R.id.list_view);
        spotList = new ArrayList<Spot>();
        downloadAllPrivateSpots();
        downloadAllPublicSpots();
        System.out.println("spotlistSize: " + spotList.size());
        adapter = new PersonalAdapter(this, spotList, mAuth.getCurrentUser());
        listView.setAdapter(adapter);
        registerForContextMenu(listView);
    }

    private void checkPreferencesAndDownload(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        ConnectivityManager connectionManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile = connectionManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = connectionManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        //check for internet connection
        if (wifi.isConnected() || mobile.isConnected()) {
            //
            //download your own spots always
            if (!sharedPreferences.getBoolean(getString(R.string.preference_key_wlan_own), Constants.PREFERENCE_DEFAULT_WLAN_OWN)) {
                if (sharedPreferences.getBoolean(getString(R.string.preference_key_download_own), Constants.PREFERENCE_DEFAULT_DOWNLOAD_OWN)) {
                    downloadAllPrivateSpots();
                }
            }
            //download own spots only in wifi
            else if (sharedPreferences.getBoolean(getString(R.string.preference_key_wlan_own), Constants.PREFERENCE_DEFAULT_WLAN_OWN) && wifi.isConnected()) {
                if (sharedPreferences.getBoolean(getString(R.string.preference_key_download_own), Constants.PREFERENCE_DEFAULT_DOWNLOAD_OWN)) {
                    downloadAllPrivateSpots();
                }
            }

            //download spots form others always
            if (!sharedPreferences.getBoolean(getString(R.string.preference_key_wlan_external), Constants.PREFERENCE_DEFAULT_WLAN_EXTERNAL)) {
                if (sharedPreferences.getBoolean(getString(R.string.preference_key_download_external), Constants.PREFERENCE_DEFAULT_DOWNLOAD_EXTERNAL)) {
                    downloadAllPublicSpots();
                }
            }
            //download spots from others only with wifi
            else if (sharedPreferences.getBoolean(getString(R.string.preference_key_wlan_external), Constants.PREFERENCE_DEFAULT_WLAN_EXTERNAL) && wifi.isConnected()) {
                if (sharedPreferences.getBoolean(getString(R.string.preference_key_download_external), Constants.PREFERENCE_DEFAULT_DOWNLOAD_EXTERNAL)) {
                    downloadAllPublicSpots();
                }
            }
        }
        else {
            Toast.makeText(this, R.string.internet_connection_no, Toast.LENGTH_LONG).show();
        }
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
            DialogFragment dialog = DeleteDialogFragment.newInstance(position, spotList.get(position).getSpotName(), this);
            dialog.show(getSupportFragmentManager(), getString(R.string.dialog_tag_delete));
        }
        else {
            Toast.makeText(getApplicationContext(), getString(R.string.toast_not_your_spot_delete), Toast.LENGTH_SHORT).show();
        }
    }

    //delete spot
    @Override
    public void onDialogPositiveClick(int position){
        Spot spot = spotList.get(position);
        spotList.remove(position);
        adapter.notifyDataSetChanged();
        deleteSpot(spot);
        adapter.notifyDataSetChanged();
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

            case R.id.menu_item_download:
                checkPreferencesAndDownload();
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
            deleteSpot(spotList.get(positionOfSpot));
            Intent goToAddActivityEditSpot = new Intent(MainActivity.this, AddActivity.class);
            goToAddActivityEditSpot.putExtra(getString(R.string.key_edit_spot), spotList.get(positionOfSpot));
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
            Spot newSpot = (Spot) extras.getParcelable(getString(R.string.key_result_spot));
            spotList.add(newSpot);
            adapter.notifyDataSetChanged();
        }
    }


    public void downloadAllPrivateSpots(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final FirebaseUser currentUser = mAuth.getCurrentUser();

        db.collection("spots")
                .whereEqualTo("UID", currentUser.getUid())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful()){
                    ArrayList<Spot> downloadedSpots = new ArrayList<Spot>();
                    QuerySnapshot querySnapshot = task.getResult();
                    for(DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()){
                        Map<String, Object> documentMap = documentSnapshot.getData();

                        String spotName = (String) documentMap.get("spotName");
                        String spotDescription = (String) documentMap.get("spotDescription");


                        Location spotLocation = new Location("");
                        spotLocation.setAltitude(Double.parseDouble(documentMap.get("spotLocationAltitude").toString()));
                        spotLocation.setLatitude(Double.parseDouble(documentMap.get("spotLocationLatitude").toString()));
                        spotLocation.setLongitude(Double.parseDouble(documentMap.get("spotLocationLongitude").toString()));
                        spotLocation.setTime((Long) documentMap.get("time"));

                        int publicSpotValue = Integer.parseInt(documentMap.get("spotPublic").toString());
                        boolean publicSpot;
                        if(publicSpotValue == 0){
                            publicSpot = false;
                        }else{
                            publicSpot = true;
                        }
                        Uri photoURI = null;
                        Spot spot = new Spot( spotName, spotDescription, false, currentUser.getUid(), photoURI, spotLocation);
                        downloadImage(spot, documentSnapshot.getId(), adapter);
                        downloadedSpots.add(spot);
                    }
                    spotList.addAll(downloadedSpots);
                    adapter.notifyDataSetChanged();
                    System.out.println("Size of spotList: " + spotList.size());
                }

            }
        });

    }


    public void downloadAllPublicSpots(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final FirebaseUser currentUser = mAuth.getCurrentUser();

        db.collection("spots")
                .whereEqualTo("spotPublic", 1)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    ArrayList<Spot> downloadedSpots = new ArrayList<Spot>();
                    QuerySnapshot querySnapshot = task.getResult();
                    for(DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()){
                        Map<String, Object> documentMap = documentSnapshot.getData();
                        if(!((String) documentMap.get("UID")).equals(currentUser.getUid())){
                            String spotName = (String) documentMap.get("spotName");
                            String spotDescription = (String) documentMap.get("spotDescription");

                            Location spotLocation = new Location("");
                            spotLocation.setAltitude(Double.parseDouble(documentMap.get("spotLocationAltitude").toString()));
                            spotLocation.setLatitude(Double.parseDouble(documentMap.get("spotLocationLatitude").toString()));
                            spotLocation.setLongitude(Double.parseDouble(documentMap.get("spotLocationLongitude").toString()));
                            spotLocation.setTime((Long) documentMap.get("time"));

                            String userUID = (String) documentMap.get("UID");
                            Uri photoURI = null;

                            Spot spot = new Spot( spotName, spotDescription,true, userUID, photoURI, spotLocation);
                            downloadImage(spot, documentSnapshot.getId(), adapter);
                            downloadedSpots.add(spot);
                        }
                    }
                    spotList.addAll(downloadedSpots);
                    System.out.println("Size of spotList: " + spotList.size());
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }


    private static void downloadImage(final Spot spot, String documentId, final PersonalAdapter adapter){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference image = storageRef.child("img/"+ documentId);
        System.out.println("documentid:" + documentId);

        final long ONE_MEGABYTE = 1024 * 1024;
        image.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                spot.setByteArray(bytes.clone());
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void deleteSpot(Spot spot) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final FirebaseUser currentUser = mAuth.getCurrentUser();

        db.collection("spots")
                .whereEqualTo("time", spot.getSpotLocation().getTime())
                .whereEqualTo("UID", currentUser.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        FirebaseFirestore deleteDb = FirebaseFirestore.getInstance();
                        deleteDb.collection("spots")
                                .document(queryDocumentSnapshots.getDocuments().get(0).getId())
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    }
                });

    }
}
