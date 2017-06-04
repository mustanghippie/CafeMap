package drgn.cafemap;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Nobu on 2017/06/03.
 */

public class FirebaseClass {

    private DatabaseReference reference;

    public FirebaseClass(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        reference = FirebaseDatabase.getInstance().getReference();

        reference.child("message").setValue("hoge");

        myRef.setValue("HOGE");
        Log.d("Firebase","firebase");
    }

}
