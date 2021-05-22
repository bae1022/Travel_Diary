package ddwu.mobile.finalproject.ma02_20180972.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ddwu.mobile.finalproject.ma02_20180972.R;
import ddwu.mobile.finalproject.ma02_20180972.database.TravelDBHelper;
import ddwu.mobile.finalproject.ma02_20180972.manager.ImageFileManager;
import ddwu.mobile.finalproject.ma02_20180972.service.Constants;
import ddwu.mobile.finalproject.ma02_20180972.service.FetchLatLngIntentService;

import androidx.core.content.FileProvider;

import com.google.android.gms.maps.model.LatLng;

public class AddActivity extends AppCompatActivity {

    final static String TAG = "AddActivity";

    private static final int REQUEST_GALLERY_PHOTO = 200;
    final static int PERMISSION_REQ_CODE = 100;

    private LatLngResultReceiver latLngResultReceiver;

    //추가 정보들
    ImageView img;
    EditText etDate;
    EditText etPlace;
    RatingBar etStar;
    EditText etMemo;
    float star_rating = 0;

    String x;
    String y;
    String title;

    //주소 확인 버튼
    Button btn;

    String confirm_address; // 등록받은 주소
    String a; //등록받지 못한 주소

    TravelDBHelper helper;

    private String mCurrentPhotoPath = null;
    private String photoPath;

    Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        checkPermission();

        helper = new TravelDBHelper(this);
        latLngResultReceiver = new LatLngResultReceiver(new Handler());

        btn = findViewById(R.id.confirm_address);

        etDate = findViewById(R.id.addact_date);
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddActivity.this, myDatePicker,
                        myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        etPlace = findViewById(R.id.addact_place);
        x = getIntent().getStringExtra("x");
        if (x != null){
            y = getIntent().getStringExtra("y");
            title = getIntent().getStringExtra("title");
            etPlace.setText(title);

            //이미 x, y 좌표 한 번에 가져오므로 확인받을 필요는 없음
            btn.setEnabled(false);
            etPlace.setClickable(false);
            etPlace.setFocusable(false);
        }

        etStar = findViewById(R.id.addact_rating);
        etStar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                star_rating = rating;
            }
        });

        etMemo = findViewById(R.id.addact_memo);

        img = findViewById(R.id.add_img);
        img.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    //갤러리 호출
                    galleryPictureIntent();
                    return true;
                }
                return false;
            }
        });


    }

    private void galleryPictureIntent(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");

        if (intent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;

            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(this,
                        "ddwu.mobile.finalproject.ma02_20180972",
                        photoFile);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, REQUEST_GALLERY_PHOTO);
            }
        }
    }

    /*현재 시간 정보를 사용하여 파일 정보 생성*/
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix  */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /*사진의 크기를 ImageView에서 표시할 수 있는 크기로 변경*/
    private void setPic() {
        // Get the dimensions of the View
        int targetW = img.getWidth();
        int targetH = img.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
//        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        img.setImageBitmap(bitmap);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GALLERY_PHOTO && resultCode == RESULT_OK) {

            photoPath = getRealPathFromURI(data.getData());

            FileInputStream fi = null;
            FileOutputStream fo = null;
            byte[] buffer;
            int c;

            try{
                fi = new FileInputStream(photoPath);
                fo = new FileOutputStream(mCurrentPhotoPath);

                buffer = new byte[1024];
                while((c=fi.read(buffer)) != -1){
                    fo.write(buffer, 0, c);
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally{
                try {
                    if (fi != null)
                        fi.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (fo != null)
                        fo.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            setPic();

        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        int column_index=0;

        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);

        if(cursor.moveToFirst()){
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }

        return cursor.getString(column_index);
    }

    DatePickerDialog.OnDateSetListener myDatePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };

    private void updateLabel() {
        String myFormat = "yyyy/MM/dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);

        etDate = findViewById(R.id.addact_date);
        etDate.setText(sdf.format(myCalendar.getTime()));
    }

    public void onClick(View v){
        SQLiteDatabase db = helper.getWritableDatabase();

        switch(v.getId()){
            case R.id.addact_btn:

                if (etDate.getText().toString().equals("")
                        || etPlace.getText().toString().equals("")){
                    Toast.makeText(this, "날짜와 장소명은 필수 입력사항입니다.", Toast.LENGTH_SHORT).show();
                }

                else if (btn.isEnabled() == true){
                    Toast.makeText(this, "장소를 확인받은 후 계속해서 이용해 주세요.", Toast.LENGTH_SHORT).show();
                }

                else {
                    ContentValues row = new ContentValues();

                    row.put(helper.COL_DATE, etDate.getText().toString());
                    row.put(helper.COL_PLACE, etPlace.getText().toString());
                    row.put(helper.COL_STAR, etStar.getRating());
                    row.put(helper.COL_MEMO, etMemo.getText().toString());
                    row.put(helper.COL_PATH, mCurrentPhotoPath);
                    row.put(helper.COL_X, x);
                    row.put(helper.COL_Y, y);

                    db.insert(helper.TABLE_NAME, null, row);

                    Toast.makeText(this, "여행 기록이 추가되었습니다.", Toast.LENGTH_SHORT).show();
                    helper.close();

                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                }
                break;

            case R.id.confirm_address:
                a = etPlace.getText().toString();

                    if (a.equals("")){
                        Toast.makeText(this, "장소를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Intent intent = new Intent(this, FetchLatLngIntentService.class);
                        intent.putExtra(Constants.RECEIVER, latLngResultReceiver);
                        intent.putExtra(Constants.ADDRESS_DATA_EXTRA, a);
                        startService(intent);
                    }
                    break;

            case R.id.addact_cancel:
                if (mCurrentPhotoPath != null){
                    File file = new File(mCurrentPhotoPath);
                    file.delete();
                }
                finish();
                break;
        }

    }

    class LatLngResultReceiver extends ResultReceiver {
        public LatLngResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            String lat = null;
            String lng = null;
            ArrayList<LatLng> latLngList = null;

            if (resultCode == Constants.SUCCESS_RESULT) {
                if (resultData == null) return;
                latLngList = (ArrayList<LatLng>) resultData.getSerializable(Constants.RESULT_DATA_KEY);
                if (latLngList == null) {
                    Toast.makeText(AddActivity.this, "주소를 찾지 못했습니다. 주소를 정확히 기입해주세요", Toast.LENGTH_SHORT).show();
                } else {
                    LatLng latlng = latLngList.get(0);
                    lat = String.valueOf(latlng.latitude);
                    lng = String.valueOf(latlng.longitude);
                }

                x = lng;
                y = lat;

                Toast.makeText(AddActivity.this, "주소가 확인되었습니다.", Toast.LENGTH_SHORT).show();

                confirm_address = a;

                etPlace.setClickable(false);
                etPlace.setFocusable(false);
                btn.setEnabled(false);

            } else {
              Toast.makeText(AddActivity.this, "주소를 찾지 못했습니다. 주소를 정확히 기입해주세요", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
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

            } else {
                Toast.makeText(this, "앱 실행을 위해 권한 허용이 필요함", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}