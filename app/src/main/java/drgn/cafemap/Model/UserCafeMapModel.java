package drgn.cafemap.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import drgn.cafemap.R;


/**
 * Created by Nobu on 2017/06/10.
 */

public class UserCafeMapModel {

    private Context context;
    private ArrayList<Marker> markerArrayList = new ArrayList<>();
    private final String TAG = "[Log] UserCafeMapModel";

    public UserCafeMapModel(Context context) {
        this.context = context;
    }

    /**
     * Sets up markers on Google Map by using cafe_user_tbl and cafe_master_tbl.
     * Reads cafe_master_tbl and sets up markers first, then reads cafe_user_tbl
     * and override markers.
     *
     * @param mMap
     */
    public void setCafeMapMarkers(GoogleMap mMap) {
        String cafeName, cafeWifi, cafeSocket, cafeTime;
        double lat, lon;

        // connect to cafe_master_tbl
        CafeMasterTblHelper cafeMasterTblHelper = new CafeMasterTblHelper(context);
        // connect to cafe_user_tbl
        CafeUserTblHelper cafeUserTblHelper = new CafeUserTblHelper(context);

        // get all data from cafe_master_tbl
        List<Map<String, Object>> cafeMasterMapList = cafeMasterTblHelper.executeSelect();
        // get all data from cafe_user_tbl
        List<Map<String, Object>> cafeUserMapList = cafeUserTblHelper.executeSelect();

        // cafe master marker
        for (Map<String, Object> maps : cafeMasterMapList) {
            cafeName = maps.get("cafeName").toString();
            cafeTime = maps.get("cafeTime").toString();
            cafeWifi = maps.get("cafeWifi").toString();
            cafeSocket = maps.get("cafeSocket").toString();
            lat = Double.parseDouble(maps.get("lat").toString());
            lon = Double.parseDouble(maps.get("lon").toString());
            this.setUpMarkers(mMap, lat, lon, cafeName, cafeTime, cafeWifi, cafeSocket, "owner");
        }

        // cafe user marker
        for (Map<String, Object> maps : cafeUserMapList) {
            cafeName = maps.get("cafeName").toString();
            cafeTime = maps.get("cafeTime").toString();
            cafeWifi = maps.get("cafeWifi").toString();
            cafeSocket = maps.get("cafeSocket").toString();
            lat = Double.parseDouble(maps.get("lat").toString());
            lon = Double.parseDouble(maps.get("lon").toString());
            this.setUpMarkers(mMap, lat, lon, cafeName, cafeTime, cafeWifi, cafeSocket, "user");
        }

    }

    public void setCafeMapMarkers(GoogleMap mMap, String searchCafeName) {
        String cafeName, cafeWifi, cafeSocket, cafeTime;
        double lat, lon;
        List<Map<String, Object>> cafeMasterMapList = new ArrayList<>();
        List<Map<String, Object>> cafeUserMapList = new ArrayList<>();

        // connect to cafe_master_tbl
        CafeMasterTblHelper cafeMasterTblHelper = new CafeMasterTblHelper(context);
        // connect to cafe_user_tbl
        CafeUserTblHelper cafeUserTblHelper = new CafeUserTblHelper(context);
        // get all data from cafe_master_tbl
        cafeMasterMapList = cafeMasterTblHelper.executeSelect("name", searchCafeName);
        // get all data from cafe_user_tbl
        cafeUserMapList = cafeUserTblHelper.executeSelect("name", searchCafeName);


        // cafe master marker
        for (Map<String, Object> maps : cafeMasterMapList) {
            cafeName = maps.get("cafeName").toString();
            cafeTime = maps.get("cafeTime").toString();
            cafeWifi = maps.get("cafeWifi").toString();
            cafeSocket = maps.get("cafeSocket").toString();
            lat = Double.parseDouble(maps.get("lat").toString());
            lon = Double.parseDouble(maps.get("lon").toString());
            this.setUpMarkers(mMap, lat, lon, cafeName, cafeTime, cafeWifi, cafeSocket, "owner");
        }

        // cafe user marker
        for (Map<String, Object> maps : cafeUserMapList) {
            cafeName = maps.get("cafeName").toString();
            cafeTime = maps.get("cafeTime").toString();
            cafeWifi = maps.get("cafeWifi").toString();
            cafeSocket = maps.get("cafeSocket").toString();
            lat = Double.parseDouble(maps.get("lat").toString());
            lon = Double.parseDouble(maps.get("lon").toString());
            this.setUpMarkers(mMap, lat, lon, cafeName, cafeTime, cafeWifi, cafeSocket, "user");
        }

    }

