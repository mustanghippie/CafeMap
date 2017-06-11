package drgn.cafemap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

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

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by musta on 2017/06/10.
 */

public class UserCafeMapModel {

    private Context context;
    private View view;

    public UserCafeMapModel(View view, Context context) {
        this.view = view;
        this.context = context;
    }

    public void saveUserCafeMap(String key, String cafeName, String cafeAddress) {

        Toast.makeText(context, "Now saving", Toast.LENGTH_LONG).show();

        // prepare data
        Gson gson = new Gson();
        Cafe cafe = new Cafe(cafeName, cafeAddress, "available", "available", "778-XXXX-8493");
        String cafeJson = new Gson().toJson(cafe);

        // delete for debug
        context.deleteFile("UserCafeMap.json");

        // json file exists or not
        File file = context.getFileStreamPath("UserCafeMap.json");
        boolean isExists = file.exists();
        if (!isExists) {
            // make an empty json file fot the first time
            try {
                OutputStream out = context.openFileOutput("UserCafeMap.json", MODE_PRIVATE);
                PrintWriter writer =
                        new PrintWriter(new OutputStreamWriter(out, "UTF-8"));

                writer.append("{}");
                writer.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }


        // read json file
        String jsonObjString = "";
        try {
            InputStream in = context.openFileInput("UserCafeMap.json");
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String s;
            while ((s = reader.readLine()) != null) {
                jsonObjString += s;
            }
            reader.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Add user cafe info to the json file
        JsonObject jsonUserCafeMap = gson.fromJson(jsonObjString, JsonObject.class); // master data
        JsonObject jsonAddData = gson.fromJson(cafeJson, JsonObject.class);
        jsonUserCafeMap.add(key, jsonAddData);

        //save a json file
        String updateJsonUserCafeMap = gson.toJson(jsonUserCafeMap);
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

        System.out.println("Json master data = " + updateJsonUserCafeMap);

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
    }


}
