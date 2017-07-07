package drgn.cafemap.Model;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Nobu on 2017/06/21.
 */

public class CafeUserTblHelper {

    private SQLiteDatabase sqLiteDatabase;
    private final String databaseName = "cafemap_db";
    private final String TAG = "[TAG]CafeUserTblHelper:";

    public CafeUserTblHelper(Context context) {
        this.sqLiteDatabase = context.openOrCreateDatabase(databaseName, MODE_PRIVATE, null);
    }

    /**
     * Obtains 1 record.
     *
     * @param lat
     * @param lon
     * @return Map<String, String> 1 record data
     */
    protected Map<String, Object> executeSelect(double lat, double lon) {
        Map<String, Object> result = new HashMap<>();
        String latString = String.valueOf(lat);
        String lonString = String.valueOf(lon);

        final String WHERE = " WHERE lat = '" + latString + "' AND lon = '" + lonString + "'";
        Cursor query = sqLiteDatabase.rawQuery("SELECT * FROM cafe_user_tbl" + WHERE, null);
        boolean isEof = query.moveToFirst();

        while (isEof) {
            // get data
            result.put("cafeName", query.getString(query.getColumnIndex("name")));
            result.put("cafeAddress", query.getString(query.getColumnIndex("address")));
            result.put("cafeTime", query.getString(query.getColumnIndex("time")));
            result.put("cafeTel", query.getString(query.getColumnIndex("tel")));
            result.put("cafeWifi", query.getString(query.getColumnIndex("wifi")));
            result.put("cafeSocket", query.getString(query.getColumnIndex("socket")));
            result.put("cafeImage", query.getBlob(query.getColumnIndex("image")));
            isEof = query.moveToNext();
        }
        query.close();
        sqLiteDatabase.close();

        return result;
    }

    protected byte[] executeSelect(double lat, double lon, String image) {
        byte[] result = null;
        String latString = String.valueOf(lat);
        String lonString = String.valueOf(lon);

        Cursor query = sqLiteDatabase.rawQuery("SELECT image FROM cafe_user_tbl WHERE lat = '" + latString + "' AND lon = '" + lonString + "'", null);
        boolean isEof = query.moveToFirst();

        while (isEof) {
            // get data
            result = query.getBlob(query.getColumnIndex("image"));
            isEof = query.moveToNext();
        }
        query.close();
        sqLiteDatabase.close();

        return result;
    }

    /**
     * Checks cafe info sent or unsent from table.
     *
     * @param lat
     * @param lon
     * @return boolean true => already sent / false => not yet
     */
    protected boolean checkSendFlag(double lat, double lon) {
        String latString = String.valueOf(lat);
        String lonString = String.valueOf(lon);

        boolean sendFlag = false;

        Cursor query = sqLiteDatabase.rawQuery("SELECT send_flag FROM cafe_user_tbl WHERE lat = '" + latString + "' AND lon = '" + lonString + "'", null);
        boolean isEof = query.moveToFirst();
        // get data
        int flag = query.getInt(query.getColumnIndex("send_flag"));
        if (flag == 1) sendFlag = true;
        query.close();
        sqLiteDatabase.close();

        return sendFlag;
    }

