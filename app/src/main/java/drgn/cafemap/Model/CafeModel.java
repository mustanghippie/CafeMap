package drgn.cafemap.Model;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.Uri;
import android.support.media.ExifInterface;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import drgn.cafemap.util.Cafe;

import static android.content.Context.MODE_PRIVATE;


/**
 * Created by Nobu on 2017/07/21.
 */

public class CafeModel {

    private Context context;
    private Resources resources;

    public CafeModel(Context context, Resources resources) {
        this.context = context;
        this.resources = resources;
    }

    /**
     * Makes cafe instance.
     *
     * @param ownerFlag
     * @param lat
     * @param lon
     * @return Cafe
     */
    public Cafe getCafeInstance(boolean ownerFlag, double lat, double lon) {
        Map<String, String> cafeDetail;
        if (ownerFlag) {
            cafeDetail = new CafeMasterTblHelper(context).executeSelect(lat, lon);
        } else {
            cafeDetail = new CafeUserTblHelper(context).executeSelect(lat, lon);
        }

        String cafeName = cafeDetail.get("cafeName");
        String cafeAddress = cafeDetail.get("cafeAddress");
        String cafeTel = cafeDetail.get("cafeTel");
        String cafeTime = cafeDetail.get("cafeTime");
        String cafeSocket = cafeDetail.get("cafeSocket");
        String cafeWifi = cafeDetail.get("cafeWifi");

        Cafe cafe = new Cafe(cafeName, cafeAddress, cafeTime, cafeTel, cafeSocket, cafeWifi);

        return cafe;
    }

    /**
     * Obtains a cafe image by using cafe name in owner case.
     * Or by using lat and lon in user data case.
     *
     * @param ownerFlag
     * @param lat
     * @param lon
     * @return bitmap
     */
    public Bitmap getCafeImage(boolean ownerFlag, double lat, double lon) {
        Bitmap image;
        if (ownerFlag) {
            String cafeName = new CafeMasterTblHelper(context).executeSelectCafeName(lat, lon);
            image = this.getImageFromAssets(cafeName);
        } else {
            image = new UserCafeMapModel(context).getCafeImage(lat, lon);
        }

        return image;
    }

    /**
     * Obtains bookmark flag from database.
     *
     * @param ownerFlag
     * @param lat
     * @param lon
     * @return boolean true = bookmarked false = not bookmarked
     */
    public boolean getBookmarkFlag(boolean ownerFlag, double lat, double lon) {
        boolean bookmarkFlag;


        if (ownerFlag) {
            bookmarkFlag = new CafeMasterTblHelper(context).checkBookmarkFlag(lat, lon);
        } else {
            bookmarkFlag = new CafeUserTblHelper(context).checkBookmarkFlag(lat, lon);
        }

        return bookmarkFlag;
    }

    /**
     * Update bookmark flag on cafe_master_tbl or cafe_user_tbl.
     *
     * @param ownerFlag
     * @param lat
     * @param lon
     * @param bookmarkFlag
     * @return boolean successfully or not
     */
    public boolean updateBookmarkFlag(boolean ownerFlag, double lat, double lon, boolean bookmarkFlag) {

        boolean successfully = true;

        if (ownerFlag) {
            new CafeMasterTblHelper(context).executeUpdateBookmark(lat, lon, !bookmarkFlag);
        } else {
            new CafeUserTblHelper(context).executeUpdateBookmark(lat, lon, !bookmarkFlag);
        }

        return successfully;
    }

