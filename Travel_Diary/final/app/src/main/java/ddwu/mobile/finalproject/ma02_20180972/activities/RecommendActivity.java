package ddwu.mobile.finalproject.ma02_20180972.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import ddwu.mobile.finalproject.ma02_20180972.R;
import ddwu.mobile.finalproject.ma02_20180972.adapter.RecommendAdapter;
import ddwu.mobile.finalproject.ma02_20180972.dto.RecommendDto;
import ddwu.mobile.finalproject.ma02_20180972.manager.ImageFileManager;
import ddwu.mobile.finalproject.ma02_20180972.manager.TravelNetworkManager;
import ddwu.mobile.finalproject.ma02_20180972.service.TravelXmlParser;

public class RecommendActivity extends AppCompatActivity {

    private final static String TAG = "RecommendActivity";
    String type;
    String area;
    String apiAddress;

    ArrayList<RecommendDto> resultList;
    ListView lvList;
    RecommendAdapter adapter;

    TravelNetworkManager networkManager;
    ImageFileManager imgFileManager;
    TravelXmlParser parser;

    Button btn;

    double center_x;
    double center_y;

    int t; //type 코드
    int a; //area 코드

    private LocationManager locManager;
    private String bestProvider;
    private final static int MY_PERMISSIONS_REQ_LOC = 100;

    double current_x;
    double current_y;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);

        parser = new TravelXmlParser();

        btn = findViewById(R.id.locations_map); //지도로 보기 버튼
        btn.setEnabled(false);

        networkManager = new TravelNetworkManager(this);

        apiAddress = getResources().getString(R.string.api_url);

        resultList = new ArrayList();

        lvList = findViewById(R.id.recommend_list);
        adapter = new RecommendAdapter(this, R.layout.recommend_item, resultList);
        lvList.setAdapter(adapter);

        Spinner spinner_type = (Spinner)findViewById(R.id.spinner_type);
        Spinner spinner_area = (Spinner)findViewById(R.id.spinner_area);

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

        //area spinner
        spinner_area.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                area = (String) spinner_area.getItemAtPosition(position);

                Log.d(TAG, "지역은 " + area);

                if (area.equals("서울")){
                    a = 1;
                }
                else if (area.equals("인천")){
                    a = 2;
                }
                else if (area.equals("대전")){
                    a = 3;
                }
                else if (area.equals("대구")){
                    a = 4;
                }
                else if (area.equals("광주")){
                    a = 5;
                }
                else if (area.equals("부산")){
                    a = 6;
                }
                else if (area.equals("울산")){
                    a = 7;
                }
                else if (area.equals("세종특별자치시")){
                    a = 8;
                }
                else if (area.equals("경기도")){
                    a = 31;
                }
                else if (area.equals("강원도")){
                    a = 32;
                }
                else if (area.equals("충청북도")){
                    a = 33;
                }
                else if (area.equals("충청남도")){
                    a = 34;
                }
                else if (area.equals("경상북도")){
                    a = 35;
                }
                else if (area.equals("경상남도")){
                    a = 36;
                }
                else if (area.equals("전라북도")){
                    a = 37;
                }
                else if (area.equals("전라남도")){
                    a = 38;
                }
                else if (area.equals("제주도")){
                    a = 39;
               }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        bestProvider = LocationManager.GPS_PROVIDER;
    }

    //메뉴
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_location, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        //현재 위치 받아서 전송
        switch (item.getItemId()) {
            case R.id.cl_recommend:
                Intent intent = new Intent(RecommendActivity.this, CurrentLocationRecommendActivity.class);
                startActivity(intent);

                break;
        }
        return true;
    }

    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.start_recommend:
                try{
                    new NetworkAsyncTask().execute(apiAddress
                    + "&contentTypeId=" + t
                    + "&areaCode=" + a
                    + "&sigunguCode=&cat1=&cat2=&cat3=&listYN=Y&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&arrange=B&numOfRows=30&pageNo=1");

                    btn.setEnabled(true);
                }
                catch (Exception e){
                    e.printStackTrace();
                }

                break;
            case R.id.locations_map:
                Intent intent = new Intent(this, RecommendLocationsActivity.class);
                intent.putExtra("result", resultList);

                startActivity(intent);
                break;

            case R.id.btn_back:
                finish();
                break;
        }
    }

    class NetworkAsyncTask extends AsyncTask<String, Integer, String> {
        ProgressDialog progressDlg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDlg = ProgressDialog.show(RecommendActivity.this, "Wait", "Downloading...");
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

}