package de.ur.hikingspots;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseUser;


public class Spot implements Parcelable {

    private Context context;
    private String spotName;
    private String spotDescription;
    private Location spotLocation;
    private String currentPhotoPath;
    private int spotPublic;
    private FirebaseUser ownerOfSpot;
    Uri photoURI;

    public Spot(Context context, String spotName, String spotDescription, String currentPhotoPath, boolean spotPublic, FirebaseUser ownerOfSpot, Uri photoURI, Location location){
        this.context = context;
        this.spotName = spotName;
        this.spotDescription = spotDescription;
        this.currentPhotoPath = currentPhotoPath;
        if (spotPublic == true){
            this.spotPublic = Constants.SPOT_IS_PUBLIC;
        }
        else {
            this.spotPublic = Constants.SPOT_IS_PRIVATE;
        }
        spotLocation = location;
        this.ownerOfSpot = ownerOfSpot;
        this.photoURI = photoURI;
    }




    protected Spot(Parcel in) {
        spotName = in.readString();
        spotDescription = in.readString();
        currentPhotoPath = in.readString();
        spotLocation = in.readParcelable(Location.class.getClassLoader());
        spotPublic = in.readInt();
        ownerOfSpot = in.readParcelable(FirebaseUser.class.getClassLoader());
        photoURI = in.readParcelable(Uri.class.getClassLoader());
    }

    public static final Creator<Spot> CREATOR = new Creator<Spot>() {
        @Override
        public Spot createFromParcel(Parcel in) {
            return new Spot(in);
        }

        @Override
        public Spot[] newArray(int size) {
            return new Spot[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(spotName);
        dest.writeString(spotDescription);
        dest.writeString(currentPhotoPath);
        dest.writeParcelable(spotLocation, flags);
        dest.writeInt(spotPublic);
        dest.writeParcelable(ownerOfSpot, flags);
        dest.writeParcelable(photoURI, flags);
    }


    public void setSpotName(String spotName) {
        this.spotName = spotName;
    }

    public void setSpotDescription(String spotDescription) {
        this.spotDescription = spotDescription;
    }

    public void setCurrentPhotoPath(String currentPhotoPath) {
        this.currentPhotoPath = currentPhotoPath;
    }

    public void setSpotPublic(boolean spotIsPublic) {
        if (spotIsPublic == true) {
            spotPublic = Constants.SPOT_IS_PUBLIC;
        }
        else {
            spotPublic = Constants.SPOT_IS_PRIVATE;
        }
    }

    public void setPhotoURI(Uri photoURI) {
        this.photoURI = photoURI;
    }

    public String getSpotName() {
        return spotName;
    }

    public String getSpotDescription() {
        return spotDescription;
    }

    public Location getSpotLocation() {
        return spotLocation;
    }

    public String getCurrentPhotoPath(){
        return currentPhotoPath;
    }

    public int getSpotPublic(){
        return spotPublic;
    }

    public FirebaseUser getOwnerOfSpot(){
        return ownerOfSpot;
    }

    public Uri getPhotoURI(){
        return photoURI;
    }

    public void setPhotoURI(Uri photoURI) {
        this.photoURI = photoURI;
    }
}
