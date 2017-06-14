package drgn.cafemap.Model;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import drgn.cafemap.R;



/**
 * Created by musta on 2017/06/11.
 */

public class MapsActivityModel {

    public MapsActivityModel() {
    }

    public void setUpMarkers(GoogleMap mMap, String title, double lat, double lon, String time, String socket, String wifi, String iconType) {

        MarkerOptions options = new MarkerOptions();
        options.title(title);

        LatLng location = new LatLng(lat, lon);
        options.position(location);

        options.snippet(time + "\n" + "Wi-fi: " + wifi + "\nSocket: " + socket + " ");
        if (iconType.equals("owner"))
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_operator));
        if ((iconType.equals("user")))
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_user));

        // Add marker on google map
        mMap.addMarker(options);

    }

}
