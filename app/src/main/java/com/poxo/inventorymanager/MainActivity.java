package com.poxo.inventorymanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity  extends AppCompatActivity {

    static {
        System.setProperty(
                "org.apache.poi.javax.xml.stream.XMLInputFactory",
                "com.fasterxml.aalto.stax.InputFactoryImpl"
        );
        System.setProperty(
                "org.apache.poi.javax.xml.stream.XMLOutputFactory",
                "com.fasterxml.aalto.stax.OutputFactoryImpl"
        );
        System.setProperty(
                "org.apache.poi.javax.xml.stream.XMLEventFactory",
                "com.fasterxml.aalto.stax.EventFactoryImpl"
        );
    }

    TextView lbl;
    XlsxCon controller = new XlsxCon(MainActivity.this);
    Button btnimport;
    Button btSearch;
    EditText etSearch;
    RecyclerView rv;
    public static final int requestcode = 1;
    static String tableName;

    CustomAdapter customAdapter;

    ArrayList<String> ProductName;
    ArrayList<String> ProductNo;
    ArrayList<String> Found;
    ArrayList<String> AssetNo;

    ArrayList<String> MajorCat;
    ArrayList<String> MinorCat;
    ArrayList<String> AssetName;
    ArrayList<String> TagSate;

    ArrayList<String> Type;
    ArrayList<String> Remarks;
    ArrayList<String> AssetCap;
    ArrayList<String> Quant;
    ArrayList<String> OriCost;
    ArrayList<String> CurCost;
    ArrayList<String> NetBlock;

    public static final String Inventory = "Inventory";
    public static final String id = "_id";// 0 integer
    public static final String ProductNamesql = "ProductNo";// 1 text(String)


    TextView tvTotal;
    TextView tvFound;
    TextView tvNotfound;
    String total, totalquery;
    String f, fquery;
    String nf, nfquery;
    String searchTerm;
    int foundcount;
    String value="RFID Code";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnimport = (Button) findViewById(R.id.btnupload);
        tvTotal=findViewById(R.id.tvTotal);
        tvFound=findViewById(R.id.tvFound);
        tvNotfound=findViewById(R.id.tvNotfound);
        btSearch=findViewById(R.id.btSearch);
        etSearch=findViewById(R.id.etSearch);

        rv= findViewById(R.id.rview);

        ProductName = new ArrayList<>();
        ProductNo = new ArrayList<>();
        Found = new ArrayList<>();
        AssetNo = new ArrayList<>();

        MajorCat = new ArrayList<>();
        MinorCat = new ArrayList<>();
        AssetName = new ArrayList<>();
        TagSate = new ArrayList<>();

        Type = new ArrayList<>();
        Remarks = new ArrayList<>();
        AssetCap = new ArrayList<>();
        Quant = new ArrayList<>();
        OriCost = new ArrayList<>();
        CurCost = new ArrayList<>();
        NetBlock = new ArrayList<>();

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1);



        lbl = (TextView) findViewById(R.id.txtresulttext);