    public void setCafeMapMarkers(GoogleMap mMap, boolean bookmarkFlag) {
        String cafeName, cafeWifi, cafeSocket, cafeTime;
        double lat, lon;
        List<Map<String, Object>> cafeMasterMapList = new ArrayList<>();
        List<Map<String, Object>> cafeUserMapList = new ArrayList<>();
        String bookmarkValue = "0";
        if (bookmarkFlag) bookmarkValue = "1"; // 0 => flag/false 1 => flag/true

        // connect to cafe_master_tbl
        CafeMasterTblHelper cafeMasterTblHelper = new CafeMasterTblHelper(context);
        // connect to cafe_user_tbl
        CafeUserTblHelper cafeUserTblHelper = new CafeUserTblHelper(context);
        // get all data from cafe_master_tbl
        cafeMasterMapList = cafeMasterTblHelper.executeSelect("bookmark", bookmarkValue);
        // get all data from cafe_user_tbl
        cafeUserMapList = cafeUserTblHelper.executeSelect("bookmark", bookmarkValue);

        // cafe master marker
        for (Map<String, Object> maps : cafeMasterMapList) {
            cafeName = maps.get("cafeName").toString();
            cafeTime = maps.get("cafeTime").toString();
            cafeWifi = maps.get("cafeWifi").toString();
            cafeSocket = maps.get("cafeSocket").toString();
            lat = Double.parseDouble(maps.get("lat").toString());
            lon = Double.parseDouble(maps.get("lon").toString());
            this.setUpMarkers(mMap, lat, lon, cafeName, cafeTime, cafeWifi, cafeSocket, "owner");
        }

        // cafe user marker
        for (Map<String, Object> maps : cafeUserMapList) {
            cafeName = maps.get("cafeName").toString();
            cafeTime = maps.get("cafeTime").toString();
            cafeWifi = maps.get("cafeWifi").toString();
            cafeSocket = maps.get("cafeSocket").toString();
            lat = Double.parseDouble(maps.get("lat").toString());
            lon = Double.parseDouble(maps.get("lon").toString());
            this.setUpMarkers(mMap, lat, lon, cafeName, cafeTime, cafeWifi, cafeSocket, "user");
        }

    }

    /**
     * Sets markers on google map.
     *
     * @param mMap
     * @param lat
     * @param lon
     * @param cafeName
     * @param cafeTime
     * @param cafeWifi
     * @param cafeSocket
     * @param iconType
     */
    private void setUpMarkers(GoogleMap mMap, double lat, double lon, String cafeName, String cafeTime, String cafeWifi, String cafeSocket, String iconType) {
        Marker marker;
        MarkerOptions options = new MarkerOptions();
        options.title(cafeName);

        LatLng location = new LatLng(lat, lon);
        options.position(location);

        options.snippet(cafeTime + "\n" + "Wi-fi: " + cafeWifi + "\nSocket: " + cafeSocket + " ");
        if (iconType.equals("owner"))
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_operator));
        if ((iconType.equals("user"))) {
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_user));
            options.zIndex(1.0f);
        }

        // Add marker on google map
        marker = mMap.addMarker(options);
        // Set tag
        if (iconType.equals("owner")) marker.setTag("owner");
        if (iconType.equals("user")) marker.setTag("user");

        // save marker
        markerArrayList.add(marker);

    }

    /**
     * Searches cafe by search text
     *
     * @param searchText
     */
    public void searchCafe(GoogleMap mMap, String searchText) {
        this.removeMarkers();
        this.setCafeMapMarkers(mMap, searchText);
    }

    public void displayBookmarkedCafe(GoogleMap mMap) {
        this.removeMarkers();
        this.setCafeMapMarkers(mMap, true);
    }

    /**
     * Removes all markers on the map.
     */
    private void removeMarkers() {

        for (Marker marker : markerArrayList) {
            marker.remove();
        }

    }

    /**
     * Converts bitmap image to byte[] data.
     *
     * @param image
     * @return Bitmap
     */
    private Bitmap convertByteToBitmap(byte[] image) {
        Bitmap bmp = BitmapFactory.decodeByteArray(image, 0, image.length);
        return bmp;
    }

    /**
     * Obtains image from cafe_user_tbl.
     *
     * @param lat
     * @param lon
     * @return Bitmap or null(Exception)
     */
    public Bitmap getCafeImage(double lat, double lon) {

        CafeUserTblHelper cafeUserTblHelper = new CafeUserTblHelper(context);
        byte[] result = new byte[0];
        try {
            result = cafeUserTblHelper.executeSelectImage(lat, lon);
        } catch (CafeUserTblHelper.MemoryOverOverflowException e) {
            Log.d(TAG, "IllegalStateException@getCafeImage");
            Toast.makeText(context, "Failed to read a image.\nA image is probably too large size.", Toast.LENGTH_LONG).show();
            result = null;
        }
        // Exception that image couldn't read from database
        if (result == null) return null;
        Bitmap image = this.convertByteToBitmap(result);
        return image;
    }

}