    /**
     * Obtains PNG image from assets folder.
     *
     * @param name
     * @return Bitmap image
     */
    private Bitmap getImageFromAssets(String name) {
        Bitmap bitmap = null;

        try {
            InputStream inputStream = resources.getAssets().open(name.replaceAll(" ", "_").toLowerCase() + ".png");
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public void setSpinnerData(Spinner spinner, String value) {
        Adapter adp = (ArrayAdapter<String>) spinner.getAdapter();
        int index = 0;
        for (int i = 0; i < adp.getCount(); i++) {
            if (adp.getItem(i).equals(value)) {
                index = i;
                break;
            }
        }
        spinner.setSelection(index);
    }

    /**
     * Resize image 375x200
     *
     * @param uri
     * @return bitmap that is resized
     */
    public Bitmap resizeBitmap(Uri uri) {
        Bitmap resizeImage = null;

        BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
        // doesn't read out of size of screen
        decodeOptions.inJustDecodeBounds = true;
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            BitmapFactory.decodeStream(inputStream, null, decodeOptions);
            inputStream.close();
            int actualWidth = decodeOptions.outWidth;
            int actualHeight = decodeOptions.outHeight;

            int desiredWidth = getResizedDimension(375, 200, actualWidth, actualHeight);
            int desiredHeight = getResizedDimension(200, 375, actualHeight, actualWidth);
            decodeOptions.inJustDecodeBounds = false;
            decodeOptions.inSampleSize = findBestSampleSize(actualWidth, actualHeight, desiredWidth, desiredHeight);

            inputStream = context.getContentResolver().openInputStream(uri);
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

    /**
     * @param uploadImageBmp
     */
    public Bitmap fixImageOrientation(Bitmap uploadImageBmp, int orientation) {

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

        Bitmap fixedOrientationImage = Bitmap.createBitmap(uploadImageBmp, 0, 0, wOrg, hOrg, mat, true);

        // save image in local
        OutputStream out;
        try {
            out = context.openFileOutput("preview.png", context.MODE_PRIVATE);
            fixedOrientationImage.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        this.uploadImage.setImageBitmap(resizeBitmap);
//        this.uploadImageBmp = resizeBitmap;

        return fixedOrientationImage;
    }

    /**
     * Deletes data of cafe user tbl.
     *
     * @param lat
     * @param lon
     * @return successfully flag
     */
    public boolean deleteCafeData(double lat, double lon) {
        boolean successfully = true;

        successfully = new CafeUserTblHelper(context).executeDelete(lat, lon);

        return successfully;
    }

    /**
     * Saves uploaded image on android's local.
     * If a user doesn't upload a image,
     * it's going to save noImage.png on the local.
     *
     * @param image
     * @return boolean successfully flag
     */
    public boolean savePreviewImageOnLocal(Bitmap image) {
        boolean successfully = true;

        try {
            OutputStream out = context.openFileOutput("preview.png", MODE_PRIVATE);
            if (image == null) {
                // Set no image
                try {
                    InputStream inputStream = resources.getAssets().open("noImage.png");
                    image = BitmapFactory.decodeStream(inputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            image.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return successfully;
    }

    /**
     * Reads preview image.
     *
     * @return Bitmap preview.png
     */
    public Bitmap readPreviewImageFromLocal() {
        Bitmap image = null;

        try {
            InputStream in = context.openFileInput("preview.png");
            image = BitmapFactory.decodeStream(in);
            // delete a temp file
//            context.deleteFile("preview.png");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    /**
     * Calls executeInsert on CafeUserTblHelper.
     *
     * @param cafe
     * @param lat
     * @param lon
     * @param uploadImage
     * @return successfully flag
     */
    public boolean insertCafeData(Cafe cafe, double lat, double lon, Bitmap uploadImage) {
        boolean successfully = true;

        String cafeName = cafe.getCafeName();
        String cafeAddress = cafe.getCafeAddress();
        String cafeTime = cafe.getCafeTime();
        String cafeTel = cafe.getCafeTel();
        String cafeSocket = cafe.getCafeSocket();
        String cafeWifi = cafe.getCafeWifi();

        successfully = new CafeUserTblHelper(context).executeInsert(lat, lon, cafeName, cafeAddress, cafeTime, cafeTel, cafeSocket, cafeWifi,
                convertBitmapToByte(uploadImage), 0);
        return successfully;
    }

    public boolean uploadCafeData(Cafe cafe, double lat, double lon, Bitmap uploadImage) {
        boolean successfully = true;

        String cafeName = cafe.getCafeName();
        String cafeAddress = cafe.getCafeAddress();
        String cafeTime = cafe.getCafeTime();
        String cafeTel = cafe.getCafeTel();
        String cafeSocket = cafe.getCafeSocket();
        String cafeWifi = cafe.getCafeWifi();

        successfully = new CafeUserTblHelper(context).executeUpdate(lat, lon, cafeName, cafeAddress, cafeTime, cafeTel, cafeSocket, cafeWifi,
                convertBitmapToByte(uploadImage), 0);
        return successfully;
    }

    /**
     * Converts bitmap image to byte[] data.
     * sqlite3 requires blob as byte[], that's why a bitmap has to convert bitmap into byte.
     *
     * @param bitmap
     * @return byte[]
     */
    private byte[] convertBitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        //PNG quality 100%
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

        byte[] result = byteArrayOutputStream.toByteArray();

        return result;
    }

    public boolean checkMailSendFlag(double lat, double lon) {
        boolean sendFlag = false;

        sendFlag = new CafeUserTblHelper(context).checkSendFlag(lat, lon);

        return sendFlag;
    }

}
