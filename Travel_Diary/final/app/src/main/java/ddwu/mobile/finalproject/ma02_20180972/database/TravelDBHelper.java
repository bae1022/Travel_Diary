package ddwu.mobile.finalproject.ma02_20180972.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TravelDBHelper extends SQLiteOpenHelper {

    private final static String DB_NAME = "travel_db";
    public final static String TABLE_NAME = "travel_table";
    public final static String COL_ID = "_id";
    public final static String COL_DATE = "date"; //여행 날짜
    public final static String COL_PLACE = "place"; //여행지
    public final static String COL_STAR = "star"; //별점
    public final static String COL_MEMO = "memo"; //메모
    public final static String COL_X = "x"; //x 좌표
    public final static String COL_Y = "y"; //y 좌표
    public final static String COL_PATH = "path"; //이미지 경로

    public TravelDBHelper(Context context) { super(context, DB_NAME, null, 1);}

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " ( " + COL_ID + " integer primary key autoincrement,"
                + COL_DATE + " TEXT, " + COL_PLACE + " TEXT, " + COL_X + " TEXT, " + COL_Y + " TEXT, " + COL_PATH + " TEXT, " + COL_STAR + " FLOAT, " + COL_MEMO + " TEXT);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table " + TABLE_NAME);
        onCreate(db);
    }
}
