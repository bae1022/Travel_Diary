package ddwu.mobile.finalproject.ma02_20180972.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

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

public class RecommendLocationsActivity extends AppCompatActivity {

    private GoogleMap mGoogleMap;
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
        setContentView(R.layout.activity_recommend_locations);

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

    private void mapLoad() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.locations_map); //map 객체 준비(MapFragment)
        mapFragment.getMapAsync(mapReadyCallBack); //map 정보 가져오기 (Callback 호출)
    }

    OnMapReadyCallback mapReadyCallBack = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mGoogleMap = googleMap;

            LatLng currentLng = new LatLng(35.95, 127.85); //한국 중심점
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLng, 7));

            for (int i = 0; i < result.size(); i++){
                LatLng location = new LatLng(list_y.get(i), list_x.get(i));

                MarkerOptions option = new MarkerOptions();

                option.position(location);
                option.title((String) list_title.get(i));
                option.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));

                marker = mGoogleMap.addMarker(option);

                markers.add(marker);
            }

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