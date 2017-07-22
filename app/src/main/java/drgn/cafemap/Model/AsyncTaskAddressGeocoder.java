package drgn.cafemap.Model;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.List;

import drgn.cafemap.Util.Cafe;

/**
 * Created by Nobu on 2017/06/10.
 */

public class AsyncTaskAddressGeocoder extends AsyncTask<String, String, String> {

    private Context context;
    private Cafe cafe;
    private double lat;
    private double lon;

    public AsyncTaskAddressGeocoder(Context context, Cafe cafe, double lat, double lon) {
        this.context = context;
        this.cafe = cafe;
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
        cafe.setCafeAddress(result);
    }
}
