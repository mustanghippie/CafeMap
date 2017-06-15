package drgn.cafemap.Model;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

/**
 * Created by musta on 2017/06/10.
 */

public class GetAddressGeocoder extends AsyncTask<String, String, String> {

    private Context context;
    private TextView addressTextView;
    private double lat;
    private double lon;

    public GetAddressGeocoder(Context context, TextView addressTextView, double lat, double lon) {
        this.context = context;
        this.addressTextView = addressTextView;
        this.lat = lat;
        this.lon = lon;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // doInBackground前処理
    }

    @Override
    protected String doInBackground(String... params) {

        Geocoder coder = new Geocoder(context);
        String address = "";
        try {
            List<Address> addresses = coder.getFromLocation(lat, lon, 1);
            address += addresses.get(0).getAddressLine(0) + addresses.get(0).getAddressLine(1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return address;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        // doInBackground後処理
        addressTextView.setText(result);
    }
}
