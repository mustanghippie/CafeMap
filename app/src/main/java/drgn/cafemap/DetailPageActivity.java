package drgn.cafemap;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class DetailPageActivity extends AppCompatActivity {

    private TextView nameTextView;
    private TextView timeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_page);

        nameTextView = (TextView) findViewById(R.id.cafeName);
        nameTextView.setText(getIntent().getStringExtra("key"));

        timeTextView = (TextView) findViewById(R.id.cafeTime);

        timeTextView = (TextView) findViewById(R.id.cafeTel);

        timeTextView = (TextView) findViewById(R.id.cafeSocket);

        timeTextView = (TextView) findViewById(R.id.cafeWifi);

        timeTextView = (TextView) findViewById(R.id.cafePower);

        MapsActivity.fcm.getTestValue();

    }

}
