package drgn.cafemap.Controller;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import drgn.cafemap.R;

public class DetailPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_page);

        // make fragment
        DetailPageFragment fragment = new DetailPageFragment();
        // Fragmentの追加や削除といった変更を行う際は、Transactionを利用します
        // Bundle for Fragment to send parameter
        Bundle args = new Bundle();
        args.putInt("viewMode",getIntent().getIntExtra("viewMode",1));
        args.putDouble("lat",getIntent().getDoubleExtra("lat",0));
        args.putDouble("lon",getIntent().getDoubleExtra("lon",0));
        // preview parameters
        if(getIntent().getIntExtra("viewMode",1) == 2 || getIntent().getIntExtra("viewMode",1) == 3){
            args.putString("cafeName", getIntent().getStringExtra("cafeName"));
            args.putString("cafeAddress", getIntent().getStringExtra("cafeAddress"));
            args.putString("cafeTel", getIntent().getStringExtra("cafeTel"));
            args.putString("cafeTime", getIntent().getStringExtra("cafeTime"));
            args.putString("cafeWifi", getIntent().getStringExtra("cafeWifi"));
            args.putString("cafeSocket", getIntent().getStringExtra("cafeSocket"));
        }


        fragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // メソッドの1つ目の引数は対象のViewGroupのID、2つ目の引数は追加するfragment
        transaction.add(R.id.container, fragment);
        // 最後にcommitを使用することで変更を反映します
        transaction.commit();
    }

}
