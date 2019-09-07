package de.ur.hikingspots;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class PersonalAdapter extends ArrayAdapter<Spot> {

    private ArrayList<Spot> spotList;
    private Context context;

    public PersonalAdapter(Context context, ArrayList<Spot> spotList){
        super(context, R.layout.spot_list_item, spotList);
        this.spotList = spotList;
        this.context = context;
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
            name.setText(spot.getSpotName());
            description.setText(spot.getSpotDescription());
        }
        return v;
    }
}