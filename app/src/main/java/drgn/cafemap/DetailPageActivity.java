package drgn.cafemap;

import android.app.ActionBar;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;

import static drgn.cafemap.MapsActivity.atms;

public class DetailPageActivity extends AppCompatActivity {

    private TextView nameTextView;
    private TextView addressTextView;
    private TextView timeTextView;
    private TextView telTextView;
    private TextView socketTextView;
    private TextView wifiTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_page);

        // check a new marker
        String key = getIntent().getStringExtra("indexKey"); // latitude + longitude

        HashMap<String, String> cafeDetail = atms.getCafeMap().get(key);


        View view = getLayoutInflater().inflate(R.layout.activity_detail_page, null);

//        ImageView img = (ImageView) view.findViewById(R.id.badge);
//        Bitmap image = atms.getCafeBitmapMap().get(marker.getTitle().replaceAll(" ", "_").toLowerCase());
//        img.setImageBitmap(image);
//        atms.getCafeBitmap().get("trees_organic_coffee");


        nameTextView = (TextView) findViewById(R.id.cafeName);
        nameTextView.setText(cafeDetail.get("name"));
        addressTextView = (TextView) findViewById(R.id.cafeAddress);
        addressTextView.setText(cafeDetail.get("address"));
        telTextView = (TextView) findViewById(R.id.cafeTel);
        telTextView.setText(cafeDetail.get("tel"));
        timeTextView = (TextView) findViewById(R.id.cafeTime);
        timeTextView.setText(cafeDetail.get("time"));
        socketTextView = (TextView) findViewById(R.id.cafeSocket);
        socketTextView.setText("(Socket) " + cafeDetail.get("socket"));
        wifiTextView = (TextView) findViewById(R.id.cafeWifi);
        wifiTextView.setText("(Wi-fi) " + cafeDetail.get("wifi"));

    }

}
