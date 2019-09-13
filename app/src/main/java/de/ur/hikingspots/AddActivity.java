package de.ur.hikingspots;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.ur.hikingspots.Authentication.LoginActivity;

public class AddActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button createButton, photoButton;
    private EditText nameEditText, descriptionEditText;
    private ImageView imagePreview;
    private Switch publicPrivateSwitch;
    private String currentPhotoPath;
    private Uri photoURISpot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_activity_layout);
        mAuth = FirebaseAuth.getInstance();
        setupViews();
        setupOnClickListeners();
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
        photoButton = findViewById(R.id.add_photo_button);
        nameEditText = findViewById(R.id.edit_text_name);
        descriptionEditText = findViewById(R.id.edit_text_description);
        imagePreview = findViewById(R.id.image_preview);
        publicPrivateSwitch = findViewById(R.id.public_private_switch);
    }

    private void setupOnClickListeners(){
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDataAndClose();
            }
        });
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });
    }

    private void getDataAndClose(){
        String spotName;
        String spotDescription;
        boolean spotPublic = publicPrivateSwitch.isChecked();
        if (!nameEditText.getText().toString().equals("")){
            spotName = nameEditText.getText().toString().trim();
            if (!descriptionEditText.getText().toString().equals("")){
                spotDescription = descriptionEditText.getText().toString().trim();
            }
            else {
                spotDescription = null;
            }
            Spot spot = new Spot(this, spotName, spotDescription, currentPhotoPath, spotPublic, mAuth.getCurrentUser(), photoURISpot);
            Intent resultIntent = new Intent();
            resultIntent.putExtra(Constants.KEY_RESULT_SPOT, spot);
            setResult(RESULT_OK, resultIntent);
            finish();
        }
        else {

        }
    }

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
            Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
            imagePreview.setImageBitmap(bitmap);
            photoButton.setText(R.string.change_photo_button_text);
        }
    }
}