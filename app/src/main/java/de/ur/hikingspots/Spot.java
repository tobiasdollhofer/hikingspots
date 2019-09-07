package de.ur.hikingspots;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;


public class Spot implements Parcelable {

    private Context context;
    private String spotName;
    private String spotDescription;
    private Location spotLocation;

    public Spot(Context context, String spotName, String spotDescription){
        this.context = context;
        this.spotName = spotName;
        this.spotDescription = spotDescription;
        spotLocation = createLocation();
    }

    protected Spot(Parcel in) {
        spotName = in.readString();
        spotDescription = in.readString();
        spotLocation = in.readParcelable(Location.class.getClassLoader());
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
        dest.writeParcelable(spotLocation, flags);
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

    private Location createLocation(){
        Location currentLocation;
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            currentLocation = null;
        }
        else {
            currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        return currentLocation;
    }
}
