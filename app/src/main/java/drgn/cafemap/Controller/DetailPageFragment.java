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
import android.widget.TextView;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import drgn.cafemap.Model.GetAddressGeocoder;
import drgn.cafemap.Model.AsyncTaskMarkerSet;
import drgn.cafemap.Model.DetailPageModel;
import drgn.cafemap.R;

import static android.app.Activity.RESULT_OK;

public class DetailPageFragment extends Fragment {

    private String key;
    private int viewMode; // viewMode: 0 => make a new data, 1 => display cafe info 2 => preview
    private AsyncTaskMarkerSet atms;
    private ImageView uploadImage;
    private Bitmap uploadImageBmp;
    private double lat, lon;
    private DetailPageModel dpm;

    // For preview page
    private Map<String, String> temporaryCafeData = new HashMap<>();

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
                temporaryCafeData.put("cafeName", args.getString("cafeName"));
                temporaryCafeData.put("cafeAddress", args.getString("cafeAddress"));
                temporaryCafeData.put("cafeTel", args.getString("cafeTel"));
                temporaryCafeData.put("cafeTime", args.getString("cafeTime"));
                temporaryCafeData.put("cafeSocket", args.getString("cafeSocket"));
                temporaryCafeData.put("cafeWifi", args.getString("cafeWifi"));
                layoutId = R.layout.activity_fragment_detail_preview_page;

                break;
            case 3:
                temporaryCafeData.put("cafeName", args.getString("cafeName"));
                temporaryCafeData.put("cafeAddress", args.getString("cafeAddress"));
                temporaryCafeData.put("cafeTel", args.getString("cafeTel"));
                temporaryCafeData.put("cafeTime", args.getString("cafeTime"));
                temporaryCafeData.put("cafeSocket", args.getString("cafeSocket"));
                temporaryCafeData.put("cafeWifi", args.getString("cafeWifi"));

                layoutId = R.layout.activity_fragment_detail_edit_page;
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

        dpm = new DetailPageModel(getContext(), key);

        switch (viewMode) {
            case 0: // Display edit page from "make a new"
                // cafe address
                final TextView addressTextView;
                addressTextView = (TextView) view.findViewById(R.id.cafeAddress);
                // set address by GetAddressGeocoder
                GetAddressGeocoder coder = new GetAddressGeocoder(getContext(), addressTextView, lat, lon);
                coder.execute();

                dpm.displayEditPage(getActivity(), getResources(), view, 0, lat, lon);

                // Upload image
                Button uploadImageButton = (Button) view.findViewById(R.id.uploadImageButton);
                uploadImageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("image/*");

                        startActivityForResult(intent, 1000);
                    }
                });

                break;
            case 1: // Display cafe detail page
                dpm.displayDetailPage(getActivity(), view, viewMode, lat, lon);

                break;
            case 2: // preview page
                dpm.displayPreviewPage(getActivity(), view, viewMode, temporaryCafeData, lat, lon);

                break;
            case 3: // Display edit page from "existing cafe info"
                dpm.displayEditPage(getActivity(), getResources(), view, viewMode, lat, lon, temporaryCafeData);

                // Upload image
                uploadImageButton = (Button) view.findViewById(R.id.uploadImageButton);
                uploadImageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("image/*");

                        startActivityForResult(intent, 1000);
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
                    dpm.setUploadImageBmp(uploadImageBmp);

                    //uploadImage.setImageBitmap(uploadImageBmp);

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
