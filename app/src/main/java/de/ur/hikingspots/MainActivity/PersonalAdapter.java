package de.ur.hikingspots.MainActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.ArrayList;

import de.ur.hikingspots.Constants;
import de.ur.hikingspots.R;
import de.ur.hikingspots.Spot;

public class PersonalAdapter extends ArrayAdapter<Spot> {

    private ArrayList<Spot> spotList;
    private Context context;
    private FirebaseUser currentUser;

    public PersonalAdapter(Context context, ArrayList<Spot> spotList, FirebaseUser currentUser){
        super(context, R.layout.spot_list_item, spotList);
        this.spotList = spotList;
        this.context = context;
        this.currentUser = currentUser;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = layoutInflater.inflate(R.layout.spot_list_item, null);
        }
        Spot spot = spotList.get(position);
        if (spot != null){
            TextView name = v.findViewById(R.id.text_view_name);
            TextView description = v.findViewById(R.id.text_view_description);
            ImageView imageView = v.findViewById(R.id.imageView);
            TextView textViewOwnerOfSpot = v.findViewById(R.id.text_view_owner);
            name.setText(spot.getSpotName());
            if (spot.getByteArray() != null){
                Bitmap bitmap = null;
                bitmap = BitmapFactory.decodeByteArray(spot.getByteArray(), 0, spot.getByteArray().length);
                imageView.setImageBitmap(bitmap);
            }
            else if (spot.getCurrentPhotoPath() != null) {
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), spot.getPhotoURI());
                } catch (IOException e) {
                    Log.e(Constants.LOG_KEY, Log.getStackTraceString(e));
                }
                imageView.setImageBitmap(bitmap);
            }
            else {
                imageView.setImageResource(android.R.color.transparent);
            }
            description.setText(spot.getSpotDescription());
            if (currentUser.getUid().equals(spot.getFirebaseUID())){
                if (spot.getSpotPublic() == Constants.SPOT_IS_PUBLIC) {
                    textViewOwnerOfSpot.setText(R.string.personal_adapter_your_spot_public);
                }
                else {
                    textViewOwnerOfSpot.setText(R.string.personal_adapter_your_spot_private);
                }
            }
            else {
                textViewOwnerOfSpot.setText(R.string.personal_adapter_other_spot);
            }
        }
        return v;
    }
}