package ddwu.mobile.finalproject.ma02_20180972.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.OnMapReadyCallback;

import ddwu.mobile.finalproject.ma02_20180972.R;

public class LocationActivity extends AppCompatActivity {
    final static String TAG = "LocationActivity";

    private GoogleMap mGoogleMap;
    private Marker centerMarker;
    private LocationManager locationManager;

    double x;
    double y;
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        x = getIntent().getDoubleExtra("x", 0);
        y = getIntent().getDoubleExtra("y", 0);
        title = getIntent().getStringExtra("title");

        Log.d(TAG, "X는" + x);
        Log.d(TAG, "y는" + y);
        Log.d(TAG, "title는" + title);

        mapLoad();
    }

    private void mapLoad() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.location_map); //map 객체 준비(MapFragment)
        mapFragment.getMapAsync(mapReadyCallBack); //map 정보 가져오기 (Callback 호출)
    }

    OnMapReadyCallback mapReadyCallBack = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {

            mGoogleMap = googleMap;

            LatLng location = new LatLng(y, x);
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 17));

            MarkerOptions option = new MarkerOptions();
            option.position(location);
            option.title(title);
            option.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

            centerMarker = mGoogleMap.addMarker(option);
            centerMarker.showInfoWindow();

        }
    };


        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_back:
                    finish();
                    break;
            }
        }
}