package com.poxo.inventorymanager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.FragmentTransaction;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;

import java.lang.ref.WeakReference;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;

import co.kr.bluebird.sled.BTReader;
import co.kr.bluebird.sled.SDConsts;

public class SearchActivity extends AppCompatActivity {

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

    private static final String TAG = SearchActivity.class.getSimpleName();

    private static final boolean D = Constants.MAIN_D;

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

    TextView mRFIDNo;
    TextView mAssetNo;
    TextView mMajorCat;
    TextView mMinorCat;
    TextView mAssetDesc;
    TextView mAssetName;
    TextView mTagState;
    TextView mType;
    TextView mRemark;
    TextView mAssetCap;
    TextView mQuant;
    TextView mOriCost;
    TextView mCurCost;
    TextView mNetBlock;
    CardView detailsCard;

    Button btSearch;
    EditText etSearch;
    int power =-1;
    public static final String Inventory = "Inventory";
    public static final String id = "_id";// 0 integer
    public static final String ProductNamesql = "ProductNo";// 1 text(String)

    XlsxCon controller = new XlsxCon(SearchActivity.this);

    private AccessHandler mAccessHandler = new AccessHandler(this);

    int triggerFlag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mContext = this;

        mRFIDNo=findViewById(R.id.tvRFIDNo);
        mAssetNo=findViewById(R.id.tvAssetNo);
        mAssetName=findViewById(R.id.tvAssetName);
        mMajorCat=findViewById(R.id.tvMajorCat);
        mMinorCat=findViewById(R.id.tvMinorCat);
        mAssetDesc=findViewById(R.id.tvAssetDesc);
        mTagState=findViewById(R.id.tvTagState);
        mType=findViewById(R.id.tvType);
        mRemark=findViewById(R.id.tvRemarks);
        mAssetCap=findViewById(R.id.tvAssetCap);
        mQuant=findViewById(R.id.tvQuant);
        mOriCost=findViewById(R.id.tvOriCost);
        mCurCost=findViewById(R.id.tvCurCost);
        mNetBlock=findViewById(R.id.tvNetBlock);
        btSearch=findViewById(R.id.btSearchRfid);
        etSearch=findViewById(R.id.etSearchRfid);
        detailsCard=findViewById(R.id.detailsCard);

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

        storeDataInArrays();


        mReader = BTReader.getReader(mContext, mAccessHandler);
        power=mReader.RF_SetRadioPowerState(5);
        if(power==0){
//            Toast.makeText(SearchActivity.this,"RF_PowerState=5",Toast.LENGTH_SHORT).show();
        }else{
//            Toast.makeText(SearchActivity.this,"RF_PowerState change failed",Toast.LENGTH_SHORT).show();
        }


        btSearch.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if(etSearch.getText().equals("")){
                    Toast.makeText(SearchActivity.this,"Not Found", Toast.LENGTH_SHORT).show();
                }
                else {
                    String searchTerm=etSearch.getText().toString();
                    if(ProductNo.contains(searchTerm)){
                        final int index = ProductNo.indexOf(searchTerm);
                        detailsCard.setVisibility(View.VISIBLE);
//                        Toast.makeText(SearchActivity.this,ProductNo.get(index),Toast.LENGTH_SHORT).show();
                        mRFIDNo.setText("Rfid No: "+ProductNo.get(index));
                        mAssetNo.setText(AssetNo.get(index));
                        mAssetName.setText(MajorCat.get(index));
                        mMajorCat.setText(MinorCat.get(index));
                        mMinorCat.setText(ProductName.get(index));
                        mAssetDesc.setText(AssetName.get(index));
                        mTagState.setText(TagSate.get(index));
                        mType.setText(Type.get(index));
                        mRemark.setText(Remarks.get(index));
                        mAssetCap.setText(LocalDate.of( 1899 , Month.DECEMBER , 30 ).plusDays((long) Integer.parseInt(  AssetCap.get(index) )).toString());
                        mQuant.setText(Quant.get(index));
                        mOriCost.setText(OriCost.get(index));
                        mCurCost.setText(CurCost.get(index));
                        mNetBlock.setText(NetBlock.get(index));

                    }

                }

