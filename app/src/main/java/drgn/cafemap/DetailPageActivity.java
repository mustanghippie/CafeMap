package drgn.cafemap;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.HashMap;

public class DetailPageActivity extends AppCompatActivity {

    private TextView nameTextView;
    private TextView addressTextView;
    private TextView timeTextView;
    private TextView socketTextView;
    private TextView wifiTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_page);
        String key = getIntent().getStringExtra("indexKey"); // latitude + longitude

        HashMap<String, String> cafeDetail = MapsActivity.fcm.getCafeDetailMap(key);

        nameTextView = (TextView) findViewById(R.id.cafeName);
        nameTextView.setText(cafeDetail.get("name"));
        addressTextView = (TextView) findViewById(R.id.cafeAddress);
        addressTextView.setText(cafeDetail.get("address"));
        timeTextView = (TextView) findViewById(R.id.cafeTime);
        timeTextView.setText(cafeDetail.get("time"));
        socketTextView = (TextView) findViewById(R.id.cafeSocket);
        socketTextView.setText("(Socket) " +cafeDetail.get("socket"));
        wifiTextView = (TextView) findViewById(R.id.cafeWifi);
        wifiTextView.setText("(Wi-fi) " +cafeDetail.get("wifi"));

    }

}
