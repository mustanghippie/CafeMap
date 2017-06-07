package drgn.cafemap;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class DetailPageActivity extends AppCompatActivity {

    private TextView nameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_page);

        System.out.println("Ryu¥¥¥¥¥");

        nameTextView = (TextView) findViewById(R.id.cafeName);
        nameTextView.setText(getIntent().getStringExtra("key"));

        MapsActivity.fcm.getTestValue();

    }

}
