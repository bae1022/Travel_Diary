package ddwu.mobile.finalproject.ma02_20180972.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

import ddwu.mobile.finalproject.ma02_20180972.R;
import ddwu.mobile.finalproject.ma02_20180972.adapter.CurrentRecommendAdapter;
import ddwu.mobile.finalproject.ma02_20180972.dto.RecommendDto;
import ddwu.mobile.finalproject.ma02_20180972.manager.TravelNetworkManager;
import ddwu.mobile.finalproject.ma02_20180972.service.Constants;
import ddwu.mobile.finalproject.ma02_20180972.service.FetchAddressIntentService;
import ddwu.mobile.finalproject.ma02_20180972.service.TravelXmlParser;

public class CurrentLocationRecommendActivity extends AppCompatActivity {

    private String TAG = "CurrentLocationRecommendActivity";

    String type;
    String apiAddress;

    ArrayList<RecommendDto> resultList;
    ListView lvList;
    CurrentRecommendAdapter adapter;

    TravelNetworkManager networkManager;

    TravelXmlParser parser;

    Button btn1;
    Button btn2;
    Button btn3; //여행지 추천받기

    double center_x;
    double center_y;

    int t; //type 코드

    private LocationManager locManager;
    private String bestProvider;
    private final static int MY_PERMISSIONS_REQ_LOC = 100;

    double current_x;
    double current_y;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;    /* 위치 수신 조건을 설정할 때 사용 */

    private boolean requestingLocationUpdates;  /* 위치 수신 중인지 확인 용도 */

    private AddressResultReceiver addressResultReceiver;

    String x;
    String y;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_location_recommend);

        checkPermission();

        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        bestProvider = LocationManager.GPS_PROVIDER;

        parser = new TravelXmlParser();

        btn1 = findViewById(R.id.locations_map); //지도로 보기 버튼
        btn1.setEnabled(false);

        btn2 = findViewById(R.id.receive_location);
        btn3 = findViewById(R.id.start_recommend);

        btn3.setVisibility(View.GONE);

        networkManager = new TravelNetworkManager(this);

        apiAddress = getResources().getString(R.string.api_url2);

        resultList = new ArrayList();

        lvList = findViewById(R.id.recommend_list);
        adapter = new CurrentRecommendAdapter(this, R.layout.recommend_item, resultList);
        lvList.setAdapter(adapter);

        addressResultReceiver = new AddressResultReceiver(new Handler());

        Spinner spinner_type = (Spinner)findViewById(R.id.spinner_type);
        spinner_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = (String) spinner_type.getItemAtPosition(position);

                if (type.equals("관광지")){
                    t = 12;
                }
                else if (type.equals("문화시설")){
                    t = 14;
                }
                else if (type.equals("축제공연행사")){
                    t = 15;
                }
                else if (type.equals("여행코스")){
                    t = 25;
                }
                else if (type.equals("레포츠")){
                    t = 28;
                }
                else if (type.equals("숙박")){
                    t = 32;
                }
                else if (type.equals("쇼핑")){
                    t = 38;
                }
                else if (type.equals("음식점")){
                    t = 39;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        /*위치 정보 탐색조건 코드를 별도의 메소드로 분리*/
        createLocationRequest();

        /*FusedLocationProviderClient - 기본 클래스인 LocationManager 의 역할을 수행*/
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        /*LocationCallback - 기본 클래스인 LocationListener 역할*/
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.d(TAG, "in location callback");
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
//                    textView.setText( String.format("Lat: %f  Lng: %f", location.getLatitude(), location.getLongitude()) );
                    Intent intent = new Intent(CurrentLocationRecommendActivity.this, FetchAddressIntentService.class);
                    center_y = location.getLatitude();
                    center_x = location.getLongitude();

                    intent.putExtra(Constants.RECEIVER, addressResultReceiver); //결과 수신할 Receiver 객체
                    intent.putExtra(Constants.LAT_DATA_EXTRA, center_x);
                    intent.putExtra(Constants.LNG_DATA_EXTRA, center_y);

                    startService(intent);
                }
            }
        };

        requestingLocationUpdates = false;
    }


    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.start_recommend:
                x = String.valueOf(center_x);
                y = String.valueOf(center_y);

                try{
                    new NetworkAsyncTask().execute(apiAddress
                            + "&contentTypeId=" + t
                            + "&mapX=" + x
                            + "&mapY=" + y
                            + "&radius=2000&listYN=Y&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&arrange=A&numOfRows=20&pageNo=1");

                    btn1.setEnabled(true);
                }
                catch (Exception e){
                    e.printStackTrace();
                }

                break;
            case R.id.locations_map:
                Intent intent = new Intent(this, CurrentLocationMapActivity.class);

                intent.putExtra("result", resultList);
                intent.putExtra("center_x", center_x);
                intent.putExtra("center_y", center_y);

                startActivity(intent);
                break;

            case R.id.btn_back:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;

            case R.id.receive_location:
                if (!requestingLocationUpdates) {
                    requestingLocationUpdates = true;
                    startLocationUpdates();

                    Toast.makeText(this, "위치가 수신되었습니다. 여행지를 추천받아주세요", Toast.LENGTH_SHORT).show();
                    btn3.setVisibility(View.VISIBLE);

                }

//                else{
//                        Toast.makeText(this, "위치가 수신되지 못했습니다", Toast.LENGTH_SHORT).show();
//                }

                break;

        }
    }

    class NetworkAsyncTask extends AsyncTask<String, Integer, String> {
        ProgressDialog progressDlg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDlg = ProgressDialog.show(CurrentLocationRecommendActivity.this, "Wait", "Downloading...");
        }

        @Override
        protected String doInBackground(String... strings) {
            String address = strings[0];
            String result = null;

            // networking
            result = networkManager.downloadContents(address);

            if (result == null) return "Error!";

            //parsing
            resultList = parser.parse(result);

            return result;

        }

        @Override
        protected void onPostExecute(String result) {
            adapter.setList(resultList);    // Adapter 에 결과 List 를 설정 후 notify
            progressDlg.dismiss(); //progress 사라짐
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (requestingLocationUpdates) {
            stopLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        if (checkPermission()) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    protected void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(50000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    /* 위도/경도 → 주소 변환 ResultReceiver */
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            String addressOutput = null;

            if (resultCode == Constants.SUCCESS_RESULT) {
                if (resultData == null) return;
                addressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
                if (addressOutput == null) addressOutput = "";
//                textView.setText(addressOutput);
            }
        }
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQ_LOC);
                return false;
            } else
                return true;
        }
        return false;
    }


    /*권한승인 요청에 대한 사용자의 응답 결과에 따른 수행*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case MY_PERMISSIONS_REQ_LOC:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    /*권한을 승인받았을 때 수행하여야 하는 동작 지정*/

                } else {
                    /*사용자에게 권한 제약에 따른 안내*/
                    Toast.makeText(this, "Permissions are not granted.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

}