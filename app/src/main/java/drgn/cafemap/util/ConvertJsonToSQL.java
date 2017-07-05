package drgn.cafemap.util;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.gson.JsonObject;

import drgn.cafemap.Model.UserCafeMapModel;
import drgn.cafemap.R;


/**
 * @Todo delete
 */
public class ConvertJsonToSQL extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convert_json_to_sql);

        UserCafeMapModel userCafeMapModel = new UserCafeMapModel(getApplicationContext());
//        JsonObject jsonObject = userCafeMapModel.getUserCafeMapJson();

//        System.out.println(jsonObject);

    }
}
