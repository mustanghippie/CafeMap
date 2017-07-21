package drgn.cafemap.Controller.Activity;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import drgn.cafemap.Controller.Fragment.DetailPageFragment;
import drgn.cafemap.Controller.Fragment.EditPageFragment;
import drgn.cafemap.Controller.Fragment.PreviewPageFragment;
import drgn.cafemap.R;
import drgn.cafemap.util.Cafe;

public class CafeActivity extends AppCompatActivity implements DetailPageFragment.DetailPageFragmentListener,
        EditPageFragment.EditPageFragmentListener, PreviewPageFragment.PreviewPageFragmentListener {

    private double lat;
    private double lon;
    private boolean ownerFlag;
    private boolean existingDataFlag;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cafe);

        this.lat = getIntent().getDoubleExtra("lat", 0);
        this.lon = getIntent().getDoubleExtra("lon", 0);
        this.existingDataFlag = getIntent().getBooleanExtra("existingDataFlag", false);
        this.ownerFlag = getIntent().getBooleanExtra("ownerFlag", false);

        // detail page fragment
        this.fragmentManager = getSupportFragmentManager();

        // existing data cafe, go to detail page
        if (existingDataFlag) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, DetailPageFragment.newInstance(lat, lon, ownerFlag))
                    .commit();
        } else {
            // add a new cafe data
            fragmentManager.beginTransaction()
                    .replace(R.id.container, EditPageFragment.newInstance(lat, lon, ownerFlag, existingDataFlag))
                    .addToBackStack(null)
                    .commit();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void goToEditPageEvent() {
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.container, EditPageFragment.newInstance(lat, lon, ownerFlag, existingDataFlag))
                .addToBackStack(null)
                .commit();

    }

    @Override
    public void goToPreviewPageEvent(Cafe cafe) {
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.container, PreviewPageFragment.newInstance(cafe, lat, lon))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void finishedPreviewPageEvent() {
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);

        intent.putExtra("defaultPosLat", lat);
        intent.putExtra("defaultPosLon", lon);
        startActivity(intent);
    }
}
