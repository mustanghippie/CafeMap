package drgn.cafemap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
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

import static android.app.Activity.RESULT_OK;

public class FragmentDetailPage extends Fragment {

    private String key;
    private int viewMode; // viewMode: 0 => make a new data, 1 => display cafe info 2 => preview
    private boolean newMarkerFlag;
    private AsyncTaskMarkerSet atms;
    private ImageView uploadImage;
    private Bitmap uploadImageBmp;
    private double lat, lon;

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

        switch (viewMode){
            case 0:
                layoutId = R.layout.activity_fragment_detail_edit_page;
                break;
            case 1:
                layoutId = R.layout.activity_fragment_detail_page;
                break;
            case 2:
                System.out.println("viewMode = 2");
                break;
            default:
                Log.d("[Error] viewMode ","Please check viewMode");
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
    private void setParamsInView(View view) {

        final TextView nameTextView;
        final TextView addressTextView;
        TextView timeTextView;
        TextView telTextView;
        TextView socketTextView;
        TextView wifiTextView;
        Button saveButton;
        Button uploadImageButton;
        Button previewButton;

        // cafe name
        nameTextView = (TextView) view.findViewById(R.id.cafeName);
        // cafe address
        addressTextView = (TextView) view.findViewById(R.id.cafeAddress);

        switch (viewMode){
            case 0:
                // set address by GetAddressGeocoder
                GetAddressGeocoder coder = new GetAddressGeocoder(getContext(), addressTextView, lat, lon);
                coder.execute();

                uploadImage = (ImageView) view.findViewById(R.id.uploadImage);

                saveButton = (Button) view.findViewById(R.id.saveButton);
                uploadImageButton = (Button) view.findViewById(R.id.uploadImageButton);
                previewButton = (Button) view.findViewById(R.id.previewButton);

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

                // save action
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String cafeName = nameTextView.getText().toString();
                        String cafeAddress = addressTextView.getText().toString();

                        UserCafeMapModel ucmm = new UserCafeMapModel(v, getContext());
                        // save image
                        ucmm.saveUserCafeMapImage(uploadImageBmp, key);
                        // save cafe information
                        ucmm.saveUserCafeMap(key, cafeName, cafeAddress);
                    }
                });

                // preview
                previewButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FragmentDetailPage fragment = new FragmentDetailPage();
                        Bundle args = new Bundle();
                        args.putInt("viewMode",2);
                        args.putDouble("lat",lat);
                        args.putDouble("lon",lon);
                        fragment.setArguments(args);

                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        // メソッドの1つ目の引数は対象のViewGroupのID、2つ目の引数は追加するfragment
                        transaction.add(R.id.container, fragment);
                        // 最後にcommitを使用することで変更を反映します
                        transaction.commit();
                    }
                });
                break;
            case 1:
                // Cafe information is made by the owner
                atms = MapsActivity.atms;

                HashMap<String, String> cafeDetail = atms.getCafeMap().get(key);
                String imageName = atms.getCafeMap().get(key).get("name").replaceAll(" ", "_").toLowerCase();
                Bitmap image = atms.getCafeBitmapMap().get(imageName);

                // Set image
                ImageView img = (ImageView) view.findViewById(R.id.badge);
                img.setImageBitmap(image);

                nameTextView.setText(cafeDetail.get("name"));
                addressTextView.setText(cafeDetail.get("address"));
                telTextView = (TextView) view.findViewById(R.id.cafeTel);
                telTextView.setText(cafeDetail.get("tel"));
                timeTextView = (TextView) view.findViewById(R.id.cafeTime);
                timeTextView.setText(cafeDetail.get("time"));
                socketTextView = (TextView) view.findViewById(R.id.cafeSocket);
                socketTextView.setText("(Socket) " + cafeDetail.get("socket"));
                wifiTextView = (TextView) view.findViewById(R.id.cafeWifi);
                wifiTextView.setText("(Wi-fi) " + cafeDetail.get("wifi"));
                break;
            case 2:

                break;
            default:
                Log.d("[Error] viewMode ","Please check viewMode");
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
