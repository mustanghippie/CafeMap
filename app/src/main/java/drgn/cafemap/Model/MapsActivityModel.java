package drgn.cafemap.Model;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by musta on 2017/06/11.
 */

public class MapsActivityModel {

    public MapsActivityModel() {
    }

    public void setUpMarkers(GoogleMap mMap, String title, double lat, double lon, String time, String socket, String wifi) {

        MarkerOptions options = new MarkerOptions();
        options.title(title);

        LatLng location = new LatLng(lat, lon);
        options.position(location);

        options.snippet(time + "\n" + "Wi-fi: " + wifi + "\nSocket: " + socket + " ");

        // Add marker on google map
        mMap.addMarker(options);

    }

}
