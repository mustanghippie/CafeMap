package drgn.cafemap.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.gson.JsonObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

import static drgn.cafemap.Controller.MapsActivity.atms;

/**
 * Created by Nobu on 2017/06/11.
 */

public class DetailPageModel {
    private String key;
    private Context context;

    private JsonObject jsonObjectUserCafeMap;

    public DetailPageModel(Context context, String key) {
        this.key = key;
        this.context = context;
        this.jsonObjectUserCafeMap = new UserCafeMapModel(context).getUserCafeMapJson();
    }

    public Bitmap getImage(String type) {
        Bitmap image;
        if (type.equals("master")) {
            String imageName = atms.getCafeMap().get(key).get("name").replaceAll(" ", "_").toLowerCase();
            image = atms.getCafeBitmapMap().get(imageName);
            return image;
        } else {
            try {
                InputStream is = context.openFileInput(key+".png");
                image = BitmapFactory.decodeStream(is);
                return image;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    public HashMap<String,String> getUserCafeMapDetail(){
        HashMap<String,String> cafeDetail = new HashMap<>();

        cafeDetail.put("name",jsonObjectUserCafeMap.get(key).getAsJsonObject().get("cafeName").getAsString());
        cafeDetail.put("address",jsonObjectUserCafeMap.get(key).getAsJsonObject().get("cafeAddress").getAsString());
        cafeDetail.put("tel",jsonObjectUserCafeMap.get(key).getAsJsonObject().get("cafeTel").getAsString());
        cafeDetail.put("time",jsonObjectUserCafeMap.get(key).getAsJsonObject().get("cafeTime").getAsString());
        cafeDetail.put("socket",jsonObjectUserCafeMap.get(key).getAsJsonObject().get("cafeSocket").getAsString());
        cafeDetail.put("wifi",jsonObjectUserCafeMap.get(key).getAsJsonObject().get("cafeWifi").getAsString());

        return cafeDetail;
    }

    public boolean checkCafeDetailExist(){
        boolean fileIsFound = true;

        try {
            jsonObjectUserCafeMap.get(key).getAsJsonObject();
        } catch (NullPointerException e) {
            // File not found
            fileIsFound = false;
        }

        return fileIsFound;

    }
}
