package com.poxo.inventorymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.HashMap;

public class XlsxCon {
    String TAG = "DBAdapter";

    public static final String Inventory = "Inventory";
    public static final String id = "_id";// 0 integer
    public static final String ProductName = "ProductName";// 1 text(String)
    public static final String ProductNo = "ProductNo";// 2 integer
    public static final String Found = "Found";// 3 date(String)
    public static final String AssetNo = "AssetNo";// 1 integer

    public static final String MajorCat = "MajorCat";// 1 integer
    public static final String MinorCat = "MinorCat";// 1 integer
    public static final String AssetName = "AssetName";// 1 integer
    public static final String TagState = "TagState";// 1 integer

    public static final String Type = "Type";// 1 integer
    public static final String Remarks = "Remarks";// 1 integer
    public static final String AssetCap = "AssetCap";// 1 integer
    public static final String Quant = "Quant";// 1 integer
    public static final String OriCost = "OriCost";// 1 integer
    public static final String CurCost = "CurCost";// 1 integer
    public static final String NetBlock = "NetBlock";// 1 integer


    private SQLiteDatabase db;
    private DBHelper dbHelper;

    public XlsxCon(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open() {
        if (null == db || !db.isOpen()) {
            try {
                db = dbHelper.getWritableDatabase();
            } catch (SQLiteException sqLiteException) {
            }
        }
    }

    public void close() {
        if (db != null) {
            db.close();
        }
    }

    public int insert(String table, ContentValues values) {
        try {
            db = dbHelper.getWritableDatabase();
            int y = (int) db.insert(table, null, values);
            db.close();
//            Log.e("Data Inserted", "Data Inserted");
//            Log.e("y", y + "");
            return y;
        } catch (Exception ex) {
            Log.e("Error Insert", ex.getMessage());
            return 0;
        }
    }

    public void delete() {
        db.execSQL("delete from " + Inventory);
    }

    public Cursor getAllRow(String table) {
        return db.query(table, null, null, null, null, null, id);
    }

    private class DBHelper extends SQLiteOpenHelper {
        private static final int VERSION = 1;
        private static final String DB_NAME = "MyDB1.db";

        public DBHelper(Context context) {
            super(context, DB_NAME, null, VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
//            db.execSQL("DROP TABLE IF EXISTS " + Inventory);
            String create_sql = "CREATE TABLE IF NOT EXISTS " + Inventory + "("
                    + id + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + ProductName + " TEXT ," + ProductNo + " TEXT ,"
                    + Found + " TEXT ," + AssetNo + " TEXT ," + MajorCat + " TEXT ," + MinorCat + " TEXT ," + AssetName + " TEXT ," + TagState
                    + " TEXT ," + Type + " TEXT ," + Remarks + " TEXT ," + AssetCap + " TEXT ," + Quant + " TEXT ," + OriCost + " TEXT ," + CurCost + " TEXT ," + NetBlock
                    + " TEXT " + ")";
            db.execSQL(create_sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + Inventory);
        }

    }

    Cursor readAllData(){
        String query="SELECT * FROM " + Inventory;
        SQLiteDatabase db=dbHelper.getReadableDatabase();
        Cursor cursor = null;
        if(db!=null){
            cursor = db.rawQuery(query,null);

        }
        return cursor;

    }
    Cursor getFoundCount(){
        String query = "SELECT COUNT(Found) FROM " + Inventory+ " WHERE Found = '1'";
        SQLiteDatabase db=dbHelper.getReadableDatabase();
        Cursor cursor = null;
        if(db!=null){
            cursor=db.rawQuery(query, null);
        }
        return cursor;
    }

}
