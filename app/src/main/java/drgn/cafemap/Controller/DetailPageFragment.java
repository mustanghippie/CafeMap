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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import drgn.cafemap.Model.AsyncTaskAddressGeocoder;
import drgn.cafemap.Model.DetailPageModel;
import drgn.cafemap.R;

import static android.app.Activity.RESULT_OK;

public class DetailPageFragment extends Fragment {

    private int viewMode; // viewMode: 0 => make a new data, 1 => display cafe info 2 => preview
    private ImageView uploadImage;
    private Bitmap uploadImageBmp;
    private double lat, lon;
    private boolean ownerFlag;
    private DetailPageModel dpm;

    // For preview page
    private Map<String, String> temporaryCafeData = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // must get params of bundle in onCreate method
        Bundle args = getArguments();
        // viewMode: 0 => make a new data, 1 => display cafe info 2 => preview
        this.viewMode = args.getInt("viewMode");
        this.lat = args.getDouble("lat");
        this.lon = args.getDouble("lon");
        this.ownerFlag = args.getBoolean("ownerFlag");

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
     * @param view
     * @return int R.layout ID
     */
    private void setParamsInView(final View view) {

        dpm = new DetailPageModel(getContext(), view, getActivity(), getResources(), lat, lon);

        switch (viewMode) {
            case 0: // Display edit page from "make a new"
                // cafe address
                final TextView addressTextView;
                addressTextView = (TextView) view.findViewById(R.id.cafeAddress);
                // set address by AsyncTaskAddressGeocoder
                AsyncTaskAddressGeocoder coder = new AsyncTaskAddressGeocoder(getContext(), addressTextView, lat, lon);
                coder.execute();

                // make edit page
                dpm.displayEditPage(0);

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
                dpm.displayDetailPage(viewMode, ownerFlag);

                break;
            case 2: // preview page
                dpm.displayPreviewPage(viewMode, temporaryCafeData);

                break;
            case 3: // Display edit page from "existing cafe info"
                dpm.displayEditPage(viewMode, temporaryCafeData, ownerFlag);

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

    /**
     * Read image from user's device.
     *
     * @param requestCode
     * @param resultCode
     * @param resultData
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == 1000 && resultCode == RESULT_OK) {
            Uri uri = null;
            if (resultData != null) {
//                uri = resultData.getData();
//                try {
                //uploadImageBmp = getBitmapFromUri(uri);
                uploadImageBmp = resizeBitmap(resultData.getData());
                dpm.setUploadImageBmp(uploadImageBmp);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }
        }
    }

    /**
     * Resize image 375x200
     *
     * @param uri
     * @return bitmap that is resized
     */
    private Bitmap resizeBitmap(Uri uri) {
        Bitmap resizeImage = null;

        BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
        // doesn't read out of size of screen
        decodeOptions.inJustDecodeBounds = true;
        try {
            InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
            BitmapFactory.decodeStream(inputStream, null, decodeOptions);
            inputStream.close();
            int actualWidth = decodeOptions.outWidth;
            int actualHeight = decodeOptions.outHeight;

            int desiredWidth = getResizedDimension(375, 200, actualWidth, actualHeight);
            int desiredHeight = getResizedDimension(200, 375, actualHeight, actualWidth);
            decodeOptions.inJustDecodeBounds = false;
            decodeOptions.inSampleSize = findBestSampleSize(actualWidth, actualHeight, desiredWidth, desiredHeight);

            inputStream = getContext().getContentResolver().openInputStream(uri);
            Bitmap tempBitmap = BitmapFactory.decodeStream(inputStream, null, decodeOptions);
            inputStream.close();
            if (tempBitmap != null && (tempBitmap.getWidth() > desiredWidth ||
                    tempBitmap.getHeight() > desiredHeight)) {
                resizeImage = Bitmap.createScaledBitmap(tempBitmap,
                        desiredWidth, desiredHeight, true);
                tempBitmap.recycle();
            } else {
                resizeImage = tempBitmap;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return resizeImage;
    }

    private int findBestSampleSize(
            int actualWidth, int actualHeight, int desiredWidth, int desiredHeight) {
        double wr = (double) actualWidth / desiredWidth;
        double hr = (double) actualHeight / desiredHeight;
        double ratio = Math.min(wr, hr);
        float n = 1.0f;
        while ((n * 2) <= ratio) {
            n *= 2;
        }
        return (int) n;
    }

    private int getResizedDimension(int maxPrimary, int maxSecondary, int actualPrimary,
                                    int actualSecondary) {
        if ((maxPrimary == 0) && (maxSecondary == 0)) {
            return actualPrimary;
        }

        if (maxPrimary == 0) {
            double ratio = (double) maxSecondary / (double) actualSecondary;
            return (int) (actualPrimary * ratio);
        }

        if (maxSecondary == 0) {
            return maxPrimary;
        }

        double ratio = (double) actualSecondary / (double) actualPrimary;
        int resized = maxPrimary;

        if ((resized * ratio) < maxSecondary) {
            resized = (int) (maxSecondary / ratio);
        }
        return resized;
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
