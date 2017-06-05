package drgn.cafemap;

import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.ContentValues.TAG;

/**
 * Created by Nobu on 2017/06/03.
 */

public class FirebaseConnectionModel {

    private DatabaseReference mDatabase;
    private GoogleMap mMap;

    public FirebaseConnectionModel(GoogleMap mMap) {

        this.mMap = mMap;
        this.mDatabase = FirebaseDatabase.getInstance().getReference();

        //Log.d("Debug","boot firebase");

    }

    protected void setMarkersOnGoogleMap() {

        mDatabase.child("MarkerList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                String name;
                MarkerOptions options;
                LatLng location;

                // location start from 1
                for (int i = 1; ; i++) {
                    if ((name = dataSnapshot.child("location" + String.valueOf(i)).child("name").getValue(String.class)) == null)
                        break;

                    options = new MarkerOptions();
                    options.title(name);
                    location = new LatLng(dataSnapshot.child("location" + String.valueOf(i)).child("lat").getValue(Double.class),
                            dataSnapshot.child("location" + String.valueOf(i)).child("lon").getValue(Double.class));
                    options.position(location);
                    options.snippet("Wi-fi: " + dataSnapshot.child("location" + String.valueOf(i)).child("wifi").getValue(String.class));

                    // マップにマーカー追加
                    mMap.addMarker(options);

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }
}
