package drgn.cafemap.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import drgn.cafemap.R;


/**
 * Created by musta on 2017/06/10.
 */

public class UserCafeMapModel {

    private Context context;

    public UserCafeMapModel(Context context) {
        this.context = context;
    }

    /**
     * Saves user cafe map data in cafe_user_tbl.
     */
    public void saveUserCafeMap(double lat, double lon, String cafeName, String cafeAddress, String cafeTime, String cafeTel,
                                String cafeSocket, String cafeWifi, Bitmap uploadImage) {
        Toast.makeText(context, "Now saving", Toast.LENGTH_LONG).show();

        CafeUserTblHelper cafeUserTblHelper = new CafeUserTblHelper(context);
        byte[] image = convertBitmapToByte(uploadImage);
        boolean successFlag = cafeUserTblHelper.executeInsert(lat, lon, cafeName, cafeAddress, cafeTime, cafeTel, cafeSocket, cafeWifi, image);

        if (successFlag) {
            System.out.println("Data inserted successfully");
        }

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

    public JsonObject getUserCafeMapJson() {
        String jsonObjStringUserCafeMap = "";
        try {
            InputStream in = context.openFileInput("UserCafeMap.json");
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String s;
            while ((s = reader.readLine()) != null) {
                jsonObjStringUserCafeMap += s;
            }
            reader.close();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            System.out.println("[Debug] UserCafeMap.json Not Found");
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        JsonObject jsonUserCafeMap = new Gson().fromJson(jsonObjStringUserCafeMap, JsonObject.class);
        return jsonUserCafeMap;
    }

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
        if ((iconType.equals("user"))) marker.setTag("user");
    }

    /**
     * Converts bitmap image to byte[] data.
     * sqlite3 requires blob as byte[], that's why a bitmap has to convert bitmap into byte.
     *
     * @param bitmap
     * @return byte[]
     */
    protected byte[] convertBitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        //PNG quality 100%
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

        byte[] result = byteArrayOutputStream.toByteArray();

        return result;
    }

    /**
     * Converts bitmap image to byte[] data.
     *
     * @param image
     * @return Bitmap
     */
    protected Bitmap convertByteToBitmap(byte[] image) {
        Bitmap bmp = BitmapFactory.decodeByteArray(image, 0, image.length);
        return bmp;
    }

    /**
     * Obtains image from cafe_user_tbl.
     *
     * @param lat
     * @param lon
     * @return Bitmap
     */
    public Bitmap getCafeImage(double lat, double lon) {
        CafeUserTblHelper cafeUserTblHelper = new CafeUserTblHelper(context);
        byte[] result = cafeUserTblHelper.executeSelect(lat, lon, "image");
        Bitmap image = this.convertByteToBitmap(result);
        return image;
    }

}
