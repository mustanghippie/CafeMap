package drgn.cafemap.Model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static android.content.ContentValues.TAG;

/**
 * Created by Nobu on 2017/06/08.
 */

public class AsyncTaskMarkerSet extends AsyncTask<String, String, String> {
    // Sync
    private CountDownLatch latch;
    // Firebase realtime database
    private DatabaseReference mDatabase;
    // Firebase storage
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private StorageReference imageRef;
    private Bitmap bitmap;

    // Google Maps data
    private GoogleMap mMap;
    // Google Maps Marker hashmap
    private Map<String, HashMap<String, String>> cafeMap = new HashMap<>();
    // Cafe names list to get images of cafes
    private ArrayList<String> cafeNameArrayList = new ArrayList<>();
    // Cafe image list
    private Map<String, Bitmap> cafeBitmapMap = new HashMap<>();

    public AsyncTaskMarkerSet(GoogleMap mMap) {
        // Google Map
        this.mMap = mMap;

        // Firebase realtime database
        this.mDatabase = FirebaseDatabase.getInstance().getReference();

        // Firebase storage
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://cafemap-530a2.appspot.com");
        imageRef = storageRef.child("firebase.png");
    }

    @Override
    protected String doInBackground(String... value) {

        latch = new CountDownLatch(1);

        // Firebase realtime database
        mDatabase.child("MarkerList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String title, wifi, socket, time;
                MarkerOptions options;
                LatLng location;
                double lat, lon;
                MapsActivityModel mam = new MapsActivityModel();
                // location start from 1
                for (int i = 1; ; i++) {
                    if ((title = dataSnapshot.child("location" + String.valueOf(i)).child("name").getValue(String.class)) == null)
                        break;
                    lat = dataSnapshot.child("location" + String.valueOf(i)).child("lat").getValue(Double.class);
                    lon = dataSnapshot.child("location" + String.valueOf(i)).child("lon").getValue(Double.class);
                    time = dataSnapshot.child("location" + String.valueOf(i)).child("time").getValue(String.class);
                    socket = dataSnapshot.child("location" + String.valueOf(i)).child("socket").getValue(String.class);
                    wifi = dataSnapshot.child("location" + String.valueOf(i)).child("wifi").getValue(String.class);
                    // marker set
                    mam.setUpMarkers(mMap, title, lat, lon, time, socket, wifi, "owner");

                    // set name to ArrayList for download images
                    cafeNameArrayList.add(title.replaceAll(" ", "_").toLowerCase());

                    // make hashmap for detail page
                    String key = dataSnapshot.child("location" + String.valueOf(i)).child("lat").getValue(Double.class).toString()
                            + dataSnapshot.child("location" + String.valueOf(i)).child("lon").getValue(Double.class).toString();
                    cafeMap.put(key, cafeDetailMap(dataSnapshot, i));
                }

                latch.countDown();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        try {
            // wait for firebase realtime database
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Storage
        // カフェ名一覧
        for (String cafeName : cafeNameArrayList) {
            readImagesFromFirebase(cafeName);
        }
        return "";
    }

    private void readImagesFromFirebase(final String cafeName) {

        try {
            final File localFile = File.createTempFile(cafeName, "png");
            imageRef = storageRef.child(cafeName + ".png");

            imageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {


                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    cafeBitmapMap.put(cafeName, bitmap);
                    //latch.countDown();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("404Error possibly ", "File not found");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d("onPostExecute:", "Execute");
    }

    private HashMap<String, String> cafeDetailMap(DataSnapshot dataSnapshot, int index) {
        HashMap<String, String> cafeMap = new HashMap<>();

        cafeMap.put("name", dataSnapshot.child("location" + String.valueOf(index)).child("name").getValue(String.class));
        cafeMap.put("address", dataSnapshot.child("location" + String.valueOf(index)).child("address").getValue(String.class));
        cafeMap.put("time", dataSnapshot.child("location" + String.valueOf(index)).child("time").getValue(String.class));
        cafeMap.put("tel", dataSnapshot.child("location" + String.valueOf(index)).child("tel").getValue(String.class));
        cafeMap.put("socket", dataSnapshot.child("location" + String.valueOf(index)).child("socket").getValue(String.class));
        cafeMap.put("wifi", dataSnapshot.child("location" + String.valueOf(index)).child("wifi").getValue(String.class));

        return cafeMap;
    }

    public Map<String, Bitmap> getCafeBitmapMap() {
        return cafeBitmapMap;
    }

    public Map<String, HashMap<String, String>> getCafeMap() {
        return cafeMap;
    }
}
