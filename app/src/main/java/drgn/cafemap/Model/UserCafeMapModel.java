package drgn.cafemap.Model;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import drgn.cafemap.Object.Cafe;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by musta on 2017/06/10.
 */

public class UserCafeMapModel {

    private Context context;

    public UserCafeMapModel(Context context) {
        this.context = context;
    }

    public void saveUserCafeMap(String key, Cafe cafe) {

        Toast.makeText(context, "Now saving", Toast.LENGTH_LONG).show();

        // prepare data
        Gson gson = new Gson();
        String cafeJson = new Gson().toJson(cafe);

        // @todo delete for debug
//        context.deleteFile("UserCafeMap.json");
//        context.deleteFile("UserCafeMapKey.json");

        JsonObject jsonUserCafeMap = getUserCafeMapJson();
        JsonObject jsonUserCafeMapKey = getUserCafeMapKeyJson();

        // If UserCafeMap.json doesn't exist, make empty UserCafeMap.json and reload
        if (jsonUserCafeMap == null) {
            makeEmptyUserCafeMapJson();
            jsonUserCafeMap = getUserCafeMapJson();
        }

        if (jsonUserCafeMapKey == null) {
            makeEmptyUserCafeMapJsonKey();
            jsonUserCafeMapKey = getUserCafeMapKeyJson();
        }

        // Add user cafe info to UserCafeMap
        JsonObject jsonAddData = gson.fromJson(cafeJson, JsonObject.class);
        jsonUserCafeMap.add(key, jsonAddData);

        // Add key
        int dataCount = jsonUserCafeMapKey.get("data_count").getAsInt();
        jsonUserCafeMapKey.addProperty("data_count", dataCount + 1); // data count up
        jsonUserCafeMapKey.addProperty("data_" + String.valueOf(dataCount), key);

        //save a json file
        saveUserCafeMapJson(jsonUserCafeMap);
        saveUserCafeMapKeyJson(jsonUserCafeMapKey);

//        System.out.println("Json master data = " + updateJsonUserCafeMap);
//        System.out.println("Json key data = " + updateJsonUserCafeMapKey);

    }

    public void saveUserCafeMapImage(Bitmap uploadImageBmp, String imageName) {
        if (uploadImageBmp != null) {
            // save image in local
            try {
                OutputStream out = context.openFileOutput(imageName + ".png", MODE_PRIVATE);
                uploadImageBmp.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //Debug
        //context.deleteFile(imageName + ".png");
    }

    public void setUserCafeMapMarkers(GoogleMap mMap) {
        // Read json
        JsonObject jsonUserCafeMap = getUserCafeMapJson();
        JsonObject jsonUserCafeMapKey = getUserCafeMapKeyJson();

        if (jsonUserCafeMap == null || jsonUserCafeMapKey == null) return;

        // get data count
        int dataCount = jsonUserCafeMapKey.get("data_count").getAsInt();
        List<String> keyList = new ArrayList<>();
        // set keys
        for (int i = 0; i < dataCount; i++) {
            keyList.add(jsonUserCafeMapKey.get("data_" + String.valueOf(i)).getAsString());
        }

        String title, wifi, socket, time;
        double lat, lon;
        MapsActivityModel mam = new MapsActivityModel();

        // make markers til finished key data
        for (String key : keyList) {
            if (key.equals("")) continue;
            title = jsonUserCafeMap.get(key).getAsJsonObject().get("cafeName").getAsString();
            lat = jsonUserCafeMap.get(key).getAsJsonObject().get("lat").getAsDouble();
            lon = jsonUserCafeMap.get(key).getAsJsonObject().get("lon").getAsDouble();
            time = jsonUserCafeMap.get(key).getAsJsonObject().get("cafeTime").getAsString();
            socket = jsonUserCafeMap.get(key).getAsJsonObject().get("cafeSocket").getAsString();
            wifi = jsonUserCafeMap.get(key).getAsJsonObject().get("cafeWifi").getAsString();
            // set
            mam.setUpMarkers(mMap, title, lat, lon, time, socket, wifi, "user");
        }
    }

    private void saveUserCafeMapJson(JsonObject userCafeMapJson) {
        String updateJsonUserCafeMap = new Gson().toJson(userCafeMapJson);
        try {
            OutputStream out = context.openFileOutput("UserCafeMap.json", MODE_PRIVATE);
            PrintWriter writer =
                    new PrintWriter(new OutputStreamWriter(out, "UTF-8"));

            writer.write(updateJsonUserCafeMap);
            writer.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void saveUserCafeMapKeyJson(JsonObject userCafeMapKeyJson) {
        String updateJsonUserCafeMapKey = new Gson().toJson(userCafeMapKeyJson);
        try {
            OutputStream out = context.openFileOutput("UserCafeMapKey.json", MODE_PRIVATE);
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, "UTF-8"));

            writer.write(updateJsonUserCafeMapKey);
            writer.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void makeEmptyUserCafeMapJson() {
        try {
            OutputStream out = context.openFileOutput("UserCafeMap.json", MODE_PRIVATE);
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, "UTF-8"));

            writer.append("{}");
            writer.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void makeEmptyUserCafeMapJsonKey() {
        try {
            OutputStream out = context.openFileOutput("UserCafeMapKey.json", MODE_PRIVATE);
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, "UTF-8"));

            writer.append("{\"data_count\":\"0\"}");
            writer.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
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

    private JsonObject getUserCafeMapKeyJson() {
        String jsonObjStringUserCafeMapKey = "";
        try {
            InputStream in = context.openFileInput("UserCafeMapKey.json");
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String s;
            while ((s = reader.readLine()) != null) {
                jsonObjStringUserCafeMapKey += s;
            }
            reader.close();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            System.out.println("[Debug] UserCafeMapKey.json Not Found");
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        JsonObject jsonUserCafeMap = new Gson().fromJson(jsonObjStringUserCafeMapKey, JsonObject.class);
        return jsonUserCafeMap;
    }

    public void deleteUserCafeMap(JsonObject jsonObjectUserCafeMap, String key) {
        System.out.println("key = " + key);
        String jsonString = new Gson().toJson(jsonObjectUserCafeMap);
        //System.out.println(jsonString);
        jsonObjectUserCafeMap.remove(key);
        saveUserCafeMapJson(jsonObjectUserCafeMap);
        String jsonString2 = new Gson().toJson(jsonObjectUserCafeMap);
        //System.out.println(jsonString2);
        String keyMap = new Gson().toJson(getUserCafeMapKeyJson());
        System.out.println(keyMap);
        deleteUSerCafeMapKey(key);
    }

    private void deleteUSerCafeMapKey(String key) {
        JsonObject userCafeMapKeyJson = getUserCafeMapKeyJson();
        int count = userCafeMapKeyJson.get("data_count").getAsInt();
        for (int i = 0; i < count; i++) {
            if (key.equals(userCafeMapKeyJson.get("data_" + String.valueOf(i)).getAsString())) {
                //userCafeMapKeyJson.remove("data_"+String.valueOf(i));
                userCafeMapKeyJson.addProperty("data_" + String.valueOf(i), "");
                saveUserCafeMapKeyJson(userCafeMapKeyJson);
            }
        }

    }

}
