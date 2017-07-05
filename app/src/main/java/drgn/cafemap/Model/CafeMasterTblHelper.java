package drgn.cafemap.Model;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Nobu on 2017/06/21.
 */

public class CafeMasterTblHelper {

    private SQLiteDatabase sqLiteDatabase;
    private final String databaseName = "cafemap_db";
    private final String table = "cafe_master_tbl";

    public CafeMasterTblHelper(Context context) {
        this.sqLiteDatabase = context.openOrCreateDatabase(databaseName, MODE_PRIVATE, null);
    }

    /**
     * Obtains all data from cafe_user_tbl
     *
     * @return
     */
    protected List<Map<String, Object>> executeSelect() {
        List<Map<String, Object>> result = new ArrayList<>();

        Cursor query = sqLiteDatabase.rawQuery("SELECT * FROM "+table, null);
        boolean isEof = query.moveToFirst();
        String cafeName, cafeAddress, cafeTime, cafeTel, cafeWifi, cafeSocket;
        double lat, lon;
        byte[] cafeImage;
        while (isEof) {
            lat = Double.parseDouble(query.getString(query.getColumnIndex("lat")));
            lon = Double.parseDouble(query.getString(query.getColumnIndex("lon")));
            cafeName = query.getString(query.getColumnIndex("name"));
            cafeAddress = query.getString(query.getColumnIndex("address"));
            cafeTime = query.getString(query.getColumnIndex("time"));
            cafeTel = query.getString(query.getColumnIndex("tel"));
            cafeWifi = query.getString(query.getColumnIndex("wifi"));
            cafeSocket = query.getString(query.getColumnIndex("socket"));
//            cafeImage = query.getBlob(query.getColumnIndex("image"));
            // Set Map<String,object> that has one record from makeOneRecordOfUserCafeData
            result.add(this.makeOneRecordOfUserCafeData(lat, lon, cafeName, cafeAddress, cafeTime, cafeTel, cafeWifi, cafeSocket));

            isEof = query.moveToNext();
        }

        return result;
    }


    /**
     * Obtains 1 record.
     *
     * @param lat
     * @param lon
     * @return Map<String, Object> 1 record data
     */
    public Map<String, Object> executeSelect(double lat, double lon) {
        Map<String, Object> result = new HashMap<>();
        String latString = String.valueOf(lat);
        String lonString = String.valueOf(lon);

        final String WHERE = " WHERE lat = '" + latString + "' AND lon = '" + lonString + "'";
        Cursor query = sqLiteDatabase.rawQuery("SELECT * FROM cafe_master_tbl"+WHERE, null);
        boolean isEof = query.moveToFirst();

        while (isEof) {
            // get data
            result.put("cafeName", query.getString(query.getColumnIndex("name")));
            result.put("cafeAddress", query.getString(query.getColumnIndex("address")));
            result.put("cafeTime", query.getString(query.getColumnIndex("time")));
            result.put("cafeTel", query.getString(query.getColumnIndex("tel")));
            result.put("cafeWifi", query.getString(query.getColumnIndex("wifi")));
            result.put("cafeSocket", query.getString(query.getColumnIndex("socket")));
            isEof = query.moveToNext();
        }

        query.close();
        sqLiteDatabase.close();

        return result;
    }

    /**
     * Makes Map<String, Object> that has one record of cafe_user_tbl
     *
     * @param lat
     * @param lon
     * @param cafeName
     * @param cafeAddress
     * @param cafeTime
     * @param cafeTel
     * @param cafeWifi
     * @param cafeSocket
     * @return Map<String, Object>
     */
    private Map<String, Object> makeOneRecordOfUserCafeData(double lat, double lon, String cafeName, String cafeAddress, String cafeTime,
                                                            String cafeTel, String cafeWifi, String cafeSocket) {
        Map<String, Object> result = new HashMap<>();

        result.put("lat", lat);
        result.put("lon", lon);
        result.put("cafeName", cafeName);
        result.put("cafeAddress", cafeAddress);
        result.put("cafeTime", cafeTime);
        result.put("cafeTel", cafeTel);
        result.put("cafeWifi", cafeWifi);
        result.put("cafeSocket", cafeSocket);

        return result;
    }

}