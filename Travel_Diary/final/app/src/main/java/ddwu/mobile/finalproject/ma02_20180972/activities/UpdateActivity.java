package ddwu.mobile.finalproject.ma02_20180972.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ddwu.mobile.finalproject.ma02_20180972.R;
import ddwu.mobile.finalproject.ma02_20180972.database.TravelDBHelper;
import ddwu.mobile.finalproject.ma02_20180972.service.Constants;
import ddwu.mobile.finalproject.ma02_20180972.service.FetchLatLngIntentService;

public class UpdateActivity extends AppCompatActivity {

    final static String TAG = "UpdateActivity";

    private static final int REQUEST_GALLERY_PHOTO = 200;
    Context context;

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

    TravelDBHelper helper;

    private String backPhotoPath = null;
    private String mCurrentPhotoPath = null;
    private String photoPath;

    Cursor cursor;
    String id;

    Calendar myCalendar = Calendar.getInstance();

    String u_date = null;
    float u_star = 0;
    String u_memo;

    int state = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        context = this;

        helper = new TravelDBHelper(this);

        btn = findViewById(R.id.update_confirm_address);

        etDate = findViewById(R.id.update_date);
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(UpdateActivity.this, myDatePicker,
                        myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        etPlace = findViewById(R.id.update_place);

        etStar = findViewById(R.id.update_rating);
        etStar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

            }
        });

        etMemo = findViewById(R.id.update_memo);

        img = findViewById(R.id.update_img);
        img.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                u_date = etDate.getText().toString();
                u_memo = etMemo.getText().toString();
                u_star = etStar.getRating();

                state ++;

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    //갤러리 호출
                    galleryPictureIntent();
                    return true;
                }
                return false;
            }
        });

        //사진 경로 구하기 위함
        id = getIntent().getStringExtra("id");
        String s = null;

        String[] columns = {helper.COL_PATH};
        String selection = "_id=?";
        String[] selectArgs = new String[]{id};

        SQLiteDatabase db = helper.getWritableDatabase();

        cursor = db.query(helper.TABLE_NAME, columns, selection, selectArgs, null, null, null, null);
        while (cursor.moveToNext()) {
            mCurrentPhotoPath = cursor.getString(cursor.getColumnIndexOrThrow(helper.COL_PATH));
        }

        btn.setEnabled(false);

        etPlace.setClickable(false);
        etPlace.setFocusable(false);
    }

    @Override
    protected void onResume() {
        super.onResume();

        id = getIntent().getStringExtra("id");
        String s = null;

        String[] columns = {helper.COL_ID, helper.COL_DATE, helper.COL_PATH, helper.COL_STAR, helper.COL_PLACE, helper.COL_MEMO};
        String selection = "_id=?";
        String[] selectArgs = new String[]{id};

        SQLiteDatabase db = helper.getWritableDatabase();

        cursor = db.query(helper.TABLE_NAME, columns, selection, selectArgs, null, null, null, null);

        while (cursor.moveToNext()) {
            etDate.setText(cursor.getString(cursor.getColumnIndex(helper.COL_DATE)));
            etPlace.setText(cursor.getString(cursor.getColumnIndex(helper.COL_PLACE)));
            etMemo.setText(cursor.getString(cursor.getColumnIndex(helper.COL_MEMO)));
            etStar.setRating(cursor.getFloat(cursor.getColumnIndex(helper.COL_STAR)));

            star_rating = (cursor.getFloat(cursor.getColumnIndex(helper.COL_STAR)));

            backPhotoPath = cursor.getString(cursor.getColumnIndexOrThrow(helper.COL_PATH));
        }

        cursor.close();

    }

    //공유 메뉴
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        //인스타그램 사진 공유
       switch (item.getItemId()) {
            case R.id.share:

                if (backPhotoPath == null && mCurrentPhotoPath == null){
                    Toast.makeText(UpdateActivity.this, "공유할 사진이 없습니다. 사진을 먼저 등록해주세요!", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("image/*");

                    File file = new File(mCurrentPhotoPath);
                    Uri uri = FileProvider.getUriForFile(UpdateActivity.this, "ddwu.mobile.finalproject.ma02_20180972", file);
                    Log.d(TAG, "Uri는 " + uri.toString());
                try {
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                    intent.setPackage("com.instagram.android");
                    startActivity(intent);
                }
                catch(ActivityNotFoundException e){
                    Toast.makeText(UpdateActivity.this, "Instagram 설치 후 계속해서 진행해주세요.", Toast.LENGTH_SHORT).show();
                }
                }

                break;
        }
        return true;
    }



    //사진 호출
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        Log.d(TAG, "옛날 경로 " + backPhotoPath);
        Log.d(TAG, "지금 경로 " + mCurrentPhotoPath);
        Log.d(TAG, "PHOTOPATH는 " + photoPath);

      if (backPhotoPath == null){ //기존 사진 경로가 존재하지 않았을 경우임
          if (mCurrentPhotoPath != null && photoPath != null){ //현재 사진을 선택했음
              view_pic(photoPath);
          }
          else if (mCurrentPhotoPath != null && photoPath == null) { //갤러리 들어갔다가 사진 선택 안 하고 그냥 나옴
              img.setImageResource(R.mipmap.ic_launcher);
          }
          else{
              img.setImageResource(R.mipmap.ic_launcher);
          }
      }

      else{ //기존에 사진이 이미 존재했음
          if (mCurrentPhotoPath.equals(backPhotoPath)){
              setPic();
          }
          else if (!mCurrentPhotoPath.equals(backPhotoPath) && photoPath == null){
              //사진이 기존에 있는 상태에서 갤러리에 들어갔지만 아무것도 선택 안 하고 나옴
              view_pic(backPhotoPath);
          }
          else{
              view_pic(photoPath);
          }
      }

      if (state != 0) {
          if (u_date != null || u_memo != null || u_star != 0) {
              etDate.setText(u_date);
              etMemo.setText(u_memo);
              etStar.setRating(u_star);

              Log.d(TAG, "u_star" + u_star);

          }
      }
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

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void setPic() {
        int targetW = img.getWidth();
        int targetH = img.getHeight();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        img.setImageBitmap(bitmap);
    }

    private void view_pic(String s){
        int targetW = img.getWidth();
        int targetH = img.getHeight();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(s, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(s, bmOptions);
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

        etDate.setText(sdf.format(myCalendar.getTime()));
        u_date = sdf.format(myCalendar.getTime());

    }

    public void onClick(View v){
        Intent resultIntent = new Intent();
        SQLiteDatabase db = helper.getWritableDatabase();

        switch(v.getId()){
            case R.id.update_btn:

                if (etDate.getText().toString().equals("")
                        || etPlace.getText().toString().equals("")){
                    Toast.makeText(this, "날짜와 장소명은 필수 입력사항입니다.", Toast.LENGTH_SHORT).show();
                }

                else {
                    String whereClause = "_id=?";
                    String[] whereArgs = new String[]{id};

                    ContentValues row = new ContentValues();

                    row.put(helper.COL_DATE, etDate.getText().toString());
                    row.put(helper.COL_STAR, etStar.getRating());
                    row.put(helper.COL_MEMO, etMemo.getText().toString());
                    row.put(helper.COL_PATH, mCurrentPhotoPath);

                    db.update(helper.TABLE_NAME, row, whereClause, whereArgs);

                    Toast.makeText(this, "여행 기록이 수정되었습니다.", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK, resultIntent);
                    helper.close();
                    finish();
                }
                break;

            case R.id.update_cancel:

                finish();
                break;
        }

    }

    public void onBackPressed() {
        super.onBackPressed();

        mCurrentPhotoPath = backPhotoPath;

        Log.d(TAG, "BACK 누른 후 CURRENT" + mCurrentPhotoPath);
        Log.d(TAG, "BACKPHOTOPATH" + backPhotoPath);
    }

}