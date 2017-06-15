package drgn.cafemap.Controller;

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
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.FileNotFoundException;
import java.io.InputStream;

import drgn.cafemap.Model.AsyncTaskMarkerSet;
import drgn.cafemap.R;
import drgn.cafemap.Model.UserCafeMapModel;

import static com.google.android.gms.location.LocationServices.FusedLocationApi;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleMap.OnMyLocationButtonClickListener, LocationSource {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest locationRequest;
    public static AsyncTaskMarkerSet atms;
    private Marker currentMarker = null;

    private OnLocationChangedListener onLocationChangedListener = null;

    private int priority[] = {LocationRequest.PRIORITY_HIGH_ACCURACY, LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY,
            LocationRequest.PRIORITY_LOW_POWER, LocationRequest.PRIORITY_NO_POWER};
    private int locationPriority;
    private double defaultPosLat;
    private double defaultPosLon;
    // don't set a marker during opening cafe info
    private boolean disableClickEvent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // get default location after user cafe info set
        this.defaultPosLat = getIntent().getDoubleExtra("defaultPosLat", 0.0);
        this.defaultPosLon = getIntent().getDoubleExtra("defaultPosLon", 0.0);

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
//        getApplicationContext().deleteFile("UserCafeMap.json");
//        getApplicationContext().deleteFile("UserCafeMapKey.json");
        // check permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mMap = googleMap;
            // default の LocationSource から自前のsourceに変更する
            mMap.setLocationSource(this);
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);

            // Reads markers from firebase and set markers up
            // And also prepare images of cafes
            this.atms = new AsyncTaskMarkerSet(mMap);
            this.atms.execute("");
            // Reads markers from UserMapCafe.json and set markers up
            UserCafeMapModel ucmm = new UserCafeMapModel(getApplicationContext());
            ucmm.setUserCafeMapMarkers(mMap);

            // マーカータップ時、情報ウィンドウを開く
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    disableClickEvent = true;

                    if (marker.getTitle().equals("Add your cafe")){
                        View view = getLayoutInflater().inflate(R.layout.info_window_new, null);
                        String title = marker.getTitle();
                        System.out.println(title);
                        TextView titleUi = (TextView) view.findViewById(R.id.title);
                        titleUi.setText(title);
                        return view;
                    }

                    // Set view
                    View view = getLayoutInflater().inflate(R.layout.info_window, null);

                    // set up image
                    ImageView img = (ImageView) view.findViewById(R.id.badge);
                    //String imgName = marker.getTitle().replaceAll(" ", "_").toLowerCase() + ".png";
                    Bitmap image = atms.getCafeBitmapMap().get(marker.getTitle().replaceAll(" ", "_").toLowerCase()); // from owner data
                    if (image == null) {
                        // in user marker case
                        String imageName = String.valueOf(marker.getPosition().latitude) + String.valueOf(marker.getPosition().longitude);
                        try {
                            InputStream in = getApplicationContext().openFileInput(imageName + ".png"); // from local data
                            image = BitmapFactory.decodeStream(in);
                        } catch (FileNotFoundException e) {
                            System.out.println("FileNotFound@MapsActivity " + imageName + ".png");
                        }

                    }
                    img.setImageBitmap(image);

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
                        String[] snippetArray = snippet.split("\n");

                        SpannableString time = new SpannableString(snippetArray[0]);
                        SpannableString wifi = new SpannableString(" Wi-fi ");
                        SpannableString socket = new SpannableString(" Socket ");
                        SpannableStringBuilder snippetSpannable = new SpannableStringBuilder();

                        // Text color
                        wifi.setSpan(new ForegroundColorSpan(Color.WHITE), 0, wifi.length(), 0);
                        socket.setSpan(new ForegroundColorSpan(Color.WHITE), 0, socket.length(), 0);

                        if (snippetArray[1].equals("Wi-fi: Available")) {
                            wifi.setSpan(new BackgroundColorSpan(0xFFF5CC5B), 0, wifi.length(), 0);
                        } else {
                            wifi.setSpan(new BackgroundColorSpan(0xFFD3D3D3), 0, wifi.length(), 0);
                        }

                        if (snippetArray[2].equals("Socket: Available ")) {
                            socket.setSpan(new BackgroundColorSpan(0xFFF5CC5B), 0, socket.length(), 0);
                        } else {
                            socket.setSpan(new BackgroundColorSpan(0xFFD3D3D3), 0, socket.length(), 0);
                        }

                        snippetSpannable.append(time + "\n");
                        snippetSpannable.append(wifi);
                        snippetSpannable.append(" ");
                        snippetSpannable.append(socket);

                        snippetUi.setText(snippetSpannable);

                    }

                    return view;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    return null;
                }
            });

            // 情報ウィンドウクリック時のアクション
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    //Log.d("Click Info Window", "Click Info Window");
                    Intent intent = new Intent(getApplication(), DetailPageActivity.class);
                    LatLng latlng = marker.getPosition();

                    // viewMode: 0 => make a new data, 1 => display cafe info 2 => preview
                    int viewMode = 1;
                    if (marker.getTitle().equals("Add your cafe")) viewMode = 0;

                    intent.putExtra("lat", latlng.latitude);
                    intent.putExtra("lon", latlng.longitude);
                    intent.putExtra("viewMode", viewMode);

                    startActivity(intent);
                }
            });

            // マーカー追加処理
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng point) {
                    if (!disableClickEvent) {
                        // タップした位置の表示
                        //Toast.makeText(getApplicationContext(), "Latitude：" + point.latitude + "\nLongitude:" + point.longitude, Toast.LENGTH_SHORT).show();
                        //Log.d("Location ", "Latitude + " + point.latitude + " Longitude + " + point.longitude);
                        // A marker can exist only one
                        if (currentMarker != null) currentMarker.remove();
                        // マーカーを追加
                        LatLng latLng = new LatLng(point.latitude, point.longitude);
                        currentMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Add your cafe"));
                        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(17).build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    } else {
                        disableClickEvent = false;
                    }
                }
            });


            if (defaultPosLat != 0.0 && defaultPosLon != 0.0) { // From detail page after set user map cafe
                LatLng position = new LatLng(defaultPosLat, defaultPosLon);

                // move camera
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 17));
            } else {
                //LocationManagerの取得(初回のマップ移動)
                LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                // get position from gps
                Location myLocate = locationManager.getLastKnownLocation("gps");
                if (myLocate != null) {
                    LatLng currentLocation = new LatLng(myLocate.getLatitude(), myLocate.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 17));
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to connect gps", Toast.LENGTH_LONG).show();

                    // サンプル用初期位置
                    LatLng position = new LatLng(49.285131, -123.112998);

//                    MarkerOptions options = new MarkerOptions();
//                    options.title("You are here");
//                    options.position(position);
//
//                    mMap.addMarker(options);

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 17));
                }
            }

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

    @Override
    public void onBackPressed() {
    }
}