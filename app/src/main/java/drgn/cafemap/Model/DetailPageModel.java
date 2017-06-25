package drgn.cafemap.Model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.JsonObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import drgn.cafemap.Controller.DetailPageActivity;
import drgn.cafemap.Controller.MapsActivity;
import drgn.cafemap.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Nobu on 2017/06/11.
 */

public class DetailPageModel {
    // Base param
    private double lat;
    private double lon;
    private Context context;
    private UserCafeMapModel ucm;
    // Json
    private JsonObject jsonObjectUserCafeMap;
    // Common View
    private View view;
    private TextView nameTextView;
    private TextView addressTextView;
    private TextView telTextView;
    private Bitmap uploadImageBmp;

    // Edit page view
    private Button saveButton;
    private Button uploadImageButton;
    private Button previewButton;
    private ImageView uploadImage;
    private Spinner startHourSpinner;
    private Spinner startMinuteSpinner;
    private Spinner startAmPmSpinner;
    private Spinner endHourSpinner;
    private Spinner endMinuteSpinner;
    private Spinner endAmPmSpinner;
    private Spinner socketSpinner;
    private Spinner wifiSpinner;
    private Button deleteButton;
    // Detail page view
    private ImageView cafeScreenshot;
    private TextView timeTextView;
    private TextView socketTextView;
    private TextView wifiTextView;
    private ImageButton editButton;


    public DetailPageModel(Context context, double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
        this.context = context;
        this.ucm = new UserCafeMapModel(context);
        this.jsonObjectUserCafeMap = this.ucm.getUserCafeMapJson();
    }

    private void prepareViewContents(View view, int viewMode) {
        this.view = view;
        this.nameTextView = (TextView) view.findViewById(R.id.cafeName);
        this.addressTextView = (TextView) view.findViewById(R.id.cafeAddress);
        this.telTextView = (TextView) view.findViewById(R.id.cafeTel);
        switch (viewMode) {
            case 3:
            case 0:
                this.uploadImage = (ImageView) view.findViewById(R.id.uploadImage);
                this.saveButton = (Button) view.findViewById(R.id.saveButton);
                this.uploadImageButton = (Button) view.findViewById(R.id.uploadImageButton);
                this.previewButton = (Button) view.findViewById(R.id.previewButton);
                this.startHourSpinner = (Spinner) view.findViewById(R.id.startHour);
                this.startMinuteSpinner = (Spinner) view.findViewById(R.id.startMinute);
                this.startAmPmSpinner = (Spinner) view.findViewById(R.id.startAmPm);
                this.endHourSpinner = (Spinner) view.findViewById(R.id.endHour);
                this.endMinuteSpinner = (Spinner) view.findViewById(R.id.endMinute);
                this.endAmPmSpinner = (Spinner) view.findViewById(R.id.endAmPm);
                this.socketSpinner = (Spinner) view.findViewById(R.id.cafeSocket);
                this.wifiSpinner = (Spinner) view.findViewById(R.id.cafeWifi);
                this.deleteButton = (Button) view.findViewById(R.id.deleteButton);

                break;
            case 1:
                this.cafeScreenshot = (ImageView) view.findViewById(R.id.badge);
                this.timeTextView = (TextView) view.findViewById(R.id.cafeTime);
                this.socketTextView = (TextView) view.findViewById(R.id.cafeSocket);
                this.wifiTextView = (TextView) view.findViewById(R.id.cafeWifi);
                this.editButton = (ImageButton) view.findViewById(R.id.editButton);
                break;
            case 2:
                this.saveButton = (Button) view.findViewById(R.id.saveButton);
                this.cafeScreenshot = (ImageView) view.findViewById(R.id.badge);
                this.timeTextView = (TextView) view.findViewById(R.id.cafeTime);
                this.socketTextView = (TextView) view.findViewById(R.id.cafeSocket);
                this.wifiTextView = (TextView) view.findViewById(R.id.cafeWifi);
            default:

        }

    }

