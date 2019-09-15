package de.ur.hikingspots.DataStorage;

import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnCompleteListener;
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



                        Map<String, Object> locationMap = (Map) documentMap.get("spotLocation");
                        Location spotLocation = new Location((String) locationMap.get("provider"));
                        spotLocation.setAccuracy(Float.parseFloat(locationMap.get("accuracy").toString()));
                        spotLocation.setAltitude((Double) locationMap.get("altitude"));
                        spotLocation.setBearing(Float.parseFloat(locationMap.get("bearing").toString()));
                        spotLocation.setBearingAccuracyDegrees(Float.parseFloat(locationMap.get("bearingAccuracyDegrees").toString()));
                        spotLocation.setElapsedRealtimeNanos((Long) locationMap.get("elapsedRealtimeNanos"));
                        spotLocation.setLatitude((Double) locationMap.get("latitude"));
                        spotLocation.setLongitude((Double) locationMap.get("longitude"));
                        spotLocation.setSpeed(Float.parseFloat(locationMap.get("speed").toString()));
                        spotLocation.setSpeedAccuracyMetersPerSecond(Float.parseFloat(locationMap.get("speedAccuracyMetersPerSecond").toString()));
                        spotLocation.setTime((Long) locationMap.get("time"));
                        spotLocation.setVerticalAccuracyMeters(Float.parseFloat(locationMap.get("verticalAccuracyMeters").toString()));

                        String currentPhotoPath = (String) documentMap.get("currentPhotoPath");
                        FirebaseUser ownerOfSpot = currentUser;
                        Uri photoURI = null;
                        Spot spot = new Spot( spotName, spotDescription, currentPhotoPath, false, ownerOfSpot, photoURI, spotLocation);

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



                        Map<String, Object> locationMap = (Map) documentMap.get("spotLocation");
                        Location spotLocation = new Location((String) locationMap.get("provider"));
                        spotLocation.setAccuracy(Float.parseFloat(locationMap.get("accuracy").toString()));
                        spotLocation.setAltitude((Double) locationMap.get("altitude"));
                        spotLocation.setBearing(Float.parseFloat(locationMap.get("bearing").toString()));
                        spotLocation.setBearingAccuracyDegrees(Float.parseFloat(locationMap.get("bearingAccuracyDegrees").toString()));
                        spotLocation.setElapsedRealtimeNanos((Long) locationMap.get("elapsedRealtimeNanos"));
                        spotLocation.setLatitude((Double) locationMap.get("latitude"));
                        spotLocation.setLongitude((Double) locationMap.get("longitude"));
                        spotLocation.setSpeed(Float.parseFloat(locationMap.get("speed").toString()));
                        spotLocation.setSpeedAccuracyMetersPerSecond(Float.parseFloat(locationMap.get("speedAccuracyMetersPerSecond").toString()));
                        spotLocation.setTime((Long) locationMap.get("time"));
                        spotLocation.setVerticalAccuracyMeters(Float.parseFloat(locationMap.get("verticalAccuracyMeters").toString()));

                        String currentPhotoPath = (String) documentMap.get("currentPhotoPath");
                        FirebaseUser ownerOfSpot = null;
                        Uri photoURI = null;

                        Spot spot = new Spot( spotName, spotDescription, currentPhotoPath, false, ownerOfSpot, photoURI, spotLocation);

                        spots.add(spot);
                        System.out.println(spot.toString());
                    }
                }
            }
        });
        return spots;
    }


}