//        lv = getListView();
        tableName = "Inventory";

        btnimport.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent fileintent = new Intent(Intent.ACTION_GET_CONTENT);
                fileintent.setType("gagt/sdf");
                try {
                    startActivityForResult(fileintent, requestcode);

                } catch (ActivityNotFoundException e) {
                    lbl.setText("No activity can handle picking a file. Showing alternatives.");
                }

            }
        });

        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    searchTerm=etSearch.getText().toString();

                if (searchTerm.equals("")) {
                    Toast.makeText(MainActivity.this,"Not Found", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(ProductNo.contains(searchTerm)){
                        int index = ProductNo.indexOf(searchTerm);
                        Toast.makeText(MainActivity.this,"Found", Toast.LENGTH_SHORT).show();
                        SQLiteDatabase mydatabase=openOrCreateDatabase("MyDB1.db",MODE_PRIVATE,null);
                        String sql = "UPDATE "+ Inventory +" SET Found = '1' WHERE ProductNo = "+searchTerm;
                        mydatabase.execSQL(sql);
                        Found.set(index, "1");
                        customAdapter.notifyDataSetChanged();
                        Cursor cursor= controller.readAllData();
                        tvTotal.setText(String.valueOf(cursor.getCount()));
                        Cursor cursor1= controller.getFoundCount();
                        cursor1.moveToFirst();
                        tvFound.setText(String.valueOf(cursor1.getInt(0)));
                        cursor1.moveToFirst();
                        tvNotfound.setText(String.valueOf((cursor.getCount())-(cursor1.getInt(0))));
                    }
                    else{
                        Toast.makeText(MainActivity.this,"Not Found", Toast.LENGTH_SHORT).show();
                    }
                }





            }
        });

        storeDataInArrays();


        customAdapter = new CustomAdapter(MainActivity.this, ProductName,ProductNo,Found,AssetNo,MajorCat,MinorCat, AssetName, TagSate, Type, Remarks, AssetCap, Quant, OriCost, CurCost, NetBlock);
        rv.setAdapter(customAdapter);
        rv.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        Cursor cursor= controller.readAllData();
        tvTotal.setText(String.valueOf(cursor.getCount()));
        Cursor cursor1= controller.getFoundCount();
        cursor1.moveToFirst();
        tvFound.setText(String.valueOf(cursor1.getInt(0)));
        cursor1.moveToFirst();
        tvNotfound.setText(String.valueOf((cursor.getCount())-(cursor1.getInt(0))));




    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                    Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }
    }

    void storeDataInArrays(){
        Cursor cursor= controller.readAllData();
        if(cursor.getCount()==0){
            Toast.makeText(MainActivity.this,"No Data", Toast.LENGTH_SHORT).show();
        }else{
            while(cursor.moveToNext()){
                ProductName.add(cursor.getString(1));
                ProductNo.add(cursor.getString(2));
                Found.add(cursor.getString(3));
                AssetNo.add(cursor.getString(4));
                MajorCat.add(cursor.getString(5));
                MinorCat.add(cursor.getString(6));
                AssetName.add(cursor.getString(7));
                TagSate.add(cursor.getString(8));
                Type.add(cursor.getString(9));
                Remarks.add(cursor.getString(10));
                AssetCap.add(cursor.getString(11));
                Quant.add(cursor.getString(12));
                OriCost.add(cursor.getString(13));
                CurCost.add(cursor.getString(14));
                NetBlock.add(cursor.getString(15));


            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null)
            return;
        switch (requestCode) {
            case requestcode:
                String FilePath = data.getData().getPath();

                Log.e("File path", FilePath);

                if (FilePath.contains("/root_path"))
                    FilePath = FilePath.replace("/root_path", "");

                Log.e("New File path", FilePath);

                try {
                    if (resultCode == RESULT_OK) {
                        AssetManager am = this.getAssets();
                        InputStream inStream;
                        Workbook wb = null;

                        try {
                            inStream = new FileInputStream(FilePath);
                            Log.e("Extension", FilePath.substring(FilePath.lastIndexOf(".")));

                            if (FilePath.substring(FilePath.lastIndexOf(".")).equals(".xls")) {
                                Log.e("File Type", "Selected file is XLS");
                                wb = new HSSFWorkbook(inStream);
                            } else if (FilePath.substring(FilePath.lastIndexOf(".")).equals(".xlsx")) {
                                Log.e("File Type", "Selected file is XLSX");
                                wb = new XSSFWorkbook(inStream);
                            } else {
                                wb = null;
                                lbl.setText("Please select a valid Excel file");
                                return;

                            }

                            inStream.close();
                        } catch (IOException e) {
                            lbl.setText("First " + e.getMessage().toString());
                            e.printStackTrace();
                        }

                        XlsxCon dbAdapter = new XlsxCon(this);
                        Sheet sheet1 = wb.getSheetAt(0);

                        dbAdapter.open();
                        dbAdapter.delete();
                        dbAdapter.close();
                        dbAdapter.open();
                        ExcelHelper.insertExcelToSqlite(dbAdapter, sheet1);

                        dbAdapter.close();

                    }
                } catch (Exception ex) {
                    lbl.setText(ex.getMessage().toString() + "Second");
                    Log.e("POI Error", ex.getMessage().toString());
                }

                SQLiteDatabase mydatabase=openOrCreateDatabase("MyDB1.db",MODE_PRIVATE,null);
                mydatabase.execSQL("DELETE FROM " + Inventory+ " WHERE "+ProductNamesql+"='"+value+"'");

                Intent i = new Intent(MainActivity.this,MainActivity.class);
                startActivity(i);
                    finish();

        }
    }
}
