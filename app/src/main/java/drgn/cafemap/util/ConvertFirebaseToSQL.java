package drgn.cafemap.util;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import drgn.cafemap.R;

public class ConvertFirebaseToSQL extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convert_json_to_sql);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        /**
         * make insert
         */
        mDatabase.child("MarkerList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String title, wifi, socket, time, tel, address;
                String lat, lon;

                ArrayList<String> datalistForInserting = new ArrayList<>();

                // location start from 1
                for (int i = 1; ; i++) {
                    if ((title = dataSnapshot.child("location" + String.valueOf(i)).child("name").getValue(String.class)) == null)
                        break;
                    lat = dataSnapshot.child("location" + String.valueOf(i)).child("lat").getValue(double.class).toString();
                    lon = dataSnapshot.child("location" + String.valueOf(i)).child("lon").getValue(double.class).toString();
                    address = dataSnapshot.child("location" + String.valueOf(i)).child("address").getValue(String.class);
                    time = dataSnapshot.child("location" + String.valueOf(i)).child("time").getValue(String.class);
                    tel = dataSnapshot.child("location" + String.valueOf(i)).child("tel").getValue(String.class);
                    socket = dataSnapshot.child("location" + String.valueOf(i)).child("socket").getValue(String.class);
                    wifi = dataSnapshot.child("location" + String.valueOf(i)).child("wifi").getValue(String.class);

                    String query = "INSERT INTO cafe_master_tbl(lat, lon, name, address, time, tel, wifi, socket) " +
                            "VALUES('" + lat + "','" + lon + "','" + title + "', '" + address + "','" + time + "','" + tel + "','" + wifi + "','" + socket + "');";
                    datalistForInserting.add(query);

                }
                String val = "";
                for (String value : datalistForInserting) {
                    val += value + "\n";
                }

                EditText editText = (EditText) findViewById(R.id.sql);
                editText.setText(val);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
