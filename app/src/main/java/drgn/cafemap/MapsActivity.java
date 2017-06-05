package drgn.cafemap;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import java.io.IOException;
import java.io.InputStream;

import static com.google.android.gms.location.LocationServices.FusedLocationApi;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleMap.OnMyLocationButtonClickListener, LocationSource {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest locationRequest;

    private OnLocationChangedListener onLocationChangedListener = null;

    private int priority[] = {LocationRequest.PRIORITY_HIGH_ACCURACY, LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY,
            LocationRequest.PRIORITY_LOW_POWER, LocationRequest.PRIORITY_NO_POWER};
    private int locationPriority;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // LocationRequest を生成して精度、インターバルを設定
        locationRequest = LocationRequest.create();

        // 測位の精度、消費電力の優先度
        locationPriority = priority[1];

        if (locationPriority == priority[0]) {
            // 位置情報の精度を優先する場合
            locationRequest.setPriority(locationPriority);
            locationRequest.setInterval(5000);
            locationRequest.setFastestInterval(16);
        } else if (locationPriority == priority[1]) {
            // 消費電力を考慮する場合
            locationRequest.setPriority(locationPriority);
            locationRequest.setInterval(60000);
            locationRequest.setFastestInterval(16);
        } else if (locationPriority == priority[2]) {
            // "city" level accuracy
            locationRequest.setPriority(locationPriority);
        } else {
            // 外部からのトリガーでの測位のみ
            locationRequest.setPriority(locationPriority);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


    }

    // onResumeフェーズに入ったら接続
    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    // onPauseで切断
    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        // check permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mMap = googleMap;
            // default の LocationSource から自前のsourceに変更する
            mMap.setLocationSource(this);
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);

            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {

                    View view = getLayoutInflater().inflate(R.layout.info_window, null);

                    // 画像設定
                    ImageView img = (ImageView) view.findViewById(R.id.badge);
                    //img.setImageResource(R.drawable.home);
                    String imgName = marker.getTitle().replaceAll(" ", "_").toLowerCase() + ".png";
                    try {
                        InputStream istream = getResources().getAssets().open(imgName);
                        Bitmap bitmap = BitmapFactory.decodeStream(istream);
                        img.setImageBitmap(bitmap);
                        //imageView3.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        Log.d("Assets", "Error");
                    }

                    // タイトル設定
                    String title = marker.getTitle();
                    TextView titleUi = (TextView) view.findViewById(R.id.title);
                    // Spannable string allows us to edit the formatting of the text.
                    SpannableString titleText = new SpannableString(title);
                    titleText.setSpan(new ForegroundColorSpan(Color.BLACK), 0, titleText.length(), 0);
                    titleUi.setText(titleText);

                    String snippet = marker.getSnippet();
                    TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
                    if (snippet != null) {
                        SpannableString snippetText = new SpannableString(snippet);
                        if (snippet.equals("Wi-fi: Good"))
                            snippetText.setSpan(new ForegroundColorSpan(Color.YELLOW), 7, 11, 0);
                        if (snippet.equals("Wi-fi: Bad"))
                            snippetText.setSpan(new ForegroundColorSpan(Color.BLUE), 7, 10, 0);
                        snippetUi.setText(snippetText);

                    }

                    return view;
                }

                @Override
                public View getInfoContents(Marker marker) {

                    return null;
                }
            });

            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    Log.d("Click Info Window", "Click Info Window");
                    Intent intent = new Intent(getApplication(), EditPageActivity.class);
                    startActivity(intent);
                }
            });

            // マーカー追加処理
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng point) {

                    // タップした位置の表示
                    Toast.makeText(getApplicationContext(), "タップ位置\n緯度：" + point.latitude + "\n経度:" + point.longitude, Toast.LENGTH_SHORT).show();
                    Log.d("MarkerClick", String.valueOf(point.latitude) + " " + String.valueOf(point.longitude));
                    // マーカーを追加
                    LatLng latLng = new LatLng(point.latitude, point.longitude);
                    mMap.addMarker(new MarkerOptions().position(latLng).title("Make a new location"));
                }
            });


            //LocationManagerの取得(初回のマップ移動)
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            //GPSから現在地の情報を取得
            Location myLocate = locationManager.getLastKnownLocation("gps");
            if (myLocate != null) {
                LatLng currentLocation = new LatLng(myLocate.getLatitude(), myLocate.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 17));
            } else {
                Toast.makeText(getApplicationContext(), "GPS取得に失敗", Toast.LENGTH_LONG).show();

                // サンプル用初期位置
                LatLng home = new LatLng(49.238323199999996, -123.0418275);
                // カメラ移動
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home, 17));

            }

            // Reads markers from firebase and set markers up
            FirebaseConnectionModel fc = new FirebaseConnectionModel(mMap);
            fc.setMarkersOnGoogleMap();

            //System.out.println("Marker Info = " +fc.getMarker());

        } else {
            Log.d("debug", "permission error");
            return;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        // 現在地へ
        Log.d("debug", "onLocationChanged");
        if (onLocationChangedListener != null) {
            // GPSのマークを表示する
            onLocationChangedListener.onLocationChanged(location);

            double lat = location.getLatitude();
            double lng = location.getLongitude();

            Log.d("debug", "location=" + lat + "," + lng);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("Debug", "onConnected");
        // check permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            //FusedLocationApi
            FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, createLocationRequest(), this);
        } else {
            Log.d("debug", "permission error");
            return;
        }
    }

    private LocationRequest createLocationRequest() {
        Log.d("Debug", "createLocationRequest");
        return new LocationRequest()
                .setInterval(1000)
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }


    @Override
    public void onConnectionSuspended(int i) {
        Log.d("debug", "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("debug", "onConnectionFailed");
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "onMyLocationButtonClick", Toast.LENGTH_SHORT).show();

        return false;
    }

    // OnLocationChangedListener calls activate() method
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        this.onLocationChangedListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {
        this.onLocationChangedListener = null;
    }
}