    /**
     * Obtains all data from cafe_user_tbl
     *
     * @return
     */
    protected List<Map<String, Object>> executeSelect() {
        List<Map<String, Object>> result = new ArrayList<>();

        Cursor query = sqLiteDatabase.rawQuery("SELECT * FROM cafe_user_tbl", null);
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
            cafeImage = query.getBlob(query.getColumnIndex("image"));
            // Set Map<String,object> that has one record from makeOneRecordOfUserCafeData
            result.add(this.makeOneRecordOfUserCafeData(lat, lon, cafeName, cafeAddress, cafeTime, cafeTel, cafeWifi, cafeSocket, cafeImage));

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
     * @param cafeImage
     * @return Map<String, Object>
     */
    private Map<String, Object> makeOneRecordOfUserCafeData(double lat, double lon, String cafeName, String cafeAddress, String cafeTime,
                                                            String cafeTel, String cafeWifi, String cafeSocket, byte[] cafeImage) {
        Map<String, Object> result = new HashMap<>();

        result.put("lat", lat);
        result.put("lon", lon);
        result.put("cafeName", cafeName);
        result.put("cafeAddress", cafeAddress);
        result.put("cafeTime", cafeTime);
        result.put("cafeTel", cafeTel);
        result.put("cafeWifi", cafeWifi);
        result.put("cafeSocket", cafeSocket);
        result.put("cafeImage", cafeImage);

        return result;
    }

    /**
     * Executes to insert.
     *
     * @param lat
     * @param lon
     * @param name
     * @param address
     * @param time
     * @param tel
     * @param socket
     * @param wifi
     * @param image
     * @return
     */
    protected boolean executeInsert(double lat, double lon, String name, String address, String time, String tel, String socket, String wifi, byte[] image, int sendFlag) {
        boolean successFlag = true;

        sqLiteDatabase.beginTransaction();
        try {
            final SQLiteStatement statement = sqLiteDatabase.compileStatement("INSERT INTO cafe_user_tbl VALUES (?,?,?,?,?,?,?,?,?,?)");
            try {
                statement.bindString(1, String.valueOf(lat));
                statement.bindString(2, String.valueOf(lon));
                statement.bindString(3, name);
                statement.bindString(4, address);
                statement.bindString(5, time);
                statement.bindString(6, tel);
                statement.bindString(7, wifi);
                statement.bindString(8, socket);
                statement.bindBlob(9, image);
                statement.bindLong(10, sendFlag);
                statement.executeInsert();
            } catch (SQLiteConstraintException e) {
                Log.d(TAG, "SQLiteConstraintException@executeInsert, execute executeUpdate");
                successFlag = false;
            } finally {
                statement.close();
            }
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }

        return successFlag;
    }

    /**
     * Executes to delete 1 record.
     *
     * @param lat
     * @param lon
     * @return
     */
    protected boolean executeDelete(double lat, double lon) {
        boolean successFlag = true;

        sqLiteDatabase.beginTransaction();
        try {
            final SQLiteStatement statement = sqLiteDatabase.compileStatement("DELETE FROM cafe_user_tbl WHERE lat=? AND lon=?");
            try {
                statement.bindString(1, String.valueOf(lat));
                statement.bindString(2, String.valueOf(lon));
                statement.executeUpdateDelete();
            } finally {
                statement.close();
            }
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }

        return successFlag;
    }

    protected boolean executeUpdate(double lat, double lon, String name, String address, String time, String tel, String socket, String wifi, byte[] image, int sendFlag) {
        boolean successFlag = true;

        sqLiteDatabase.beginTransaction();
        try {
            final SQLiteStatement statement = sqLiteDatabase.compileStatement(
                    "UPDATE cafe_user_tbl SET name=?,address=?,time=?,tel=?,wifi=?,socket=?,image=?,send_flag=? WHERE lat=? AND lon=?");
            try {
                statement.bindString(1, name);
                statement.bindString(2, address);
                statement.bindString(3, time);
                statement.bindString(4, tel);
                statement.bindString(5, wifi);
                statement.bindString(6, socket);
                statement.bindBlob(7, image);
                statement.bindLong(8, sendFlag);
                statement.bindString(9, String.valueOf(lat));
                statement.bindString(10, String.valueOf(lon));
                statement.executeInsert();
            } finally {
                statement.close();
            }
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
            sqLiteDatabase.close();
        }

        return successFlag;
    }

    protected boolean executeUpdate(double lat, double lon, int sendFlag) {
        boolean successFlag = true;

        sqLiteDatabase.beginTransaction();
        try {
            final SQLiteStatement statement = sqLiteDatabase.compileStatement(
                    "UPDATE cafe_user_tbl SET send_flag=? WHERE lat=? AND lon=?");
            try {
                statement.bindLong(1, sendFlag);
                statement.bindString(2, String.valueOf(lat));
                statement.bindString(3, String.valueOf(lon));
                statement.executeInsert();
            } finally {
                statement.close();
            }
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }

        return successFlag;
    }

}
