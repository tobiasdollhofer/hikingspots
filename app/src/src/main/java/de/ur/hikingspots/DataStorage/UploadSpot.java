package de.ur.hikingspots.DataStorage;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import de.ur.hikingspots.Spot;

public class UploadSpot extends AsyncTask<Spot, Integer, Long> {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected Long doInBackground(Spot... spots) {
       db.collection("spots").add(spots[0]).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
           @Override
           public void onSuccess(DocumentReference documentReference) {

           }
       }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {

           }
       });
        return null;
    }


}
