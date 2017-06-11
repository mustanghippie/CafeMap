package drgn.cafemap;

import android.app.ActionBar;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;

import static drgn.cafemap.MapsActivity.atms;

public class DetailPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_page);

        // make fragment
        FragmentDetailPage fragment = new FragmentDetailPage();
        // Fragmentの追加や削除といった変更を行う際は、Transactionを利用します
        // Bundle for Fragment to send parameter
        Bundle args = new Bundle();
        args.putInt("viewMode",getIntent().getIntExtra("viewMode",1));
        args.putDouble("lat",getIntent().getDoubleExtra("lat",0));
        args.putDouble("lon",getIntent().getDoubleExtra("lon",0));
        fragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // メソッドの1つ目の引数は対象のViewGroupのID、2つ目の引数は追加するfragment
        transaction.add(R.id.container, fragment);
        // 最後にcommitを使用することで変更を反映します
        transaction.commit();
    }

}
