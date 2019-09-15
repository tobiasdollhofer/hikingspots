package de.ur.hikingspots.DataStorage;

import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

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

import de.ur.hikingspots.Spot;

public class DownloadSpot {

    public static ArrayList<Spot> downloadAllPrivateSpots(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        final ArrayList<Spot> spots = new ArrayList<Spot>();
        db.collection("spots")
                .whereEqualTo("UID", currentUser.getUid())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot querySnapshot = task.getResult();
                    for(DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()){
                        Map<String, Object> documentMap = documentSnapshot.getData();

                        String spotName = (String) documentMap.get("spotName");
                        String spotDescription = (String) documentMap.get("spotDescription");


                        Location spotLocation = new Location("");
                        spotLocation.setAltitude((Double) documentMap.get("spotLocationAltitude"));
                        spotLocation.setLatitude((Double) documentMap.get("spotLocationLatitude"));
                        spotLocation.setLongitude((Double) documentMap.get("spotLocatonLongitude"));
                        spotLocation.setTime((Long) documentMap.get("time"));

                        String currentPhotoPath = (String) documentMap.get("currentPhotoPath");

                        Uri photoURI = null;
                        final Spot spot = new Spot( spotName, spotDescription, currentPhotoPath, false, currentUser.getUid(), photoURI, spotLocation);
                        downloadImage(spot);
                        spots.add(spot);
                        System.out.println(spot.toString());
                    }
                }
            }
        });
        return spots;
    }


    public static ArrayList<Spot> downloadAllPublicSpots(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        final ArrayList<Spot> spots = new ArrayList<Spot>();
        db.collection("spots")
                .whereEqualTo("spotPublic", 0)
                .whereGreaterThan("UID", currentUser.getUid())
                .whereLessThan("UID", currentUser.getUid())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot querySnapshot = task.getResult();
                    for(DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()){
                        Map<String, Object> documentMap = documentSnapshot.getData();

                        String spotName = (String) documentMap.get("spotName");
                        String spotDescription = (String) documentMap.get("spotDescription");

                        Location spotLocation = new Location("");
                        spotLocation.setAltitude((Double) documentMap.get("spotLocationAltitude"));
                        spotLocation.setLatitude((Double) documentMap.get("spotLocationLatitude"));
                        spotLocation.setLongitude((Double) documentMap.get("spotLocatonLongitude"));
                        spotLocation.setTime((Long) documentMap.get("time"));

                        String currentPhotoPath = (String) documentMap.get("currentPhotoPath");
                        String userUID = (String) documentMap.get("UID");
                        Uri photoURI = null;

                        final Spot spot = new Spot( spotName, spotDescription, currentPhotoPath, true, userUID, photoURI, spotLocation);
                        downloadImage(spot);
                        spots.add(spot);
                        System.out.println(spot.toString());
                    }
                }
            }
        });
        return spots;
    }


    private static void downloadImage(final Spot spot){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference image = storageRef.child("img/"+spot.getFirebaseUID());

        final long ONE_MEGABYTE = 1024 * 1024;
        image.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                spot.setByteArray(bytes);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

    }

}