                int result = -1000;
                String sResult = null;
                StringBuilder message = new StringBuilder();
                result = mReader.RF_READ(SDConsts.RFMemType.EPC, 2, 2, "00000000", false);
                if (result == SDConsts.RFResult.SUCCESS)
                    message.append("RF_READ");
                else
                    message.append("RF_READ failed " + result);

//                handleMessage();

//                Toast.makeText(SearchActivity.this,message.toString(),Toast.LENGTH_SHORT).show();


            }
        });

    }

    void storeDataInArrays(){
        Cursor cursor= controller.readAllData();
        if(cursor.getCount()==0){
            Toast.makeText(SearchActivity.this,"No Data", Toast.LENGTH_SHORT).show();
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

    private static class AccessHandler extends Handler {
        private final WeakReference<SearchActivity> mExecutor;
        public AccessHandler(SearchActivity f) {
            mExecutor = new WeakReference<>(f);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void handleMessage(Message msg) {
            SearchActivity executor = mExecutor.get();
            if (executor != null) {
                executor.handleMessage(msg);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void handleMessage(Message m) {
        if (D) Log.d(TAG, "mAccessHandler m arg1 = " + m.arg1 + " arg2 = " + m.arg2);
        int result = m.arg2;
        String data = "";
        if (m.obj != null  && m.obj instanceof String)
            data = (String)m.obj;
        String messageStr = null;
        switch (m.what) {
            case SDConsts.Msg.RFMsg:
                switch (m.arg1) {
                    //RF_Read callback message
                    case SDConsts.RFCmdMsg.READ:
                        messageStr = "READ result = " + result + "\n" + data;
                        break;

                    case SDConsts.RFCmdMsg.WRITE:
                        messageStr = "WRITE result = " + result + " " + data;
                        break;
                    case SDConsts.RFCmdMsg.WRITE_TAG_ID:
                        messageStr = "WRITE_TAG_ID result = " + result + " " + data;
                        break;
                    case SDConsts.RFCmdMsg.WRITE_ACCESS_PASSWORD:
                        messageStr = "WRITE_ACCESS_PASSWORD result = " + result + " " + data;
                        break;
                    case SDConsts.RFCmdMsg.WRITE_KILL_PASSWORD:
                        messageStr = "WRITE_KILL_PASSWORD result = " + result + " " + data;
                        break;
                    case SDConsts.RFCmdMsg.LOCK:
                        messageStr = "LOCK result = " + result + " " + data;
                        break;
                    case SDConsts.RFCmdMsg.KILL:
                        messageStr = "KILL result = " + result + " " + data;
                        break;
                    case SDConsts.RFCmdMsg.BLOCK_WRITE:
                        messageStr = "BLOCK_WRITE result = " + result + " " + data;
                        break;
                    case SDConsts.RFCmdMsg.BLOCK_PERMALOCK:
                        messageStr = "BLOCK_PERMALOCK result = " + result + " " + data;
                        break;
                    case SDConsts.RFCmdMsg.BLOCK_ERASE:
                        messageStr = "BLOCK_ERASE result = " + result + " " + data;
                        break;
                    case SDConsts.RFCmdMsg.UPDATE_RF_FW_START:

                        break;
                    case SDConsts.RFCmdMsg.UPDATE_RF_FW:
//                        setProgressState(result);
                        break;
                    case SDConsts.RFCmdMsg.UPDATE_RF_FW_END:
//                        closeDialog();
//                        messageStr = "UPDATE_RF_FW_END " + result + " " + data;
                        break;
                }
                break;
            case SDConsts.Msg.SDMsg:
                switch(m.arg1){
                    case SDConsts.SDCmdMsg.TRIGGER_PRESSED:
////                        if(triggerFlag==0){
//                            int res=-1000;
//                            res = mReader.RF_READ(SDConsts.RFMemType.EPC, 2, 2, "00000000", false);
                            btSearch.performClick();
//                            Toast.makeText(this,"trigger pressed", Toast.LENGTH_SHORT).show();
////                        }
                        break;
                    case SDConsts.SDCmdMsg.TRIGGER_RELEASED:
////                        triggerFlag=0;
                        break;
                }

                if (m.arg1 == SDConsts.SDCmdMsg.SLED_HOTSWAP_STATE_CHANGED) {
                    if (m.arg2 == SDConsts.SDHotswapState.HOTSWAP_STATE)
                        Toast.makeText(mContext, "HOTSWAP STATE CHANGED = HOTSWAP_STATE", Toast.LENGTH_SHORT).show();
                    else if (m.arg2 == SDConsts.SDHotswapState.NORMAL_STATE)
                        Toast.makeText(mContext, "HOTSWAP STATE CHANGED = NORMAL_STATE", Toast.LENGTH_SHORT).show();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
//                    ft.detach(mFragment).attach(mFragment).commit();
                }
                break;
            case SDConsts.Msg.BTMsg:
                if (m.arg1 == SDConsts.BTCmdMsg.SLED_BT_CONNECTION_STATE_CHANGED) {
                    if (D) Log.d(TAG, "SLED_BT_CONNECTION_STATE_CHANGED = " + m.arg2);

                }
                break;
        }
        if (messageStr != null){
            Toast.makeText(mContext, messageStr, Toast.LENGTH_SHORT).show();

            String searchTerm=data.trim();
            if(ProductNo.contains(searchTerm)) {
                final int index = ProductNo.indexOf(searchTerm);
                detailsCard.setVisibility(View.VISIBLE);
//                        Toast.makeText(SearchActivity.this,ProductNo.get(index),Toast.LENGTH_SHORT).show();
                mRFIDNo.setText("Rfid No: "+ProductNo.get(index));
                mAssetNo.setText(AssetNo.get(index));
                mAssetName.setText(MajorCat.get(index));
                mMajorCat.setText(MinorCat.get(index));
                mMinorCat.setText(ProductName.get(index));
                mAssetDesc.setText(AssetName.get(index));
                mTagState.setText(TagSate.get(index));
                mType.setText(Type.get(index));
                mRemark.setText(Remarks.get(index));
                mAssetCap.setText(LocalDate.of(1899, Month.DECEMBER, 30).plusDays((long) Integer.parseInt(AssetCap.get(index))).toString());
                mQuant.setText(Quant.get(index));
                mOriCost.setText(OriCost.get(index));
                mCurCost.setText(CurCost.get(index));
                mNetBlock.setText(NetBlock.get(index));
            }
        }
    }

    protected void onStart() {
        // TODO Auto-generated method stub
        if (D) Log.d(TAG, " onStart");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        boolean openResult = false;
        mReader = BTReader.getReader(mContext, mAccessHandler);
        if (mReader != null)
            openResult = mReader.SD_Open();
        if (openResult == SDConsts.RF_OPEN_SUCCESS) {
            Log.i(TAG, "Reader opened");
        }
        else if (openResult == SDConsts.RF_OPEN_FAIL)
            if (D) Log.e(TAG, "Reader open failed");

//        updateConnectState();
        super.onStart();
    }


}