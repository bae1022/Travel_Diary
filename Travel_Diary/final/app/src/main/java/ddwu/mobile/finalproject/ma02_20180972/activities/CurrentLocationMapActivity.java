package ddwu.mobile.finalproject.ma02_20180972.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import ddwu.mobile.finalproject.ma02_20180972.R;
import ddwu.mobile.finalproject.ma02_20180972.dto.RecommendDto;

public class CurrentLocationMapActivity extends AppCompatActivity {

    public String TAG = "CurrentLocationMapActivity";

    final static int PERMISSION_REQ_CODE = 100;

    private GoogleMap mGoogleMap;
    private Marker centerMarker;
    private LocationManager locationManager;

    double x;
    double y;
    String title;

    double center_x;
    double center_y;

    private MarkerOptions markerOptions;
    private Marker marker;
    private ArrayList<Marker> markers = new ArrayList<Marker>();

    ArrayList<Double> list_x = new ArrayList<Double>();
    ArrayList<Double> list_y = new ArrayList<Double>();
    ArrayList<String> list_title = new ArrayList<String>();

    ArrayList<RecommendDto> result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_location_map);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        center_x = getIntent().getDoubleExtra("center_x", 0);
        center_y = getIntent().getDoubleExtra("center_y", 0);

        Intent intent = getIntent();
        result = (ArrayList<RecommendDto>) intent.getSerializableExtra("result");

        for (int i = 0; i < result.size(); i++){
            double x = Double.valueOf(result.get(i).getX());
            double y = Double.valueOf(result.get(i).getY());
            String t = result.get(i).getTitle();

            list_x.add(x);
            list_y.add(y);
            list_title.add(t);
        }

        mapLoad();
    }

    /*구글맵을 멤버변수로 로딩*/
    private void mapLoad() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.locations_map); //map 객체 준비(MapFragment)
        mapFragment.getMapAsync(mapReadyCallBack); //map 정보 가져오기 (Callback 호출)
    }

    OnMapReadyCallback mapReadyCallBack = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mGoogleMap = googleMap;

            LatLng currentLng = new LatLng(center_y, center_x);
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLng, 14));

            for (int i = 0; i < result.size(); i++){
                LatLng location = new LatLng(list_y.get(i), list_x.get(i));

                MarkerOptions option = new MarkerOptions();

                option.position(location);
                option.title((String) list_title.get(i));
                option.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

                marker = mGoogleMap.addMarker(option);

                markers.add(marker);
            }

            MarkerOptions option = new MarkerOptions();
            option.position(currentLng);
            option.title("현재위치");
            option.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

            centerMarker = mGoogleMap.addMarker(option);
            centerMarker.showInfoWindow();

        }
    };


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.locations_btn_back:
               finish();
                break;
        }
    }
    }