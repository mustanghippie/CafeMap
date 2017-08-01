package drgn.cafemap.Controller.Fragment;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

import drgn.cafemap.Controller.Activity.MapsActivity;
import drgn.cafemap.Model.AsyncTaskAddressGeocoder;
import drgn.cafemap.Model.CafeModel;
import drgn.cafemap.R;
import drgn.cafemap.databinding.FragmentEditPageBinding;
import drgn.cafemap.Object.Cafe;
import drgn.cafemap.Util.EditPageHandlers;

import static android.app.Activity.RESULT_OK;


public class EditPageFragment extends Fragment implements EditPageHandlers,View.OnKeyListener {

    private Context context;
    private FragmentEditPageBinding binding;
    private EditPageFragmentListener listener;

    private Bitmap uploadImageBmp = null;
    private double lat, lon;
    private boolean ownerFlag;
    private boolean existingDataFlag;
    private CafeModel cafeModel;

    public interface EditPageFragmentListener {
        void goToPreviewPageEvent(Cafe cafe);
    }

    public EditPageFragment() {
        // Required empty public constructor
    }

    public static EditPageFragment newInstance(double lat, double lon, boolean ownerFlag, boolean existingDataFlag) {
        EditPageFragment fragment = new EditPageFragment();
        Bundle args = new Bundle();
        args.putDouble("lat", lat);
        args.putDouble("lon", lon);
        args.putBoolean("ownerFlag", ownerFlag);
        args.putBoolean("existingDataFlag", existingDataFlag);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof EditPageFragmentListener) {
            listener = (EditPageFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement EditPageFragmentListener");
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.lat = getArguments().getDouble("lat");
            this.lon = getArguments().getDouble("lon");
            this.ownerFlag = getArguments().getBoolean("ownerFlag");
            this.existingDataFlag = getArguments().getBoolean("existingDataFlag");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_page, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // initialize
        this.context = getContext();
        this.cafeModel = new CafeModel(context, getResources());
        // prepare binding
        this.binding = FragmentEditPageBinding.bind(view);
        binding.setHandlers(this);
        // close the keyboard window
        binding.cafeAddress.setOnKeyListener(this);
        binding.cafeTel.setOnKeyListener(this);

        if (existingDataFlag) {
            Cafe cafe = cafeModel.getCafeInstance(ownerFlag, lat, lon);
            // setting cafe detail
            binding.setCafe(cafe);
            // setting cafe time spinners
            String[] timeArray = cafe.getCafeTime().split(" - ");
            String fromTime = timeArray[0].replaceAll("AM", "").replaceAll("PM", ""); // EX) 10:00
            String toTime = timeArray[1].replaceAll("AM", "").replaceAll("PM", ""); // EX) 19:00
            cafeModel.setSpinnerData(binding.startHour, fromTime.split(":")[0]);
            cafeModel.setSpinnerData(binding.startMinute, fromTime.split(":")[1]);
            cafeModel.setSpinnerData(binding.startAmPm, timeArray[0].substring(timeArray[0].length() - 2, timeArray[0].length()));
            cafeModel.setSpinnerData(binding.endHour, toTime.split(":")[0]);
            cafeModel.setSpinnerData(binding.endMinute, toTime.split(":")[1]);
            cafeModel.setSpinnerData(binding.endAmPm, timeArray[1].substring(timeArray[1].length() - 2, timeArray[1].length()));
            // setting socket spinner
            cafeModel.setSpinnerData(binding.cafeSocket, cafe.getCafeSocket());
            // setting wifi spinner
            cafeModel.setSpinnerData(binding.cafeWifi, cafe.getCafeWifi());
            // setting an upload image
            this.uploadImageBmp = cafeModel.getCafeImage(ownerFlag, lat, lon);
            binding.uploadImage.setImageBitmap(uploadImageBmp);

            // hide delete button in owner cafe case
            if (ownerFlag) binding.deleteButton.setVisibility(View.INVISIBLE);

        } else {
            Cafe cafe = new Cafe("", "", "", "", "", "");
            binding.setCafe(cafe);

            // get address by geocoder
            AsyncTaskAddressGeocoder geocoder = new AsyncTaskAddressGeocoder(context, cafe, lat, lon);
            geocoder.execute();
            // hide delete button
            binding.deleteButton.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Closes the keyboard window after pressed the enter key.
     *
     * @param v
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        // get an event when pressed button that is the enter key.
        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
            // close android keyboard
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(binding.cafeTel.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN); // or binding cafeAddress
            return true;
        }

        return false;
    }
    /**
     * Opens user's picture folder and uploads a image
     * when a user click the upload button.
     *
     * @param view
     */
    @Override
    public void onClickUploadImageButton(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");

        startActivityForResult(intent, 1000);
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
            Uri uri = resultData.getData();
            if (resultData != null) {
                try {
                    // get exif data from a picture
                    InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
                    ExifInterface exif = new ExifInterface(inputStream);
                    int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//
//                    Bitmap uploadImageBmp = getBitmapFromUri(uri);
                    Bitmap resizeUploadImageBmp = cafeModel.resizeBitmap(uri);
                    Bitmap fixedImage = cafeModel.fixImageOrientation(resizeUploadImageBmp, exifOrientation);

                    // assign fixed image global variable to save it in android's local
                    this.uploadImageBmp = fixedImage;
                    binding.uploadImage.setImageBitmap(fixedImage);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onClickPreviewButton(View view) {

        // name
        String cafeName = binding.cafeName.getText().toString();
        // cafe address
        String cafeAddress = binding.cafeAddress.getText().toString();
        // cafe tel
        String cafeTel = binding.cafeTel.getText().toString();
        // cafe time
        String startHour = binding.startHour.getSelectedItem().toString();
        String startMinute = binding.startMinute.getSelectedItem().toString();
        String startAmPm = binding.startAmPm.getSelectedItem().toString();
        String endHour = binding.endHour.getSelectedItem().toString();
        String endMinute = binding.endMinute.getSelectedItem().toString();
        String endAmPm = binding.endAmPm.getSelectedItem().toString();
        String cafeTime = startHour + ":" + startMinute + startAmPm + " - " + endHour + ":" + endMinute + endAmPm;
        // socket
        String cafeSocket = binding.cafeSocket.getSelectedItem().toString();
        // wifi
        String cafeWifi = binding.cafeWifi.getSelectedItem().toString();

        Cafe cafe = new Cafe(cafeName, cafeAddress, cafeTime, cafeTel, cafeSocket, cafeWifi);
        // save image in android's local
        cafeModel.savePreviewImageOnLocal(uploadImageBmp);

        // save an upload image in android's local
        if (listener != null) {
            listener.goToPreviewPageEvent(cafe);
        }
    }

    /**
     * Delete button.
     *
     * @param view
     */
    @Override
    public void onClickDeleteButton(View view) {

        new AlertDialog.Builder(getActivity())
                .setTitle("Delete cafe information")
                .setMessage("Are you sure you want to permanently delete this cafe info ?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // OK button pressed
                        boolean successfully = cafeModel.deleteCafeData(lat, lon);
                        if (successfully) {
                            Toast.makeText(context, "Deleted successfully.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Sorry, failed to delete your cafe map.", Toast.LENGTH_SHORT).show();
                        }
                        Intent intent = new Intent(context, MapsActivity.class);

                        intent.putExtra("defaultPosLat", lat);
                        intent.putExtra("defaultPosLon", lon);
                        getActivity().startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


}
