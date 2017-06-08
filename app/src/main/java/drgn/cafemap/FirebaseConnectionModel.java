package drgn.cafemap;

import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * Created by Nobu on 2017/06/03.
 */

public class FirebaseConnectionModel {

    private DatabaseReference mDatabase;
    private GoogleMap mMap;
    private Map<String, HashMap<String, String>> cafeMap = new HashMap<>();

    public FirebaseConnectionModel(GoogleMap mMap) {

        this.mMap = mMap;
        this.mDatabase = FirebaseDatabase.getInstance().getReference();

    }

    protected void setMarkersOnGoogleMap() {
        Query query = mDatabase.child("MarkerList").orderByChild("name").equalTo("HOME");

//        query.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                System.out.println("Firebase ------ onChildAdded " +dataSnapshot.getValue());
//                String name;
//                MarkerOptions options;
//                LatLng location;
//
//                // location start from 1
//                for (int i = 1; ; i++) {
//                    if ((name = dataSnapshot.child("location" + String.valueOf(i)).child("name").getValue(String.class)) == null)
//                        break;
//
//                    options = new MarkerOptions();
//                    options.title(name);
//                    location = new LatLng(dataSnapshot.child("location" + String.valueOf(i)).child("lat").getValue(Double.class),
//                            dataSnapshot.child("location" + String.valueOf(i)).child("lon").getValue(Double.class));
//                    options.position(location);
//                    options.snippet("Wi-fi: " + dataSnapshot.child("location" + String.valueOf(i)).child("wifi").getValue(String.class));
//
//                    // マップにマーカー追加
//                    mMap.addMarker(options);
//
//                }
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                System.out.println("Firebase ------ onChildChanged");
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });


        mDatabase.child("MarkerList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                String name, wifi, socket;
                MarkerOptions options;
                LatLng location;
                double lat, lon;

                // location start from 1
                for (int i = 1; ; i++) {
                    if ((name = dataSnapshot.child("location" + String.valueOf(i)).child("name").getValue(String.class)) == null)
                        break;

                    options = new MarkerOptions();
                    options.title(name);

                    lat = dataSnapshot.child("location" + String.valueOf(i)).child("lat").getValue(Double.class);
                    lon = dataSnapshot.child("location" + String.valueOf(i)).child("lon").getValue(Double.class);
                    location = new LatLng(lat, lon);
                    options.position(location);
                    wifi = dataSnapshot.child("location" + String.valueOf(i)).child("wifi").getValue(String.class);
                    socket = dataSnapshot.child("location" + String.valueOf(i)).child("socket").getValue(String.class);
                    options.snippet("Wi-fi: " + wifi + "\n" + "Socket: " + socket);

                    // マップにマーカー追加
                    mMap.addMarker(options);

                    // make hashmap for detail page
                    String key = dataSnapshot.child("location" + String.valueOf(i)).child("lat").getValue(Double.class).toString()
                            + dataSnapshot.child("location" + String.valueOf(i)).child("lon").getValue(Double.class).toString();
                    cafeMap.put(key,cafeDetailMap(dataSnapshot, i));

                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

    private HashMap<String, String> cafeDetailMap(DataSnapshot dataSnapshot, int index) {
        HashMap<String, String> cafeMap = new HashMap<>();

        cafeMap.put("name", dataSnapshot.child("location" + String.valueOf(index)).child("name").getValue(String.class));
        cafeMap.put("address",dataSnapshot.child("location" + String.valueOf(index)).child("address").getValue(String.class));
        cafeMap.put("time",dataSnapshot.child("location" + String.valueOf(index)).child("time").getValue(String.class));
        cafeMap.put("socket",dataSnapshot.child("location" + String.valueOf(index)).child("socket").getValue(String.class));
        cafeMap.put("wifi",dataSnapshot.child("location" + String.valueOf(index)).child("wifi").getValue(String.class));

        return cafeMap;
    }

    public HashMap<String,String> getCafeDetailMap(String key){

        HashMap<String,String> result = new HashMap<>();
        result = cafeMap.get(key);

        return result;
    }
}
