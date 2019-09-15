package de.ur.hikingspots;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.ur.hikingspots.Authentication.LoginActivity;
import de.ur.hikingspots.DataStorage.UploadSpot;
import de.ur.hikingspots.MainActivity.MainActivity;
import de.ur.hikingspots.Map.MapsActivity;
import de.ur.hikingspots.Settings.SettingsActivity;

public class AddActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button createButton;
    //private Button photoButton;
    private EditText nameEditText, descriptionEditText;
    private ImageView imagePreview;
    private Switch publicPrivateSwitch;
    private String currentPhotoPath;
    private Uri photoURISpot;
    private Spot spot;
    private ProgressBar progressBar;
    private TextView createPictureTextView;
    private byte[] photoByte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_activity_layout);
        mAuth = FirebaseAuth.getInstance();
        setupViews();
        Intent receivedIntent = getIntent();
        Bundle bundle = receivedIntent.getExtras();
        if (bundle != null) {
            spot = bundle.getParcelable(getString(R.string.key_edit_spot));
            setupInformation();
            setupOnClickListenerEdit();
        }
        else {
            setupOnClickListeners();
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI (currentUser);
    }

    //TODO: Change If-Structure.
    private void updateUI(FirebaseUser user) {
        if (user == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    private void setupViews(){
        createButton = findViewById(R.id.create_button);
        nameEditText = findViewById(R.id.edit_text_name);
        descriptionEditText = findViewById(R.id.edit_text_description);
        imagePreview = findViewById(R.id.image_preview);
        publicPrivateSwitch = findViewById(R.id.public_private_switch);
        createPictureTextView = findViewById(R.id.textViewNoPicture);
        registerForContextMenu(imagePreview);
    }

    //------------------------setup actionbar-------------------------------------------------------
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.floating_menu_image_preview, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_edit_option:
                takePicture();
                return true;
            case R.id.menu_delete_option:
                imagePreview.setImageBitmap(null);
                photoURISpot = null;
                currentPhotoPath = null;
                createPictureTextView.setVisibility(View.VISIBLE);
                return true;
        }
        return super.onContextItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_menu_add_activity, menu);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.menu_item_logout_add_activity:
                logout();

            case R.id.menu_item_settings_add_activity:
                openSetting();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openSetting(){
        Intent intent = new Intent(AddActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    private void logout(){
        mAuth.signOut();
        updateUI(mAuth.getCurrentUser());
    }
    //------------------------code  used for create Spot--------------------------------------------
    private void setupOnClickListeners(){
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadNewSpot();
            }
        });
        createPictureTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });
    }

    private void uploadNewSpot(){
        spot = getNewSpot();
        spot.setByteArray(photoByte);
        if (spot != null) {
            setupIntentAndFinish();
        }
    }

    private Spot getNewSpot(){
        String spotName;
        String spotDescription;
        if (!nameEditText.getText().toString().equals("")){
            spotName = nameEditText.getText().toString().trim();
            Location spotLocation = createLocation();
            if (spotLocation == null){
                return null;
            }
            boolean spotPublic = publicPrivateSwitch.isChecked();
            if (!descriptionEditText.getText().toString().equals("")){
                spotDescription = descriptionEditText.getText().toString().trim();
            }
            else {
                spotDescription = null;
            }
            Spot spot = new Spot(spotName, spotDescription, spotPublic, mAuth.getCurrentUser().getUid(), photoURISpot, spotLocation);
            return spot;
        }
        else {
            Toast.makeText(this, getString(R.string.toast_no_name), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private Location createLocation(){
        if (checkLocationPermission()){
            Location spotLocation;
            LocationManager locationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
            spotLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            return spotLocation;
        }
        else {
            askForPermission();
            return null;
        }
    }

    private void askForPermission(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    uploadNewSpot();
                } else {
                    Toast.makeText(this, R.string.toast_GPS_permission, Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private boolean checkLocationPermission(){
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }





    //-------------------------code used to edit spot-----------------------------------------------
    private void setupInformation(){
        nameEditText.setText(spot.getSpotName());
        descriptionEditText.setText(spot.getSpotDescription());
        if (spot.getSpotPublic() == Constants.SPOT_IS_PUBLIC){
            publicPrivateSwitch.setChecked(true);
        }
        if (spot.getByteArray().length != 1){
            createPictureTextView.setVisibility(View.INVISIBLE);
            Bitmap bitmap = null;
            bitmap = BitmapFactory.decodeByteArray(spot.getByteArray(), 0, spot.getByteArray().length);
            imagePreview.setImageBitmap(bitmap);
        }
    }

    private void setupOnClickListenerEdit(){
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadChanges();
            }
        });
        createPictureTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });
    }

    private void uploadChanges(){
        changeData();
        //TODO: implement spot update with editSpot
        setupIntentAndFinish();
    }

    private void changeData(){
        spot.setSpotName(nameEditText.getText().toString().trim());
        spot.setSpotDescription(descriptionEditText.getText().toString().trim());
        spot.setPhotoURI(photoURISpot);
        spot.setByteArray(photoByte);
        spot.setSpotPublic(publicPrivateSwitch.isChecked());
    }

    // --------------------code used by both--------------------------------------------------------

    //TODO: call method when update is finished
    private void setupIntentAndFinish(){
        new UploadSpot().execute(spot);
        Intent resultIntent = new Intent();
        resultIntent.putExtra(getString(R.string.key_result_spot), spot);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    //--------------------------photo function------------------------------------------------------

    private void takePicture(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try { photoFile = createImageFile(); }
            catch (IOException ex) {}
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileProvider",
                        photoFile);
                photoURISpot = photoURI;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, Constants.REQUEST_CODE_FOR_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg",storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if ((requestCode == Constants.REQUEST_CODE_FOR_PHOTO) && (resultCode == RESULT_OK)){
            Bitmap receivedBitmap = BitmapFactory.decodeFile(currentPhotoPath);
            createPictureTextView.setVisibility(View.INVISIBLE);
            byte[] byteArray = getBytesFromBitmap(receivedBitmap);
            Bitmap showBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            imagePreview.setImageBitmap(showBitmap);
            photoByte = byteArray;
        }
    }

    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }
}