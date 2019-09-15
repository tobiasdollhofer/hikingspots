package de.ur.hikingspots.DataStorage;

import android.net.Uri;
import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import de.ur.hikingspots.Spot;

public class UploadSpot extends AsyncTask<Spot, Integer, Long> {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference storageReference = firebaseStorage.getReference();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = firebaseAuth.getCurrentUser();

    @Override
    protected Long doInBackground(Spot... spots) {
        final Uri file = spots[0].getPhotoURI();

        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("UID", currentUser.getUid());
        //dataMap.put("currentPhotoPath", spots[0].getCurrentPhotoPath());
        dataMap.put("spotName", spots[0].getSpotName());
        dataMap.put("spotDescription", spots[0].getSpotDescription());
        dataMap.put("spotPublic", spots[0].getSpotPublic());
        dataMap.put("spotLocationLongitude", spots[0].getSpotLocation().getLongitude());
        dataMap.put("spotLocationLatitude", spots[0].getSpotLocation().getLatitude());
        dataMap.put("spotLocationAltitude", spots[0].getSpotLocation().getAltitude());
        dataMap.put("time", spots[0].getSpotLocation().getTime());
        db.collection("spots").add(dataMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                String documentIdString = documentReference.getId();
                uploadImage(documentIdString, file);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
        return null;
    }


    private void uploadImage(String documentIdString,Uri file){

        if(file != null){
            StorageReference reference = storageReference.child("img/" + documentIdString);
            reference.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                }
            });
        }
    }

}
