package drgn.cafemap.Model;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by Nobu on 2017/07/31.
 */

public class FirebaseStorageConnection {

    private Context context;
    private Resources resources;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private FirebaseStorageListener listener;
    final private String TAG = "FirebaseStorage";

    final long ONE_MEGABYTE = 1024 * 1024;

    public interface FirebaseStorageListener {
        void updateInfoWindowView(Marker marker);
    }

    public FirebaseStorageConnection(Context context, Resources resources) {
        this.storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        this.storageRef = storage.getReferenceFromUrl("gs://cafemap-530a2.appspot.com");
        this.context = context;
        this.resources = resources;
        if (context instanceof FirebaseStorageListener) {
            this.listener = (FirebaseStorageListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement DetailPageFragmentListener");
        }
    }

    public void getImageFromFirebaseStorage(final String imageName, final Marker marker) {
//        final StorageReference imagesRef = storageRef.child("CafeImages/sample.png");
        StorageReference imagesRef = storageRef.child("CafeImages/" + imageName + ".png");

        imagesRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // save image as a local file
                Bitmap cafeImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                CafeModel cafeModel = new CafeModel(context, resources);
                cafeModel.saveImageInLocal(cafeImage, imageName);
                listener.updateInfoWindowView(marker);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"onFailure");
            }
        });

    }

}
