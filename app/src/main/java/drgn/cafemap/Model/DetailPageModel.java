package drgn.cafemap.Model;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.support.media.ExifInterface;
import android.support.v4.app.FragmentActivity;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import drgn.cafemap.Controller.Activity.CafeActivity;
import drgn.cafemap.Controller.Activity.MapsActivity;
import drgn.cafemap.R;
import drgn.cafemap.Util.AnimationUtil;

import static android.content.Context.MODE_PRIVATE;

/**
 * -------------------------------------
 * Todo This class is deprecated.
 * I'm going to delete this class later.
 * -------------------------------------
 */
public class DetailPageModel {
    // Base param
    private double lat;
    private double lon;
    private Context context;
    private UserCafeMapModel ucm;
    private FragmentActivity fragmentActivity;
    private View view;
    private Resources resources;
    // Common View
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
    private ImageButton addBookmarkButton;
    private ImageButton mailButton;


    public DetailPageModel(Context context, View view, FragmentActivity fragmentActivity, Resources resources, double lat, double lon) {
        this.context = context;
        this.view = view;
        this.fragmentActivity = fragmentActivity;
        this.resources = resources;
        this.lat = lat;
        this.lon = lon;
        this.ucm = new UserCafeMapModel(context);
    }

    private void prepareViewContents(int viewMode) {

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
                this.mailButton = (ImageButton) view.findViewById(R.id.mailButton);
                this.addBookmarkButton = (ImageButton) view.findViewById(R.id.addBookmarkButton);
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
     * Displays preview page
     * View mode 2
     *
     * @param viewMode
     * @param cafeData
     */
    public void displayPreviewPage(int viewMode, Map<String, String> cafeData) {
        final Map<String, String> temporaryCafeData = cafeData;
        prepareViewContents(viewMode);

        // read preview image
        try {
            InputStream in = context.openFileInput("preview.png");
            uploadImageBmp = BitmapFactory.decodeStream(in);
            // delete a temp file
            context.deleteFile("preview.png");
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
                CafeUserTblHelper cafeUserTblHelper = new CafeUserTblHelper(context);
                boolean successFlag = cafeUserTblHelper.executeInsert(lat, lon, temporaryCafeData.get("cafeName"), temporaryCafeData.get("cafeAddress"),
                        temporaryCafeData.get("cafeTime"), temporaryCafeData.get("cafeTel"),
                        temporaryCafeData.get("cafeSocket"), temporaryCafeData.get("cafeWifi"), new UserCafeMapModel(context).convertBitmapToByte(uploadImageBmp), 0);

                // If cafe data already exist, execute UPDATE
                if (!successFlag) {
                    cafeUserTblHelper.executeUpdate(lat, lon, temporaryCafeData.get("cafeName"), temporaryCafeData.get("cafeAddress"),
                            temporaryCafeData.get("cafeTime"), temporaryCafeData.get("cafeTel"),
                            temporaryCafeData.get("cafeSocket"), temporaryCafeData.get("cafeWifi"), new UserCafeMapModel(context).convertBitmapToByte(uploadImageBmp), 0);
                }

                Intent intent = new Intent(context, MapsActivity.class);

                intent.putExtra("defaultPosLat", lat);
                intent.putExtra("defaultPosLon", lon);
                fragmentActivity.startActivity(intent);
            }
        });
    }

    /**
     * Displays edit page
     * View mode 0 or 3
     * From making a new data
     *
     * @param viewMode
     */
    public void displayEditPage(int viewMode) { // view 0
        prepareViewContents(viewMode);

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

                Intent intent = new Intent(context, CafeActivity.class);

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
                fragmentActivity.startActivity(intent);

            }
        });

    }

    // From existing data(View mode 3)
    public void displayEditPage(int viewMode, Map<String, String> cafeData, boolean ownerFlag, final Activity activity) {
        prepareViewContents(viewMode);
        final Map<String, String> previousCafeData = cafeData;

        nameTextView.setText(previousCafeData.get("cafeName"));
        addressTextView.setText(previousCafeData.get("cafeAddress"));
        telTextView.setText(previousCafeData.get("cafeTel"));

        // set time parts
        String[] timeArray = previousCafeData.get("cafeTime").split(" - ");
        final String fromTime = timeArray[0].replaceAll("AM", "").replaceAll("PM", ""); // EX) 10:00
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
        if (ownerFlag) {
            uploadImageBmp = getImageFromAssets(previousCafeData.get("cafeName"));
            uploadImage.setImageBitmap(uploadImageBmp);
        } else {
            uploadImageBmp = new UserCafeMapModel(context).getCafeImage(lat, lon);
            uploadImage.setImageBitmap(uploadImageBmp);
        }

        // Delete only existing cafe info
        if (!ownerFlag) {
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new AlertDialog.Builder(fragmentActivity)
                            .setTitle("Delete cafe information")
                            .setMessage("Are you sure you want to permanently delete this cafe info ?")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // OK button pressed
                                    new CafeUserTblHelper(context).executeDelete(lat, lon); // DELETE

                                    Intent intent = new Intent(context, MapsActivity.class);

                                    intent.putExtra("defaultPosLat", lat);
                                    intent.putExtra("defaultPosLon", lon);
                                    fragmentActivity.startActivity(intent);
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                }
            });
        } else {
            deleteButton.setVisibility(View.INVISIBLE);
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

                Intent intent = new Intent(context, CafeActivity.class);

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
                    uploadImageBmp.compress(Bitmap.CompressFormat.PNG, 100, out);
                    out.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                fragmentActivity.startActivity(intent);
            }
        });
    }

    /**
     * Displays detail page
     * View mode 1
     *
     * @param viewMode
     */
    public void displayDetailPage(int viewMode, final boolean ownerFlag) {
        prepareViewContents(viewMode);

        // image in cafe_user_tbl exits or not
        // Get data owner's or user's
        final Map<String, String> cafeDetail;
        Bitmap image = null;

        if (ownerFlag) {
            cafeDetail = new CafeMasterTblHelper(context).executeSelect(lat, lon);
            image = getImageFromAssets(cafeDetail.get("cafeName").toString());
        } else {
            cafeDetail = new CafeUserTblHelper(context).executeSelect(lat, lon);
            image = new UserCafeMapModel(context).getCafeImage(lat, lon);
        }

        nameTextView.setText(cafeDetail.get("cafeName").toString());
        addressTextView.setText(cafeDetail.get("cafeAddress").toString());
        telTextView.setText(cafeDetail.get("cafeTel").toString());
        timeTextView.setText(cafeDetail.get("cafeTime").toString());
        socketTextView.setText(cafeDetail.get("cafeSocket").toString());
        wifiTextView.setText(cafeDetail.get("cafeWifi").toString());
        cafeScreenshot.setImageBitmap(image);

        //if (type.equals("master")) editButton.setVisibility(View.INVISIBLE);

        // go to edit page
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // click animation
                new AnimationUtil().clickFadeInFadeOutAnimation(editButton);

                Intent intent = new Intent(context, CafeActivity.class);
                intent.putExtra("lat", lat);
                intent.putExtra("lon", lon);
                intent.putExtra("viewMode", 3);
                intent.putExtra("ownerFlag", ownerFlag);
                intent.putExtra("cafeName", cafeDetail.get("cafeName").toString());
                intent.putExtra("cafeAddress", cafeDetail.get("cafeAddress").toString());
                intent.putExtra("cafeTel", cafeDetail.get("cafeTel").toString());
                intent.putExtra("cafeTime", cafeDetail.get("cafeTime").toString());
                intent.putExtra("cafeSocket", cafeDetail.get("cafeSocket").toString());
                intent.putExtra("cafeWifi", cafeDetail.get("cafeWifi").toString());

                fragmentActivity.startActivity(intent);

            }
        });

        // to be invisible in cafe master case
        if (ownerFlag) mailButton.setVisibility(View.INVISIBLE);

        // send cafe information
        mailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                // click animation
                new AnimationUtil().clickFadeInFadeOutAnimation(mailButton);

                new AlertDialog.Builder(fragmentActivity)
                        .setTitle("Would it be possible to send your cafe map?")
                        .setMessage("About your cafe information submitted here, we'll register this with " +
                                "our application database.\nPlease press the \"AGREE\" button if you agree with us.")
                        .setPositiveButton("Agree", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // OK button pressed
                                // check send_flag preventing to send email in a row
                                boolean sendFlag = new CafeUserTblHelper(context).checkSendFlag(lat, lon);
                                if (!sendFlag) {
                                    AsyncTaskSendMail asyncTaskSendMail = new AsyncTaskSendMail(context, lat, lon);
                                    asyncTaskSendMail.execute("");
                                }
                                Toast.makeText(context, "Thank you for your support!!", Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

        // get bookmark flag
        boolean bookmarkFlag;
        if (ownerFlag) bookmarkFlag = new CafeMasterTblHelper(context).checkBookmarkFlag(lat, lon);
        else bookmarkFlag = new CafeUserTblHelper(context).checkBookmarkFlag(lat, lon);

        // change bookmark icon and set flag
        if (bookmarkFlag) addBookmarkButton.setImageResource(R.drawable.icon_bookmark_added);
        else addBookmarkButton.setImageResource(R.drawable.icon_bookmark_add);

        // bookmark button
        addBookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean bookmarkFlag;

                // click animation
                new AnimationUtil().clickFadeInFadeOutAnimation(addBookmarkButton);

                if (ownerFlag)
                    bookmarkFlag = new CafeMasterTblHelper(context).checkBookmarkFlag(lat, lon);
                else bookmarkFlag = new CafeUserTblHelper(context).checkBookmarkFlag(lat, lon);

                // update bookmark flag on database table
                if (ownerFlag) {
                    new CafeMasterTblHelper(context).executeUpdateBookmark(lat, lon, !bookmarkFlag);
                } else {
                    new CafeUserTblHelper(context).executeUpdateBookmark(lat, lon, !bookmarkFlag);
                }

                // change bookmark icon and set flag
                if (!bookmarkFlag)
                    addBookmarkButton.setImageResource(R.drawable.icon_bookmark_added);
                else addBookmarkButton.setImageResource(R.drawable.icon_bookmark_add);
            }
        });

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
     * Sets a upload image by the preview button.
     * This method suppose to be called
     * from onActivityResult@DetailPageFragment.
     * <p>
     * Resize image
     *
     * @param uploadImageBmp
     */
    public void setUploadImageBmp(Bitmap uploadImageBmp, int orientation) {

        WindowManager windowManager = ((Activity) context).getWindowManager();
        // make instance of the window
        Display disp = windowManager.getDefaultDisplay();
        Point size = new Point();
        disp.getSize(size);
        float viewWidth = size.x;
        float viewHeight = size.y;

        Matrix mat = new Matrix();

        // get width and height
        int wOrg = uploadImageBmp.getWidth();
        int hOrg = uploadImageBmp.getHeight();

        // リサイズ比の取得（画像の短辺がMAX_PIXELになる比を求めます）
        float scale = Math.max(viewWidth / wOrg, viewHeight / hOrg);
        if (scale < 1.0) {
            mat.postScale(scale, scale);
        }

        switch (orientation) {
            case ExifInterface.ORIENTATION_UNDEFINED:
                break;
            case ExifInterface.ORIENTATION_NORMAL:
                break;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL://flip vertical
                mat.postScale(1f, -1f);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180://flip horizontal
                mat.postRotate(180f);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90://flip vertical rotate270
                mat.postRotate(90f);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE://rotate 90
                mat.postRotate(-90f);
                mat.postScale(1f, -1f);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE://flip vertical, rotate 90
                mat.postRotate(90f);
                mat.postScale(1f, -1f);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270://rotate 270
                mat.postRotate(-90f);
                break;
        }

        Bitmap resizeBitmap = Bitmap.createBitmap(uploadImageBmp, 0, 0, wOrg, hOrg, mat, true);

        // save image in local
        OutputStream out;
        try {
            out = context.openFileOutput("preview.png", context.MODE_PRIVATE);
            resizeBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.uploadImage.setImageBitmap(resizeBitmap);
        this.uploadImageBmp = resizeBitmap;
    }

    /**
     * Obtains PNG image from assets folder.
     *
     * @param name
     * @return Bitmap image
     */
    public Bitmap getImageFromAssets(String name) {
        Bitmap bitmap = null;

        try {
            InputStream inputStream = resources.getAssets().open(name.replaceAll(" ", "_").toLowerCase() + ".png");
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }
}