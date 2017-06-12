package drgn.cafemap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

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

        // delete for debug
//        context.deleteFile("UserCafeMap.json");
//        context.deleteFile("UserCafeMapKey.json");

        // json file exists or not
        File file = context.getFileStreamPath("UserCafeMap.json");
        boolean isExists = file.exists();
        if (!isExists) {
            // make an empty json file fot the first time
            try {
                OutputStream out = context.openFileOutput("UserCafeMap.json", MODE_PRIVATE);
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, "UTF-8"));

                writer.append("{}");
                writer.close();

                out = context.openFileOutput("UserCafeMapKey.json", MODE_PRIVATE);
                writer = new PrintWriter(new OutputStreamWriter(out, "UTF-8"));

                writer.append("{\"data_count\":\"0\"}");
                writer.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        // read json file
        String jsonObjStringUserCafeMap = "";
        String jsonObjStringUserCafeMapKey = "";
        try {
            InputStream in = context.openFileInput("UserCafeMap.json");
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String s;
            while ((s = reader.readLine()) != null) {
                jsonObjStringUserCafeMap += s;
            }
            reader.close();

            in = context.openFileInput("UserCafeMapKey.json");
            reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            s = "";
            while ((s = reader.readLine()) != null) {
                jsonObjStringUserCafeMapKey += s;
            }
            reader.close();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Add user cafe info to UserCafeMap
        JsonObject jsonUserCafeMap = gson.fromJson(jsonObjStringUserCafeMap, JsonObject.class); // master data
        JsonObject jsonAddData = gson.fromJson(cafeJson, JsonObject.class);
        jsonUserCafeMap.add(key, jsonAddData);

        // Add key
        JsonObject jsonUserCafeMapKey = gson.fromJson(jsonObjStringUserCafeMapKey, JsonObject.class); // key data
        int dataCount = jsonUserCafeMapKey.get("data_count").getAsInt();
        jsonUserCafeMapKey.addProperty("data_count", dataCount + 1); // data count up
        jsonUserCafeMapKey.addProperty("data_" + String.valueOf(dataCount), key);

        //save a json file
        String updateJsonUserCafeMap = gson.toJson(jsonUserCafeMap);
        String updateJsonUserCafeMapKey = gson.toJson(jsonUserCafeMapKey);
        try {
            OutputStream out = context.openFileOutput("UserCafeMap.json", MODE_PRIVATE);
            PrintWriter writer =
                    new PrintWriter(new OutputStreamWriter(out, "UTF-8"));

            writer.write(updateJsonUserCafeMap);
            writer.close();

            out = context.openFileOutput("UserCafeMapKey.json", MODE_PRIVATE);
            writer = new PrintWriter(new OutputStreamWriter(out, "UTF-8"));

            writer.write(updateJsonUserCafeMapKey);
            writer.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

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
        // Read UserCafeMap.json
        File file = context.getFileStreamPath("UserCafeMap.json");
        boolean isExists = file.exists();
        if (!isExists) return;

        // read json file
        String jsonObjStringUserCafeMap = "";
        String jsonObjStringUserCafeMapKey = "";
        try {
            InputStream in = context.openFileInput("UserCafeMap.json");
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String s;
            while ((s = reader.readLine()) != null) {
                jsonObjStringUserCafeMap += s;
            }
            reader.close();

            in = context.openFileInput("UserCafeMapKey.json");
            reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            s = "";
            while ((s = reader.readLine()) != null) {
                jsonObjStringUserCafeMapKey += s;
            }
            reader.close();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //System.out.println("setUserCafeMapMarkers " + jsonObjString);
        // Add user cafe info to the json file
        Gson gson = new Gson();
        JsonObject jsonUserCafeMap = gson.fromJson(jsonObjStringUserCafeMap, JsonObject.class); // master data
        JsonObject jsonUserCafeMapKey = gson.fromJson(jsonObjStringUserCafeMapKey, JsonObject.class); // key data
        // get data count
        int dataCount = jsonUserCafeMapKey.get("data_count").getAsInt();
        List<String> keyList = new ArrayList<>();
        // set keys
        for (int i = 0; i < dataCount; i++) {
            keyList.add(jsonUserCafeMapKey.get("data_" + String.valueOf(i)).getAsString());
        }

        MarkerOptions options;
        double lat, lon;
        String wifi,socket,time;
        LatLng location;
        // make markers til finished key data
        for (String key : keyList) {
            options = new MarkerOptions();
            options.title(jsonUserCafeMap.get(key).getAsJsonObject().get("cafeName").getAsString());

            lat = jsonUserCafeMap.get(key).getAsJsonObject().get("lat").getAsDouble();
            lon = jsonUserCafeMap.get(key).getAsJsonObject().get("lon").getAsDouble();
            location = new LatLng(lat, lon);
            options.position(location);
            time = jsonUserCafeMap.get(key).getAsJsonObject().get("cafeTime").getAsString();
            socket = jsonUserCafeMap.get(key).getAsJsonObject().get("cafeSocket").getAsString();
            wifi = jsonUserCafeMap.get(key).getAsJsonObject().get("cafeWifi").getAsString();

            options.snippet("Wi-fi: " + wifi + "\n" + "Socket: " + socket + " " + time);

            // Add marker on google map
            mMap.addMarker(options);

        }
    }

    public JsonObject getUserCafeMapJson(){
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        JsonObject jsonUserCafeMap = new Gson().fromJson(jsonObjStringUserCafeMap, JsonObject.class);
        return jsonUserCafeMap;
    }
}
