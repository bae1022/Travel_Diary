package ddwu.mobile.finalproject.ma02_20180972.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import ddwu.mobile.finalproject.ma02_20180972.R;
import ddwu.mobile.finalproject.ma02_20180972.adapter.TravelCursorAdapter;
import ddwu.mobile.finalproject.ma02_20180972.database.TravelDBHelper;

public class MainActivity extends AppCompatActivity {

    String TAG = "MainActivity";
    ListView list = null;
    TravelDBHelper helper;
    Cursor cursor;

    TravelCursorAdapter adapter;
    final int UPDATE_CODE = 100;
    boolean result = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = (ListView) findViewById(R.id.mainact_list);
        helper = new TravelDBHelper(this);
        adapter = new TravelCursorAdapter(this, R.layout.diary_item, null);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String _id = String.valueOf(l);

                Intent intent = new Intent(MainActivity.this, UpdateActivity.class);
                intent.putExtra("id", _id);

                startActivityForResult(intent, UPDATE_CODE);
            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final String _id = String.valueOf(l);

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("삭제")
                        .setMessage("기록을 삭제하시겠습니까?")
                        .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                SQLiteDatabase sdb = helper.getWritableDatabase();
                                String whereClause = helper.COL_ID + "=?";
                                String[] whereArgs = new String[]{_id};

                                sdb.delete(helper.TABLE_NAME, whereClause, whereArgs);

                                SQLiteDatabase db = helper.getReadableDatabase();
                                cursor = db.rawQuery("select * from " + helper.TABLE_NAME, null);

                                adapter.changeCursor(cursor);
                                helper.close();
                            }
                        })
                        .setNegativeButton("취소", null)
                        .setCancelable(false)
                        .show();
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (result == false) { }

        else{
            SQLiteDatabase db = helper.getReadableDatabase();
            cursor = db.rawQuery("select * from " + helper.TABLE_NAME, null);

            adapter.changeCursor(cursor);

            helper.close();
        }
        result = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cursor != null) cursor.close();
    }

    public void onClick(View v) {
        Intent intent = null;

        switch (v.getId()) {
            case R.id.memo_travel: //여행 기록
                intent = new Intent(this, AddActivity.class);
                break;

            case R.id.view_map: //지도로 보기
                intent = new Intent(this, MapActivity.class);
                break;

            case R.id.recommend_travel: //여행지 추천받기
                intent = new Intent(this, RecommendActivity.class);
        }
        if (intent != null)
            startActivity(intent);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPDATE_CODE) {
            switch (resultCode) {
                case RESULT_OK:
                    result = true;
                    break;

                case RESULT_CANCELED:
                    result = false;
                    break;
            }
        }
    }
}