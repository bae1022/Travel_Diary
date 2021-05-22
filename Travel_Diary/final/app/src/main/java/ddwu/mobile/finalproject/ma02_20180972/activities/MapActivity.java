package ddwu.mobile.finalproject.ma02_20180972.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.lang.reflect.Array;
import java.util.ArrayList;

import ddwu.mobile.finalproject.ma02_20180972.R;
import ddwu.mobile.finalproject.ma02_20180972.database.TravelDBHelper;

public class MapActivity extends AppCompatActivity{

    final static String TAG = "MainActivity";
    final static int PERMISSION_REQ_CODE = 100;

    TravelDBHelper helper;
    Cursor cursor;

    private GoogleMap mGoogleMap;
    private MarkerOptions markerOptions;
    private Marker marker;
    private ArrayList<Marker> markers = new ArrayList<Marker>();

    ArrayList<Double> list_x = new ArrayList<Double>();
    ArrayList<Double> list_y = new ArrayList<Double>();
    ArrayList<String> list_title = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mapLoad();

        helper = new TravelDBHelper(this);

        String[] columns = {helper.COL_PLACE, helper.COL_X, helper.COL_Y};

        SQLiteDatabase db = helper.getReadableDatabase();

        cursor = db.query(helper.TABLE_NAME, columns, null, null, null, null, null, null);

        while(cursor.moveToNext()) {
            String t = cursor.getString(cursor.getColumnIndex(helper.COL_PLACE));
            String sx = cursor.getString(cursor.getColumnIndex(helper.COL_X));
            String sy = cursor.getString(cursor.getColumnIndex(helper.COL_Y));

            if (sx == null || sy == null){

            }

            else{
                double x = Double.valueOf(sx);
                double y = Double.valueOf(sy);

                list_x.add(x);
                list_y.add(y);
                list_title.add(t);
            }

        }

        cursor.close();

    }

    private void mapLoad() {

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map); //map 객체 준비(MapFragment)
        mapFragment.getMapAsync(mapReadyCallBack); //map 정보 가져오기 (Callback 호출)
    }

    OnMapReadyCallback mapReadyCallBack = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mGoogleMap = googleMap;

            LatLng currentLng = new LatLng(35.95, 127.85); //한국 중심점
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLng, 7));

            for (int i = 0; i < list_x.size(); i++){
                LatLng location = new LatLng(list_y.get(i), list_x.get(i));

                MarkerOptions option = new MarkerOptions();

                option.position(location);
                option.title((String) list_title.get(i));
                option.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

                marker = mGoogleMap.addMarker(option);

                markers.add(marker);
            }

        }
    };

    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.map_btn_back:

                finish();
                break;
        }
    }


                /* 필요 permission 요청 */
    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_REQ_CODE);
                return false;
            }
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_REQ_CODE) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mapLoad();
            } else {
                Toast.makeText(this, "앱 실행을 위해 권한 허용이 필요함", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}