    /**
     * Displays edit page
     * View mode 2
     *
     * @param fa
     * @param v
     * @param viewMode
     * @param cafeData
     * @param latitude
     * @param longitude
     */
    public void displayPreviewPage(FragmentActivity fa, View v, int viewMode, Map<String, String> cafeData, double latitude, double longitude) {
        final Map<String, String> temporaryCafeData = cafeData;
        final double lat = latitude;
        final double lon = longitude;
        final FragmentActivity activity = fa;
        prepareViewContents(v, viewMode);

        // read preview image
        try {
            InputStream in = context.openFileInput("preview.png");
            uploadImageBmp = BitmapFactory.decodeStream(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        cafeScreenshot.setImageBitmap(uploadImageBmp);

        if (temporaryCafeData.get("cafeName").equals("")) {
            temporaryCafeData.put("cafeName", "No name");
        }
        nameTextView.setText(temporaryCafeData.get("cafeName"));
        addressTextView.setText(temporaryCafeData.get("cafeAddress"));
        timeTextView.setText(temporaryCafeData.get("cafeTime"));
        telTextView.setText(temporaryCafeData.get("cafeTel"));
        socketTextView.setText(temporaryCafeData.get("cafeSocket"));
        wifiTextView.setText(temporaryCafeData.get("cafeWifi"));

        // Save
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UserCafeMapModel ucmm = new UserCafeMapModel(context);
//                Cafe cafe = new Cafe(lat, lon, temporaryCafeData.get("cafeName"), temporaryCafeData.get("cafeAddress"),
//                        temporaryCafeData.get("cafeTime"), temporaryCafeData.get("cafeTel"),
//                        temporaryCafeData.get("cafeSocket"), temporaryCafeData.get("cafeWifi"));
//
//                ucmm.saveUserCafeMap(key, cafe);
//                ucmm.saveUserCafeMapImage(uploadImageBmp, key);

                ucmm.saveUserCafeMap(lat, lon, temporaryCafeData.get("cafeName"), temporaryCafeData.get("cafeAddress"),
                        temporaryCafeData.get("cafeTime"), temporaryCafeData.get("cafeTel"),
                        temporaryCafeData.get("cafeSocket"), temporaryCafeData.get("cafeWifi"), uploadImageBmp);


                Intent intent = new Intent(context, MapsActivity.class);

                intent.putExtra("defaultPosLat", lat);
                intent.putExtra("defaultPosLon", lon);
                activity.startActivity(intent);
            }
        });
    }

    /**
     * Displays edit page
     * View mode 0 or 3
     * From making a new data
     *
     * @param fa
     * @param re
     * @param v
     * @param viewMode
     * @param latitude
     * @param longitude
     */
    public void displayEditPage(FragmentActivity fa, Resources re, View v, int viewMode, double latitude, double longitude) {
        prepareViewContents(v, viewMode);
        final FragmentActivity activity = fa;
        final Resources resources = re;
        final double lat = latitude;
        final double lon = longitude;

        // Invisible
        deleteButton.setVisibility(View.INVISIBLE);

        // preview
        previewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // cafe information that is set by users
                // name
                String cafeName = nameTextView.getText().toString();
                // cafe address
                String cafeAddress = addressTextView.getText().toString();
                // cafe tel
                String cafeTel = telTextView.getText().toString();
                // cafe time
                String startHour = startHourSpinner.getSelectedItem().toString();
                String startMinute = startMinuteSpinner.getSelectedItem().toString();
                String startAmPm = startAmPmSpinner.getSelectedItem().toString();
                String endHour = endHourSpinner.getSelectedItem().toString();
                String endMinute = endMinuteSpinner.getSelectedItem().toString();
                String endAmPm = endAmPmSpinner.getSelectedItem().toString();
                String cafeTime = startHour + ":" + startMinute + startAmPm + " - " + endHour + ":" + endMinute + endAmPm;
                // socket
                String cafeSocket = socketSpinner.getSelectedItem().toString();
                // wifi
                String cafeWifi = wifiSpinner.getSelectedItem().toString();

                Intent intent = new Intent(context, DetailPageActivity.class);

                intent.putExtra("lat", lat);
                intent.putExtra("lon", lon);
                intent.putExtra("viewMode", 2);
                intent.putExtra("cafeName", cafeName);
                intent.putExtra("cafeAddress", cafeAddress);
                intent.putExtra("cafeTel", cafeTel);
                intent.putExtra("cafeTime", cafeTime);
                intent.putExtra("cafeWifi", cafeWifi);
                intent.putExtra("cafeSocket", cafeSocket);

                // temp preview image
                try {
                    OutputStream out = context.openFileOutput("preview.png", MODE_PRIVATE);
                    if (uploadImageBmp == null) {
                        // Set no image
                        try {
                            InputStream istream = resources.getAssets().open("noImage.png");
                            uploadImageBmp = BitmapFactory.decodeStream(istream);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    uploadImageBmp.compress(Bitmap.CompressFormat.PNG, 100, out);
                    out.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                activity.startActivity(intent);

            }
        });

    }

    // From existing data
    public void displayEditPage(FragmentActivity fa, Resources re, View v, int viewMode, double latitude, double longitude, Map<String, String> cafeData) {
        prepareViewContents(v, viewMode);
        final FragmentActivity activity = fa;
        final Resources resources = re;
        final double lat = latitude;
        final double lon = longitude;
        final Map<String, String> previousCafeData = cafeData;

        nameTextView.setText(previousCafeData.get("cafeName"));
        addressTextView.setText(previousCafeData.get("cafeAddress"));
        telTextView.setText(previousCafeData.get("cafeTel"));

        // set time parts
        String[] timeArray = previousCafeData.get("cafeTime").split(" - ");
        String fromTime = timeArray[0].replaceAll("AM", "").replaceAll("PM", ""); // EX) 10:00
        String toTime = timeArray[1].replaceAll("AM", "").replaceAll("PM", ""); // EX) 19:00
        setSpinnerData(startHourSpinner, fromTime.split(":")[0]);
        setSpinnerData(startMinuteSpinner, fromTime.split(":")[1]);
        setSpinnerData(startAmPmSpinner, timeArray[0].substring(timeArray[0].length() - 2, timeArray[0].length()));
        setSpinnerData(endHourSpinner, toTime.split(":")[0]);
        setSpinnerData(endMinuteSpinner, toTime.split(":")[1]);
        setSpinnerData(endAmPmSpinner, timeArray[1].substring(timeArray[1].length() - 2, timeArray[1].length()));

        setSpinnerData(socketSpinner, previousCafeData.get("cafeSocket"));
        setSpinnerData(wifiSpinner, previousCafeData.get("cafeWifi"));

        // set previous image
//        try {
//            FileInputStream is = context.openFileInput(key + ".png");
//            setUploadImageBmp(BitmapFactory.decodeStream(is));
//            is.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        // Delete only existing cafe info
        if (checkCafeDetailExist() == true) {
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new AlertDialog.Builder(activity)
                            .setTitle("Delete cafe information")
                            .setMessage("Are you sure you want to permanently delete this cafe info ?")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // OK button pressed
                                    //ucm.deleteUserCafeMap(jsonObjectUserCafeMap, key);

                                    Intent intent = new Intent(context, MapsActivity.class);

                                    intent.putExtra("defaultPosLat", lat);
                                    intent.putExtra("defaultPosLon", lon);
                                    activity.startActivity(intent);
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                }
            });
        } else {
            //deleteButton.setVisibility(View.INVISIBLE);
        }

        // preview
        previewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // cafe information that is set by users
                // name
                String cafeName = nameTextView.getText().toString();
                // cafe address
                String cafeAddress = addressTextView.getText().toString();
                // cafe tel
                String cafeTel = telTextView.getText().toString();
                // cafe time
                String startHour = startHourSpinner.getSelectedItem().toString();
                String startMinute = startMinuteSpinner.getSelectedItem().toString();
                String startAmPm = startAmPmSpinner.getSelectedItem().toString();
                String endHour = endHourSpinner.getSelectedItem().toString();
                String endMinute = endMinuteSpinner.getSelectedItem().toString();
                String endAmPm = endAmPmSpinner.getSelectedItem().toString();
                String cafeTime = startHour + ":" + startMinute + startAmPm + " - " + endHour + ":" + endMinute + endAmPm;
                // socket
                String cafeSocket = socketSpinner.getSelectedItem().toString();
                // wifi
                String cafeWifi = wifiSpinner.getSelectedItem().toString();

                Intent intent = new Intent(context, DetailPageActivity.class);

                intent.putExtra("lat", lat);
                intent.putExtra("lon", lon);
                intent.putExtra("viewMode", 2);
                intent.putExtra("cafeName", cafeName);
                intent.putExtra("cafeAddress", cafeAddress);
                intent.putExtra("cafeTel", cafeTel);
                intent.putExtra("cafeTime", cafeTime);
                intent.putExtra("cafeWifi", cafeWifi);
                intent.putExtra("cafeSocket", cafeSocket);

                // temp preview image
                try {
                    OutputStream out = context.openFileOutput("preview.png", MODE_PRIVATE);
                    if (uploadImageBmp == null) {
                        // Set no image
                        try {
                            InputStream istream = resources.getAssets().open("noImage.png");
                            uploadImageBmp = BitmapFactory.decodeStream(istream);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    uploadImageBmp.compress(Bitmap.CompressFormat.PNG, 100, out);
                    out.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                activity.startActivity(intent);

            }
        });
    }

    /**
     * Displays detail page
     * View mode 1
     *
     * @param v
     * @param viewMode
     */
    public void displayDetailPage(FragmentActivity fa, View v, int viewMode, double latitude, double longitude) {
        prepareViewContents(v, viewMode);
        final FragmentActivity activity = fa;
        final double lat = latitude;
        final double lon = longitude;

        // image in cafe_user_tbl exits or not
        byte[] result = new CafeUserTblHelper(context).executeSelect(lat, lon, "image");

        String type;
        if (result != null) type = "user"; // get data from user data
        else type = "master";// det data from master data

        // Set image

        final Map<String, Object> cafeDetail;

        if (type.equals("master")) cafeDetail = getUserCafeMapDetail(lat, lon);
        else cafeDetail = getUserCafeMapDetail(lat, lon);

        nameTextView.setText(cafeDetail.get("cafeName").toString());
        addressTextView.setText(cafeDetail.get("cafeAddress").toString());
        telTextView.setText(cafeDetail.get("cafeTel").toString());
        timeTextView.setText(cafeDetail.get("cafeTime").toString());
        socketTextView.setText(cafeDetail.get("cafeSocket").toString());
        wifiTextView.setText(cafeDetail.get("cafeWifi").toString());
        byte[] imageByte = (byte[]) cafeDetail.get("cafeImage");
        cafeScreenshot.setImageBitmap(ucm.convertByteToBitmap(imageByte));

        if (type.equals("master")) editButton.setVisibility(View.INVISIBLE);

        // go to edit page
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailPageActivity.class);
                intent.putExtra("lat", lat);
                intent.putExtra("lon", lon);
                intent.putExtra("viewMode", 3);
                intent.putExtra("cafeName", cafeDetail.get("cafeName").toString());
                intent.putExtra("cafeAddress", cafeDetail.get("cafeAddress").toString());
                intent.putExtra("cafeTel", cafeDetail.get("cafeTel").toString());
                intent.putExtra("cafeTime", cafeDetail.get("cafeTime").toString());
                intent.putExtra("cafeSocket", cafeDetail.get("cafeSocket").toString());
                intent.putExtra("cafeWifi", cafeDetail.get("cafeWifi").toString());

                activity.startActivity(intent);

            }
        });

    }

    public Map<String, Object> getUserCafeMapDetail(double lat, double lon) {
        Map<String, Object> cafeDetail = new HashMap<>();

        CafeUserTblHelper cafeUserTblHelper = new CafeUserTblHelper(context);
        cafeDetail = cafeUserTblHelper.executeSelect(lat, lon);

        return cafeDetail;
    }
    
    private void setSpinnerData(Spinner timeSpinner, String time) {
        Adapter adp = (ArrayAdapter<String>) timeSpinner.getAdapter();
        int index = 0;
        for (int i = 0; i < adp.getCount(); i++) {
            if (adp.getItem(i).equals(time)) {
                index = i;
                break;
            }
        }
        timeSpinner.setSelection(index);
    }

    /**
     * Checks cafe data that exists in json file.
     *
     * @return boolean
     */
    public boolean checkCafeDetailExist() {
        boolean fileIsFound = true;

        try {
            //this.jsonObjectUserCafeMap.get(key).getAsJsonObject();
        } catch (NullPointerException e) {
            // File not found
            fileIsFound = false;
        }

        return fileIsFound;
    }

    public void setUploadImageBmp(Bitmap uploadImageBmp) {
        this.uploadImage.setImageBitmap(uploadImageBmp);
        this.uploadImageBmp = uploadImageBmp;
    }
}
