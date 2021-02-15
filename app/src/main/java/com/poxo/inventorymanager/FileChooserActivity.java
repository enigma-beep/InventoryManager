package com.poxo.inventorymanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.poxo.inventorymanager.fragment.BTConnectivityFragment;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import co.kr.bluebird.sled.BTReader;
import co.kr.bluebird.sled.SDConsts;

public class FileChooserActivity extends AppCompatActivity {

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

    private TextView phoneBatteryTxt;
    ImageView ivPhoneBattery;
    ImageView ivScannerBattery;
    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            deviceStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS,-1);
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
//            phoneBatteryTxt.setText("Phone Battery: "+String.valueOf(level) + "%");
            if(deviceStatus == BatteryManager.BATTERY_STATUS_CHARGING){

                phoneBatteryTxt.setText("Charging");
                ivPhoneBattery.setImageResource(R.drawable.batterychargecharging);

            }
            if(deviceStatus == BatteryManager.BATTERY_STATUS_NOT_CHARGING||deviceStatus == BatteryManager.BATTERY_STATUS_DISCHARGING){

                phoneBatteryTxt.setText("Phone Battery: "+level+" %");

                if(level>0&&level<=20){
                    ivPhoneBattery.setImageResource(R.drawable.batterychargeverylow);
                }
                else if(level>20&&level<=40){
                    ivPhoneBattery.setImageResource(R.drawable.batterychargelow);
                }
                else if(level>40&&level<=60){
                    ivPhoneBattery.setImageResource(R.drawable.batterychargemedium);
                }
                else if(level>60&&level<=80){
                    ivPhoneBattery.setImageResource(R.drawable.batterychargehigh);
                }
                else if(level>80&&level<100){
                    ivPhoneBattery.setImageResource(R.drawable.batterychargeveryhigh);
                }
                else if(level==100){
                    ivPhoneBattery.setImageResource(R.drawable.batterychargefull);
                }

            }

        }
    };
    int deviceStatus;
    private TextView tvScannerBattery;
    private Button mConnectButton;
    private Button mResumeButton;
    private BTConnectivityFragment mBTConnectivityFragment;
    private Fragment mCurrentFragment;
    private TextView tvFileName;
    private Fragment mFragment;
    private TextView tvStatus;
    public static final int requestcode = 1;
    private FragmentManager mFragmentManager;
    private BTReader mReader;
    public static final int MSG_OPTION_CONNECT_STATE_CHANGED = 0;
    private Context mContext;
    private ImageView mConnectImage;
    public static final int MSG_BACK_PRESSED = 2;
    private static final boolean D = Constants.INV_D;
    private FileChooserActivity.MainHandler mMainHandler = new FileChooserActivity.MainHandler(this);
    public final FileChooserActivity.UpdateConnectHandler mUpdateConnectHandler = new FileChooserActivity.UpdateConnectHandler(this);
    private MaterialCardView fileCard;
    private CardView toolsCard;
    private MaterialCardView searchCard;
    private MaterialCardView identifyCard;

    private boolean mIsConnected;
    private LinearLayout mUILayout;
    Button btnimport;

    public static final String Inventory = "Inventory";
    public static final String ProductNamesql = "ProductNo";
    public static final String id = "_id";
    String value="RFID Code";
    String fileName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_chooser);

        phoneBatteryTxt = (TextView) this.findViewById(R.id.tvPhonebattery);
        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        mContext=this;

        String FileName = getIntent().getStringExtra("FileName");



        ivPhoneBattery=findViewById(R.id.ivPhoneBattery);
        ivScannerBattery=findViewById(R.id.ivScannerBattery);
        tvScannerBattery=findViewById(R.id.tvScannerBattery);

        mConnectButton = findViewById(R.id.manage_bt);
        mConnectImage=findViewById(R.id.ivBluetooth);
        mConnectButton.setOnClickListener(buttonListener);
        mUILayout = findViewById(R.id.ui_layout);
        mFragmentManager = getFragmentManager();
        mIsConnected = false;
        tvStatus=findViewById(R.id.btStatus);
        mResumeButton=findViewById(R.id.btnResume);
        tvFileName=findViewById(R.id.tvFileName);
        fileCard=findViewById(R.id.fileCard);
        toolsCard=findViewById(R.id.toolsCard);
        searchCard=findViewById(R.id.searchCard);
        identifyCard=findViewById(R.id.identifyCard);

        btnimport = findViewById(R.id.btnAdd);
        ActivityCompat.requestPermissions(FileChooserActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1);

        btnimport.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                Intent fileintent = new Intent(Intent.ACTION_GET_CONTENT);
                fileintent.setType("gagt/sdf");
                try {
                    startActivityForResult(fileintent, requestcode);

                } catch (ActivityNotFoundException e) {
//                    lbl.setText("No activity can handle picking a file. Showing alternatives.");
                }

            }
        });

        fileCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(FileChooserActivity.this,MainActivity.class);
                startActivity(i);
            }
        });
        searchCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(FileChooserActivity.this,SearchActivity.class);
                startActivity(i);
            }
        });
        identifyCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(FileChooserActivity.this,IdentifyActivity.class);
                startActivity(i);
            }
        });

        if(FileName==null){

        }else{
            tvFileName.setText(FileName+" Uploaded");
            toolsCard.setVisibility(View.VISIBLE);
        }



    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null)
            return;
        switch (requestCode) {
            case requestcode:
                String FilePath = data.getData().getPath();
                String FileName = FilePath.substring(FilePath.lastIndexOf("/")).replace("/","").trim();

                if (FilePath.contains("/root_path"))
                    FilePath = FilePath.replace("/root_path", "");



                try {
                    if (resultCode == RESULT_OK) {
                        AssetManager am = this.getAssets();
                        InputStream inStream;
                        Workbook wb = null;

                        try {
                            inStream = new FileInputStream(FilePath);

                            if (FilePath.substring(FilePath.lastIndexOf(".")).equals(".xls")) {
                                wb = new HSSFWorkbook(inStream);
                            } else if (FilePath.substring(FilePath.lastIndexOf(".")).equals(".xlsx")) {
                                wb = new XSSFWorkbook(inStream);
                            } else {
                                wb = null;
//                                lbl.setText("Please select a valid Excel file");
                                return;

                            }

                            inStream.close();
                        } catch (IOException e) {
//                            lbl.setText("First " + e.getMessage());
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
//                    lbl.setText(ex.getMessage() + "Second");
                }
                SQLiteDatabase filedatabase=openOrCreateDatabase("fileLoc.db",MODE_PRIVATE,null);
                filedatabase.execSQL("CREATE TABLE IF NOT EXISTS FL(FileLoc varchar(50));");
                filedatabase.execSQL("INSERT INTO FL VALUES('"+FilePath+"');");


                SQLiteDatabase mydatabase=openOrCreateDatabase("MyDB1.db",MODE_PRIVATE,null);
                mydatabase.execSQL("DELETE FROM " + Inventory+ " WHERE "+ProductNamesql+"='"+value+"'");
//                tvFileName.setText(FileName);
//                fileCard.setVisibility(View.VISIBLE);
//                mResumeButton.setVisibility(View.VISIBLE);
                Intent i = new Intent(FileChooserActivity.this,FileChooserActivity.class);
                i.putExtra("FileName", FileName);
                startActivity(i);
                finish();

        }
    }
    public View.OnClickListener buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = 0;
            switch(v.getId()) {
                case R.id.manage_bt:
                    id = 0;
                    break;

            }
            selectItem(id);
        }
    };

    private void selectItem(int position) {
        switch (position) {
            case 0:
                if (mBTConnectivityFragment == null)
                    mBTConnectivityFragment = BTConnectivityFragment.newInstance();
                mCurrentFragment = mBTConnectivityFragment;
                break;
            default:
                return;
        }
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.replace(R.id.content, mCurrentFragment);
        ft.commit();
        mUILayout.setVisibility(View.GONE);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
//        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        mDrawerToggle.onConfigurationChanged(newConfig);
    }






    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }



    @Override
    public void onBackPressed() {
        if (mCurrentFragment != null)
            switchToHome();
        else
//            this.deleteDatabase("MyDB1.db");
            super.onBackPressed();
    }



    public void handleMessage(Message m) {
        if (D) {
//            Log.d(TAG, "mMainHandler");
//            tvLog.setText("mMainHandler");
        }

        if (D) {
//            Log.d(TAG, "command = " + m.arg1 + " result = " + m.arg2 + " obj = data");
//            tvLog.setText("command = " + m.arg1 + " result = " + m.arg2 + " obj = data");
        }
        switch (m.what) {
            case SDConsts.Msg.SDMsg:
                if (m.arg1 == SDConsts.SDCmdMsg.SLED_HOTSWAP_STATE_CHANGED) {
                    if (m.arg2 == SDConsts.SDHotswapState.HOTSWAP_STATE)
                        Toast.makeText(mContext, "HOTSWAP STATE CHANGED = HOTSWAP_STATE", Toast.LENGTH_SHORT).show();
                    else if (m.arg2 == SDConsts.SDHotswapState.NORMAL_STATE)
                        Toast.makeText(mContext, "HOTSWAP STATE CHANGED = NORMAL_STATE", Toast.LENGTH_SHORT).show();
                }
                break;
            case SDConsts.Msg.RFMsg:
                break;
            case SDConsts.Msg.BCMsg:
                break;
        }


    }
    private void switchToHome() {
        try {
//            mDrawerLayout.closeDrawer(mDrawerList);
            if (mCurrentFragment != null) {
                FragmentTransaction ft = mFragmentManager.beginTransaction();
                ft.remove(mCurrentFragment);
                ft.commit();
                mCurrentFragment = null;
                mReader = BTReader.getReader(mContext, mMainHandler);
            }
            setTitle(getString(R.string.app_name));
            if (mUILayout.getVisibility() != View.VISIBLE)
                mUILayout.setVisibility(View.VISIBLE);
        } catch (java.lang.IllegalStateException e) {
        }
        return;
    }
    private static class MainHandler extends Handler {
        private final WeakReference<FileChooserActivity> mExecutor;
        public MainHandler(FileChooserActivity f) {
            mExecutor = new WeakReference<>(f);
        }

        @Override
        public void handleMessage(Message msg) {
            FileChooserActivity executor = mExecutor.get();
            if (executor != null) {
                executor.handleMainHandler(msg);
            }
        }
    }

    private void updateConnectState() {
        if (mReader.BT_GetConnectState() == SDConsts.BTConnectState.CONNECTED){
            mIsConnected = true;
            mConnectImage.setImageResource(R.drawable.bluetoothconnected);
            tvStatus.setText("Connected");
            if (mReader != null && mReader.BT_GetConnectState() == SDConsts.BTConnectState.CONNECTED) {
                int value = mReader.SD_GetBatteryStatus();
                tvScannerBattery.setText("Scanner Battery: " + value + "%");
                if(value>0&&value<=20){
                    ivScannerBattery.setImageResource(R.drawable.batterychargeverylow);
                }
                else if(value>20&&value<=40){
                    ivScannerBattery.setImageResource(R.drawable.batterychargelow);
                }
                else if(value>40&&value<=60){
                    ivScannerBattery.setImageResource(R.drawable.batterychargemedium);
                }
                else if(value>60&&value<=80){
                    ivScannerBattery.setImageResource(R.drawable.batterychargehigh);
                }
                else if(value>80&&value<100){
                    ivScannerBattery.setImageResource(R.drawable.batterychargeveryhigh);
                }
                else if(value==100){
                    ivScannerBattery.setImageResource(R.drawable.batterychargefull);
                }

            }

        }
        else {
            mIsConnected = false;
            mConnectImage.setImageResource(R.drawable.bluetoothnotconnected);
            tvStatus.setText("Not Connected");
        }
        invalidateOptionsMenu();
    }

    private static class UpdateConnectHandler extends Handler {
        private final WeakReference<FileChooserActivity> mExecutor;
        public UpdateConnectHandler(FileChooserActivity ac) {
            mExecutor = new WeakReference<>(ac);
        }

        @Override
        public void handleMessage(Message msg) {
            FileChooserActivity executor = mExecutor.get();
            if (executor != null) {
                executor.handleUpdateConnectHandler(msg);
                executor.handleMessage(msg);
            }
        }
    }

    public void handleUpdateConnectHandler(Message m) {
        if (m.what == MSG_OPTION_CONNECT_STATE_CHANGED) {
            updateConnectState();
        }
        else if (m.what == MSG_BACK_PRESSED)
            switchToHome();
    }
    @Override
    public void onStart() {

        // TODO Auto-generated method stub
        if (D){
//            Log.d(TAG, " onStart");
//            tvLog.setText("onStart");
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        boolean openResult = false;

        mReader = BTReader.getReader(mContext, mMainHandler);
//        mReader = BTReader.getReader(mContext, mInventoryHandler);
        if (mReader != null)
            openResult = mReader.SD_Open();
        if (openResult == SDConsts.RF_OPEN_SUCCESS) {
//            Log.i(TAG, "Reader opened");
//            tvLog.setText("Reader opened");
        }
        else if (openResult == SDConsts.RF_OPEN_FAIL)
            if (D) {
//                Log.e(TAG, "Reader open failed");
//                tvLog.setText("Reader open Failed");
            }

        if (mReader != null && mReader.BT_GetConnectState() == SDConsts.BTConnectState.CONNECTED) {
            int value = mReader.SD_GetBatteryStatus();
                tvScannerBattery.setText("Scanner Battery: " + value + "%");
            if(value>0&&value<=20){
                ivScannerBattery.setImageResource(R.drawable.batterychargeverylow);
            }
            else if(value>20&&value<=40){
                ivScannerBattery.setImageResource(R.drawable.batterychargelow);
            }
            else if(value>40&&value<=60){
                ivScannerBattery.setImageResource(R.drawable.batterychargemedium);
            }
            else if(value>60&&value<=80){
                ivScannerBattery.setImageResource(R.drawable.batterychargehigh);
            }
            else if(value>80&&value<100){
                ivScannerBattery.setImageResource(R.drawable.batterychargeveryhigh);
            }
            else if(value==100){
                ivScannerBattery.setImageResource(R.drawable.batterychargefull);
            }

        }

        updateConnectState();

        super.onStart();
    }
    public void handleMainHandler(Message m) {
        if (D) {
//            Log.d(TAG, "mMainHandler");
//            tvLog.setText("mMainHandler");
        }
        if (D) {
//            Log.d(TAG, "m arg1 = " + m.arg1 + " arg2 = " + m.arg2);
//            tvLog.setText("m arg1 = " + m.arg1 + " arg2 = " + m.arg2);
        }
        switch (m.what) {
            case SDConsts.Msg.SDMsg:
                switch(m.arg1) {


                    //You can receive this message every a minute. SDConsts.SDCmdMsg.SLED_BATTERY_STATE_CHANGED
                    case SDConsts.SDCmdMsg.SLED_BATTERY_STATE_CHANGED:
                        //Toast.makeText(mContext, "Battery state = " + m.arg2, Toast.LENGTH_SHORT).show();
                        if (D) {
//                            Log.d(TAG, "Battery state = " + m.arg2);
//                            tvLog.setText("Battery state = " + m.arg2);
                        }
                        break;
                    case SDConsts.SDCmdMsg.SLED_HOTSWAP_STATE_CHANGED:
                        if (m.arg2 == SDConsts.SDHotswapState.HOTSWAP_STATE)
                            Toast.makeText(mContext, "HOTSWAP STATE CHANGED = HOTSWAP_STATE", Toast.LENGTH_SHORT).show();
                        else if (m.arg2 == SDConsts.SDHotswapState.NORMAL_STATE)
                            Toast.makeText(mContext, "HOTSWAP STATE CHANGED = NORMAL_STATE", Toast.LENGTH_SHORT).show();
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.detach(mFragment).attach(mFragment).commit();
                        break;
                }
                break;
        }
    }






}