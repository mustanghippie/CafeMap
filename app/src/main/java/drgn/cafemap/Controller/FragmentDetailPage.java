package drgn.cafemap.Controller;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import drgn.cafemap.Object.Cafe;
import drgn.cafemap.Model.GetAddressGeocoder;
import drgn.cafemap.Model.AsyncTaskMarkerSet;
import drgn.cafemap.Model.DetailPageModel;
import drgn.cafemap.Model.UserCafeMapModel;
import drgn.cafemap.R;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class FragmentDetailPage extends Fragment {

    private String key;
    private int viewMode; // viewMode: 0 => make a new data, 1 => display cafe info 2 => preview
    private AsyncTaskMarkerSet atms;
    private ImageView uploadImage;
    private Bitmap uploadImageBmp;
    private double lat, lon;

    // For preview page
    private String cafeNamePreview;
    private String cafeAddressPreview;
    private String cafeTelPreview;
    private String cafeTimePreview;
    private String cafeWifiPreview;
    private String cafeSocketPreview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // must get params of bundle in onCreate method
        Bundle args = getArguments();
        // viewMode: 0 => make a new data, 1 => display cafe info 2 => preview
        viewMode = args.getInt("viewMode");
        lat = args.getDouble("lat");
        lon = args.getDouble("lon");
        key = String.valueOf(args.getDouble("lat")) + String.valueOf(args.getDouble("lon"));

        int layoutId = 0;

        switch (viewMode) {
            case 0:
                layoutId = R.layout.activity_fragment_detail_edit_page;
                break;
            case 1:
                layoutId = R.layout.activity_fragment_detail_page;
                break;
            case 2:
                this.cafeNamePreview = args.getString("cafeName");
                this.cafeAddressPreview = args.getString("cafeAddress");
                this.cafeTelPreview = args.getString("cafeTel");
                this.cafeTimePreview = args.getString("cafeTime");
                this.cafeSocketPreview = args.getString("cafeWifi");
                this.cafeWifiPreview = args.getString("cafeWifi");
                layoutId = R.layout.activity_fragment_detail_preview_page;

                break;
            default:
                Log.d("[Error] viewMode ", "Please check viewMode");
        }

        return inflater.inflate(layoutId, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setParamsInView(view);
    }

    /**
     * Makes detail page layout or edit page layout by newMarkerFlag
     * if newMarkerFlag is true, user make cafe information.
     *
     * @param view
     * @return int R.layout ID
     */
    private void setParamsInView(final View view) {

        final TextView nameTextView;
        final TextView addressTextView;
        TextView timeTextView;
        final TextView telTextView;
        TextView socketTextView;
        TextView wifiTextView;
        Button saveButton;
        Button uploadImageButton;
        Button previewButton;
        ImageView img;

        // cafe name
        nameTextView = (TextView) view.findViewById(R.id.cafeName);
        // cafe address
        addressTextView = (TextView) view.findViewById(R.id.cafeAddress);
        // cafe tel
        telTextView = (TextView) view.findViewById(R.id.cafeTel);

        switch (viewMode) {
            case 0:
                // set address by GetAddressGeocoder
                GetAddressGeocoder coder = new GetAddressGeocoder(getContext(), addressTextView, lat, lon);
                coder.execute();

                // Initialize view component
                uploadImage = (ImageView) view.findViewById(R.id.uploadImage);
                uploadImageButton = (Button) view.findViewById(R.id.uploadImageButton);
                previewButton = (Button) view.findViewById(R.id.previewButton);
                //
                final Spinner startHourSpinner = (Spinner) view.findViewById(R.id.startHour);
                final Spinner startMinuteSpinner = (Spinner) view.findViewById(R.id.startMinute);
                final Spinner startAmPmSpinner = (Spinner) view.findViewById(R.id.startAmPm);
                final Spinner endHourSpinner = (Spinner) view.findViewById(R.id.endHour);
                final Spinner endMinuteSpinner = (Spinner) view.findViewById(R.id.endMinute);
                final Spinner endAmPmSpinner = (Spinner) view.findViewById(R.id.endAmPm);
                // cafe socket
                final Spinner socketSpinner = (Spinner) view.findViewById(R.id.cafeSocket);
                // cafe wifi
                final Spinner wifiSpinner = (Spinner) view.findViewById(R.id.cafeWifi);

                // upload image
                uploadImageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("image/*");
                        startActivityForResult(intent, 1000);
                    }
                });



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

                        Intent intent = new Intent(getContext(), DetailPageActivity.class);

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
                            OutputStream out = getContext().openFileOutput("preview.png", MODE_PRIVATE);
                            if (uploadImageBmp == null) {
                                // Set no image
                                try {
                                    InputStream istream = getResources().getAssets().open("noImage.png");
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

                        startActivity(intent);

                    }
                });
                break;
            case 1: // Display cafe detail page
                // check info is made by owner or user

                DetailPageModel dpm = new DetailPageModel(getContext(), key);

                String type;
                if (dpm.checkCafeDetailExist()) {
                    //from user
                    type = "user";
                } else {
                    // from master
                    type = "master";
                }

                // Set image
                img = (ImageView) view.findViewById(R.id.badge);
                img.setImageBitmap(dpm.getImage(type));

                // prepare view
                // cafe time
                timeTextView = (TextView) view.findViewById(R.id.cafeTime);
                // cafe socket
                socketTextView = (TextView) view.findViewById(R.id.cafeSocket);
                // cafe wifi
                wifiTextView = (TextView) view.findViewById(R.id.cafeWifi);

                HashMap<String, String> cafeDetail = null;
                if (type.equals("master")) cafeDetail = MapsActivity.atms.getCafeMap().get(key);
                else cafeDetail = dpm.getUserCafeMapDetail();


                nameTextView.setText(cafeDetail.get("name"));
                addressTextView.setText(cafeDetail.get("address"));
                telTextView.setText(cafeDetail.get("tel"));
                timeTextView.setText(cafeDetail.get("time"));
                socketTextView.setText(cafeDetail.get("socket"));
                wifiTextView.setText(cafeDetail.get("wifi"));
                break;
            case 2: // preview page

                saveButton = (Button) view.findViewById(R.id.saveButton);

                // Set image
                img = (ImageView) view.findViewById(R.id.badge);
                // read preview image
                try {
                    InputStream in = getContext().openFileInput("preview.png");
                    uploadImageBmp = BitmapFactory.decodeStream(in);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                img.setImageBitmap(uploadImageBmp);

                // cafe time
                timeTextView = (TextView) view.findViewById(R.id.cafeTime);
                // cafe socket
                socketTextView = (TextView) view.findViewById(R.id.cafeSocket);
                // cafe wifi
                wifiTextView = (TextView) view.findViewById(R.id.cafeWifi);

                if (cafeNamePreview.equals("")) {
                    cafeNamePreview = "No name";
                }
                nameTextView.setText(cafeNamePreview);
                addressTextView.setText(cafeAddressPreview);
                timeTextView.setText(cafeTimePreview);
                telTextView.setText(cafeTelPreview);
                socketTextView.setText(cafeWifiPreview);
                wifiTextView.setText(cafeSocketPreview);

                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // Delete preview image
                        //getContext().deleteFile("preview.png");

                        UserCafeMapModel ucmm = new UserCafeMapModel(getContext());
                        Cafe cafe = new Cafe(lat, lon, cafeNamePreview, cafeAddressPreview, cafeTimePreview, cafeTelPreview, cafeSocketPreview, cafeWifiPreview);
                        ucmm.saveUserCafeMap(key, cafe);
                        ucmm.saveUserCafeMapImage(uploadImageBmp, key);

                        Intent intent = new Intent(getContext(), MapsActivity.class);

                        intent.putExtra("defaultPosLat", lat);
                        intent.putExtra("defaultPosLon", lon);
                        startActivity(intent);
                    }
                });

                break;
            default:
                Log.d("[Error] viewMode ", "Please check viewMode");
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == 1000 && resultCode == RESULT_OK) {
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();

                try {
                    uploadImageBmp = getBitmapFromUri(uri);
                    //System.out.println("Bitmap image = " + uploadImageBmp);
                    uploadImage.setImageBitmap(uploadImageBmp);
//                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                    bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
//                    imageBytes = baos.toByteArray();

                    // decode
//                    bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getActivity().getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }
}
