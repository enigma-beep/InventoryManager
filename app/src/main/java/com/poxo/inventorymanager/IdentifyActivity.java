package com.poxo.inventorymanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import co.kr.bluebird.sled.BTReader;

public class IdentifyActivity extends AppCompatActivity {

    private TextView tv;

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

    private BTReader mReader;

    private Context mContext;

    private static final String TAG = IdentifyActivity.class.getSimpleName();

    private static final boolean D = Constants.MAIN_D;

    SearchAdapter searchAdapter;

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

    Button btIdentify;
    EditText etIdentify;
    XlsxCon controller = new XlsxCon(IdentifyActivity.this);

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify);

//        String mEPC = getIntent().getStringExtra("mEPC");
//        tv=findViewById(R.id.textViewtest);
//        tv.setText(mEPC);
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
        btIdentify=findViewById(R.id.btIdenRfid);
        etIdentify=findViewById(R.id.etIdenRfid);
        recyclerView=findViewById(R.id.identify_rview);




        storeDataInArrays();

        searchAdapter = new SearchAdapter(IdentifyActivity.this, ProductName,ProductNo);
        recyclerView.setAdapter(searchAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(IdentifyActivity.this));


        etIdentify.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

                // filter your list from your input
                filter(s.toString());
                //you can use runnable postDelayed like 500 ms to delay search text
            }
        });


    }

    void filter(String text){
        String term=text.toLowerCase();
        ArrayList<String> tempName = new ArrayList<>();
        ArrayList<String> tempNo = new ArrayList<>();

        for(String element: ProductName){
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if(element.toLowerCase().contains(term)){
                tempName.add(element);
                int index=ProductName.indexOf(element);
                tempNo.add(ProductNo.get(index));
            }
        }
        //update recyclerview
        searchAdapter.updateList(tempName,tempNo);
    }

    void storeDataInArrays(){
        Cursor cursor= controller.readAllData();
        if(cursor.getCount()==0){
            Toast.makeText(IdentifyActivity.this,"No Data", Toast.LENGTH_SHORT).show();
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
}