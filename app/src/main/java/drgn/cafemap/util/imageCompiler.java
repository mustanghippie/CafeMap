package drgn.cafemap.util;

import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

import drgn.cafemap.Model.UserCafeMapModel;

/**
 * Created by musta on 2017/06/24.
 */

public class imageCompiler {
    // Firebase storage
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private StorageReference imageRef;

    public imageCompiler(){
        // Firebase storage
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://cafemap-530a2.appspot.com");
        imageRef = storageRef.child("firebase.png");

    }

    public void compileImage(){

        try {
            String cafeName = "";

            final File localFile = File.createTempFile(cafeName, "png");
            imageRef = storageRef.child(cafeName + ".png");

            imageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {


                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("404Error possibly ", "File not found");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
