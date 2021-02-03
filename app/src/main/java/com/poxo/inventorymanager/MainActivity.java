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

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Point;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
//import android.support.annotation.NonNull;
import androidx.annotation.NonNull;
//import androidx.drawerlayout.widget.DrawerLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import com.poxo.inventorymanager.Constants;
import com.poxo.inventorymanager.MainActivity;
import com.poxo.inventorymanager.R;
import com.poxo.inventorymanager.control.ListItem;
import com.poxo.inventorymanager.control.TagListAdapter;
import com.poxo.inventorymanager.fileutil.FileManager;
import com.poxo.inventorymanager.fragment.BTConnectivityFragment;
//import com.poxo.inventorymanager.fragment.InventoryFragment;
import com.poxo.inventorymanager.permission.PermissionHelper;
import com.poxo.inventorymanager.stopwatch.StopwatchService;
import co.kr.bluebird.sled.BTReader;
import co.kr.bluebird.sled.SDConsts;
import co.kr.bluebird.sled.SelectionCriterias;










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

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final boolean D = Constants.INV_D;

    private StopwatchService mStopwatchSvc;

    private TagListAdapter mAdapter;

    TextView tvRfidno;

    private ListView mRfidList;

    private TextView tvLog;

    private TextView mBatteryText;

    private TextView mTimerText;

    private TextView mCountText;

    private TextView mSpeedCountText;

    private TextView mAvrSpeedCountTest;

    private Button mClearButton;

    private Button mInvenButton;

    private Button mStopInvenButton;

    private Switch mTurboSwitch;

    private Switch mRssiSwitch;

    private Switch mFilterSwitch;

    private Switch mSoundSwitch;

    private Switch mMaskSwitch;

    private Switch mToggleSwitch;

    private Switch mPCSwitch;

    private Switch mFileSwitch;

    private ProgressBar mProgressBar;

    private BTReader mReader;

    private Context mContext;

    private boolean mTagFilter = false;

    private boolean mSoundPlay = true;

    private boolean mMask = false;

    private boolean mInventory = false;

    private boolean mIsTurbo = true;

    private boolean mToggle = true;

    private boolean mIgnorePC = false;

    private boolean mRssi = true;

    private boolean mFile = false;

    private Handler mOptionHandler;

    private SoundPool mSoundPool;

    private int mSoundId;

    private float mSoundVolume;

    private boolean mSoundFileLoadState;

    private MainActivity.SoundTask mSoundTask;

    private double mOldTotalCount = 0;

    private double mOldSec = 0;

    private Fragment mFragment;

    private FileManager mFileManager;

    private String mLocateTag;

    private String mLocateEPC;

    private int mLocateStartPos;

    private LinearLayout mLocateLayout;

    private LinearLayout mListLayout;

    private ProgressBar mTagLocateProgress;

    private int mLocateValue;

    private ImageButton mBackButton;

    private TextView mLocateTv;

    private boolean mLocate;

    private TimerTask mLocateTimerTask;

    private Timer mClearLocateTimer;

    private Spinner mSessionSpin;
    private ArrayAdapter<CharSequence> mSessionChar;

    private Spinner mSelFlagSpin;
    private ArrayAdapter<CharSequence> mSelFlagChar;
    //private int mCurrentPower;

    private int mTickCount = 0;

    private MainActivity.UpdateStopwatchHandler mUpdateStopwatchHandler = new MainActivity.UpdateStopwatchHandler(this);

    private MainHandler mMainHandler = new MainHandler(this);

//    public static InventoryFragment newInstance() {
//        return new InventoryFragment();
//    }

    ///////////////////////////////////////////////////////////////////

//    private static final boolean DM = Constants.MAIN_D;

    public static final int MSG_OPTION_CONNECT_STATE_CHANGED = 0;

    public static final int MSG_BACK_PRESSED = 2;

    private String[] mFunctionsString;

//    private DrawerLayout mDrawerLayout;

//    private ListView mDrawerList;

//    private ActionBarDrawerToggle mDrawerToggle;

//    private BTReader mReader;

//    private Context mContext;

    private FragmentManager mFragmentManager;

    private boolean mIsConnected;

    private BTConnectivityFragment mBTConnectivityFragment;


    private LinearLayout mUILayout;

    private Fragment mCurrentFragment;

    private ImageButton mConnectButton;


    public final UpdateConnectHandler mUpdateConnectHandler = new UpdateConnectHandler(this);







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
        btnimport = findViewById(R.id.btnupload);
        tvTotal=findViewById(R.id.tvTotal);
        tvFound=findViewById(R.id.tvFound);
        tvNotfound=findViewById(R.id.tvNotfound);
        btSearch=findViewById(R.id.btSearch);
        etSearch=findViewById(R.id.etSearch);

        rv= findViewById(R.id.rview);

        tvRfidno=findViewById(R.id.tvRfidno);

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

//        SQLiteDatabase mydatabase=openOrCreateDatabase("MyDB1.db",MODE_PRIVATE,null);
//        mydatabase.execSQL("DROP TABLE "+Inventory);



        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1);

        ////////////////////////////////////////////////////////vvvvvvvvvvFROM INVENTORY ACTIVTIY IN RFIDTEST APPLICATIONvvvvvvvvvv//////////////////////////////////////

        mContext=this;

        mRfidList = findViewById(R.id.rfid_list);

        mRfidList.setOnItemClickListener(listItemClickListener);

        mLocateLayout = findViewById(R.id.tag_locate_container);

        mListLayout = findViewById(R.id.tag_list_container);

        mLocateTv = findViewById(R.id.tag_locate_text);

        mTagLocateProgress = findViewById(R.id.tag_locate_progress);

        mBackButton = findViewById(R.id.back_button);
        mBackButton.setOnClickListener(sledListener);

        mTimerText = findViewById(R.id.timer_text);

        mCountText = findViewById(R.id.count_text);

        mSpeedCountText = findViewById(R.id.speed_count_text);

        mAvrSpeedCountTest = findViewById(R.id.speed_avr_count_text);

        tvLog=findViewById(R.id.tvLog);

        Activity activity = MainActivity.this;

        if (activity != null) {
            String speedCountStr = activity.getString(R.string.speed_count_str) + activity.getString(R.string.speed_postfix_str);
            mSpeedCountText.setText(speedCountStr);
            mAvrSpeedCountTest.setText(speedCountStr);
        }

        mBatteryText = findViewById(R.id.battery_text);

        mTurboSwitch = findViewById(R.id.turbo_switch);

        mRssiSwitch = findViewById(R.id.rssi_switch);

        mFilterSwitch = findViewById(R.id.filter_switch);

        mSoundSwitch = findViewById(R.id.sound_switch);

        mMaskSwitch = findViewById(R.id.mask_switch);

        mToggleSwitch = findViewById(R.id.toggle_switch);

        mPCSwitch = findViewById(R.id.pc_switch);

        mFileSwitch = findViewById(R.id.file_switch);

        mClearButton = findViewById(R.id.clear_button);
        mClearButton.setOnClickListener(clearButtonListener);

        mInvenButton = findViewById(R.id.inven_button);
        mInvenButton.setOnClickListener(sledListener);

        mStopInvenButton = findViewById(R.id.stop_inven_button);
        mStopInvenButton.setOnClickListener(sledListener);

        mProgressBar = findViewById(R.id.timer_progress);
        mProgressBar.setVisibility(View.INVISIBLE);

        mSessionSpin = findViewById(R.id.session_spin);
        mSessionChar = ArrayAdapter.createFromResource(mContext, R.array.session_array,
                android.R.layout.simple_spinner_dropdown_item);
        mSessionSpin.setAdapter(mSessionChar);

        mSelFlagSpin = findViewById(R.id.sel_flag_spin);
        mSelFlagChar = ArrayAdapter.createFromResource(mContext, R.array.sel_flag_array,
                android.R.layout.simple_spinner_dropdown_item);
        mSelFlagSpin.setAdapter(mSelFlagChar);

        mAdapter = new TagListAdapter(mContext);
        mRfidList.setAdapter(mAdapter);

        ///////////////////////////////////////////////////////////////////
//        getActionBar().setDisplayHomeAsUpEnabled(true);
//        getActionBar().setDisplayShowHomeEnabled(false);

//        mDrawerLayout = findViewById(R.id.drawer_layout);
//        mDrawerList = findViewById(R.id.left_drawer);

//        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mUILayout = findViewById(R.id.ui_layout);

        mCurrentFragment = null;

        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        int buttonHeight = size.x / 3;

        mConnectButton = findViewById(R.id.connect_BT);



        mConnectButton.setOnClickListener(buttonListener);



        mFunctionsString = getResources().getStringArray(R.array.functions_array);
//        mDrawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mFunctionsString));
//        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        mFragmentManager = getFragmentManager();

        mIsConnected = false;





        bindStopwatchSvc();


        ////////////////////////////////////////////////////////^^^^^^^^^^^FROM INVENTORY ACTIVTIY IN RFIDTEST APPLICATION^^^^^^^^^^//////////////////////////////////////




        lbl = findViewById(R.id.txtresulttext);
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
//                        Toast.makeText(MainActivity.this,"Found", Toast.LENGTH_SHORT).show();
                        SQLiteDatabase mydatabase=openOrCreateDatabase("MyDB1.db",MODE_PRIVATE,null);
                        String sql = "UPDATE "+ Inventory +" SET Found = '1' WHERE ProductNo = '"+searchTerm+"'";
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
//                        Toast.makeText(MainActivity.this,"Not Found", Toast.LENGTH_SHORT).show();
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
        customAdapter.notifyDataSetChanged();





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

//                Log.e("File path", FilePath);

                if (FilePath.contains("/root_path"))
                    FilePath = FilePath.replace("/root_path", "");

//                Log.e("New File path", FilePath);

                try {
                    if (resultCode == RESULT_OK) {
                        AssetManager am = this.getAssets();
                        InputStream inStream;
                        Workbook wb = null;

                        try {
                            inStream = new FileInputStream(FilePath);
//                            Log.e("Extension", FilePath.substring(FilePath.lastIndexOf(".")));

                            if (FilePath.substring(FilePath.lastIndexOf(".")).equals(".xls")) {
//                                Log.e("File Type", "Selected file is XLS");
                                wb = new HSSFWorkbook(inStream);
                            } else if (FilePath.substring(FilePath.lastIndexOf(".")).equals(".xlsx")) {
//                                Log.e("File Type", "Selected file is XLSX");
                                wb = new XSSFWorkbook(inStream);
                            } else {
                                wb = null;
                                lbl.setText("Please select a valid Excel file");
                                return;

                            }

                            inStream.close();
                        } catch (IOException e) {
                            lbl.setText("First " + e.getMessage());
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
                    lbl.setText(ex.getMessage() + "Second");
//                    Log.e("POI Error", ex.getMessage());
                }

                SQLiteDatabase mydatabase=openOrCreateDatabase("MyDB1.db",MODE_PRIVATE,null);
                mydatabase.execSQL("DELETE FROM " + Inventory+ " WHERE "+ProductNamesql+"='"+value+"'");

                Intent i = new Intent(MainActivity.this,MainActivity.class);
                startActivity(i);
                    finish();

        }
    }
    /////////////////////vvvvvvvvvvvvvvvvvvvvvvFROM INVENTORY ACTIVITY IN RFIDTESTvvvvvvvvvvvvvvvvvvv////////////////////////

    public View.OnClickListener buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = 0;
            switch(v.getId()) {
                case R.id.connect_BT:
                    id = 0;
                    break;

            }
            selectItem(id);
        }
    };

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        if (mIsConnected)
            menu.getItem(0).setVisible(true);
        else
            menu.getItem(0).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_connected) {
//            Toast.makeText(this, getString(R.string.sled_connected_str), Toast.LENGTH_SHORT).show();
        }
        else if (id == R.id.action_home) {
            switchToHome();
        }


        return super.onOptionsItemSelected(item);
    }


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
//        mDrawerList.setItemChecked(position, true);
//        setTitle(mFunctionsString[position]);
//        mDrawerLayout.closeDrawer(mDrawerList);
        mUILayout.setVisibility(View.GONE);
    }

    @Override
    public void setTitle(CharSequence title) {
//        getActionBar().setTitle(title);
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
        }
        catch (java.lang.IllegalStateException e) {
        }
        return;
    }

    private void updateConnectState() {
        if (mReader.BT_GetConnectState() == SDConsts.BTConnectState.CONNECTED){
            mIsConnected = true;
            mConnectButton.setBackgroundResource(R.drawable.bluetoothconnected);
        }
        else {
            mIsConnected = false;
            mConnectButton.setBackgroundResource(R.drawable.bluetoothnotconnected);
        }
        invalidateOptionsMenu();
    }

    private static class UpdateConnectHandler extends Handler {
        private final WeakReference<MainActivity> mExecutor;
        public UpdateConnectHandler(MainActivity ac) {
            mExecutor = new WeakReference<>(ac);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity executor = mExecutor.get();
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




    /////////////////////////////////////////////////////////////////////

    private OnItemSelectedListener sessionListener = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (position > 0)
                Toast.makeText(mContext, "If you want to use session 1 ~ 3 value, toggle off", Toast.LENGTH_SHORT).show();
            mReader.RF_SetSession(position);
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    private OnItemSelectedListener selFlagListener = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            mReader.RF_SetSelectionFlag(position + 1);
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private AdapterView.OnItemClickListener listItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ListItem i = (ListItem)mRfidList.getItemAtPosition(position);
            mLocateTag = i.mUt;
            mLocateStartPos = (i.mHasPc ? 0 : 4);
            if (i.mHasPc)
                mLocateEPC = mLocateTag.substring(4);
            else
                mLocateEPC = mLocateTag;

            AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
            alert.setTitle(getString(R.string.locating_str));
            alert.setMessage(getString(R.string.want_tracking_str));

            alert.setPositiveButton(getString(R.string.yes_str), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    SelectionCriterias s = new SelectionCriterias();
                    s.makeCriteria(SelectionCriterias.SCMemType.EPC, mLocateTag,
                            mLocateStartPos, mLocateTag.length() * 4,
                            SelectionCriterias.SCActionType.ASLINVA_DSLINVB);
                    mReader.RF_SetSelection(s);
//                    switchLayout(false);
                    mLocateTv.setText(mLocateTag);
                }
            });
            alert.setNegativeButton(getString(R.string.no_str) ,new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });
            alert.show();
        }
    };

    private void switchLayout(boolean showList) {
        mLocate = !showList;
//        if (mLocate)
//            mReader.RF_SetRssiTrackingState(SDConsts.RFRssi.ON);
        if (showList) {
            mListLayout.setVisibility(View.VISIBLE);
            mLocateLayout.setVisibility(View.GONE);
            mInvenButton.setText(R.string.inven_str);
            mStopInvenButton.setText(R.string.stop_inven_str);
        }
        else {
            mTagLocateProgress.setProgress(0);
            mListLayout.setVisibility(View.GONE);
            mLocateLayout.setVisibility(View.VISIBLE);
            mInvenButton.setText(R.string.track_str);
            mStopInvenButton.setText(R.string.stop_track_str);
        }
    }
    private void createSoundPool() {
        boolean b = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            b = createNewSoundPool();
        else
            b = createOldSoundPool();
        if (b) {
            AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            float actVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            float maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            mSoundVolume = actVolume / maxVolume;
            SoundLoadListener();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private boolean createNewSoundPool(){
        AudioAttributes attributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build();
        mSoundPool = new SoundPool.Builder().setAudioAttributes(attributes).setMaxStreams(5).build();
        return mSoundPool != null;
    }

    @SuppressWarnings("deprecation")
    private boolean createOldSoundPool(){
        mSoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        return mSoundPool != null;
    }

    private void SoundLoadListener() {
        if (mSoundPool != null) {
            mSoundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    // TODO Auto-generated method stub
                    mSoundFileLoadState = true;
                }
            });
            mSoundId = mSoundPool.load(mContext, R.raw.beep, 1);
        }
    }

    private class SoundTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            if (mLocate)
                mTagLocateProgress.setProgress(mLocateValue);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            if (mSoundPlay) {
                try {
                    if (mSoundFileLoadState) {
                        if (mSoundPool != null) {
                            mSoundPool.play(mSoundId, mSoundVolume, mSoundVolume, 0, 0, (48000.0f / 44100.0f));
                            try {
                                Thread.sleep(25);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (java.lang.NullPointerException e) {
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
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
        if (mReader != null && mReader.BT_GetConnectState() == SDConsts.BTConnectState.CONNECTED) {
            enableControl(true);
            updateButtonState();
        }

        else
            enableControl(true);
        updateButtonState();
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

        updateConnectState();

        mSoundFileLoadState = false;

        createSoundPool();

        mOldTotalCount = 0;

        mOldSec = 0;



        mLocate = false;

        mInventory = false;


        addCheckListener();
        super.onStart();
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        if (D) {
//            tvLog.setText("onResume");
//            Log.d(TAG, "onResume");
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
//        this.deleteDatabase("MyDB1.db");
        super.onPause();
    }

    @Override
    public void onStop() {
        if (D) {
//            tvLog.setText("onStop");
//            Log.d(TAG, "onStop");
        }

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mReader = BTReader.getReader(mContext, mMainHandler);
        this.deleteDatabase("MyDB1.db");
        customAdapter.clearData();
        customAdapter.notifyDataSetChanged();
        tvTotal.setText("0");
        tvFound.setText("0");
        tvNotfound.setText("0");
//        SQLiteDatabase mydatabase=openOrCreateDatabase("MyDB1.db",MODE_PRIVATE,null);
//        customAdapter.notifyDataSetChanged();

        mReader.RF_StopInventory();
        pauseStopwatch();
        mInventory = false;
        if (mSoundPool != null)
            mSoundPool.release();
        mSoundFileLoadState = false;
        if (mFileManager != null) {
            mFileManager.closeFile();
            mFileManager = null;
        }
        stopStopwatch();

        unbindStopwatchSvc();
        super.onStop();
    }


    private OnClickListener clearButtonListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (D) {
//                tvLog.setText("clearButtonListener");
//                Log.d(TAG, "clearButtonListener");
            }
            clearAll();
        }
    };

    private void clearAll() {
        if (!mInventory) {
            mAdapter.removeAllItem();

            updateCountText();

            stopStopwatch();

            mOldTotalCount = 0;

            mOldSec = 0;

            updateSpeedCountText();

            updateAvrSpeedCountText();

            Activity activity = MainActivity.this;
            if (activity != null)
                mSpeedCountText.setText("0" + activity.getString(R.string.speed_postfix_str));
        }
    }

    private OnClickListener sledListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub


            if (D) {
//                tvLog.setText("CountListener");
//                Log.d(TAG, "stopwatchListener");
//                Toast.makeText(MainActivity.this,"stopwatchlistener",Toast.LENGTH_SHORT).show();
            }

            int id = v.getId();
            int ret;
            switch (id) {
                case R.id.back_button:
                    ret = mReader.RF_StopInventory();
                    if (ret == SDConsts.RFResult.SUCCESS || ret == SDConsts.RFResult.NOT_INVENTORY_STATE) {
                        mInventory = false;
                        enableControl(!mInventory);
                        pauseStopwatch();
                    } else if (ret == SDConsts.RFResult.STOP_FAILED_TRY_AGAIN)
                        Toast.makeText(mContext, "Stop Inventory failed", Toast.LENGTH_SHORT).show();

                    switchLayout(true);
                    mLocateTv.setText("");
                    mLocateTag = null;
                    clearAll();
                    break;
                case R.id.inven_button:
                    onStart();
                    if (!mInventory) {
                        clearAll();
                        openFile();
                        if (mLocate) {
                            //mCurrentPower = mReader.RF_GetRadioPowerState();
                            ret = mReader.RF_PerformInventoryForLocating(mLocateEPC);
                        } else
                            ret = mReader.RF_PerformInventoryWithLocating(mIsTurbo, mMask, mIgnorePC);
                        if (ret == SDConsts.RFResult.SUCCESS) {
                            startStopwatch();
                            mInventory = true;
                            enableControl(!mInventory);
                        } else if (ret == SDConsts.RFResult.MODE_ERROR)
                            Toast.makeText(mContext, "Start Inventory failed, Please check RFR900 MODE", Toast.LENGTH_SHORT).show();
                        else if (ret == SDConsts.RFResult.LOW_BATTERY)
                            Toast.makeText(mContext, "Start Inventory failed, LOW_BATTERY", Toast.LENGTH_SHORT).show();
                        else
                        if (D) {
//                            tvLog.setText("Start Inventory Failed");
//                            Log.d(TAG, "Start Inventory failed");
                        }
                    }
                    break;

                case R.id.stop_inven_button:
                    ret = mReader.RF_StopInventory();
                    if (ret == SDConsts.RFResult.SUCCESS || ret == SDConsts.RFResult.NOT_INVENTORY_STATE) {
                        mInventory = false;
                        enableControl(!mInventory);
                        pauseStopwatch();
                    } else if (ret == SDConsts.RFResult.STOP_FAILED_TRY_AGAIN)
                        Toast.makeText(mContext, "Stop Inventory failed", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void openFile() {
        if (mFile && !mInventory && !mLocate) {
            if (mFileManager == null)
                mFileManager = new FileManager(mContext);
            mFileManager.openFile();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (D) {
//            tvLog.setText("onRequestPermissionsResult");
//            Log.d(TAG, "onRequestPermissionsResult");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mCurrentFragment != null)
                mCurrentFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            switch (requestCode) {
                case PermissionHelper.REQ_PERMISSION_CODE:
                    if (permissions != null) {
                        boolean hasResult = false;
                        for (String p : permissions) {
                            if (p.equals(PermissionHelper.mStoragePerms[0])) {
                                hasResult = true;
                                break;
                            }
                        }
                        if (hasResult) {
                            mFile = grantResults != null && grantResults.length != 0 &&
                                    grantResults[0] == PackageManager.PERMISSION_GRANTED;
                        }
                    }
                    break;
            }
            mFileSwitch.setChecked(mFile);
        }
    }

    private OnCheckedChangeListener sledcheckListener = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // TODO Auto-generated method stub
            int id = buttonView.getId();
            switch (id) {
                case R.id.turbo_switch:
                    mIsTurbo = isChecked;
                    break;

                case R.id.file_switch:
                    if (isChecked) {
                        boolean b = PermissionHelper.checkPermission(mContext,PermissionHelper.mStoragePerms[0]);
                        if (!b)
                            PermissionHelper.requestPermission(MainActivity.this, PermissionHelper.mStoragePerms);
                        else
                            mFile = true;
                    }
                    else {
                        mFile = false;
                        if (mFileManager != null) {
                            mFileManager.closeFile();
                            mFileManager = null;
                        }
                    }
                    break;

                case R.id.rssi_switch:
                    if (isChecked) {
                        if (mReader.RF_SetRssiTrackingState(SDConsts.RFRssi.ON) == SDConsts.RFResult.SUCCESS)
                            mRssi = true;
                    }
                    else {
                        if (mReader.RF_SetRssiTrackingState(SDConsts.RFRssi.OFF) == SDConsts.RFResult.SUCCESS)
                            mRssi = false;
                    }
                    break;

                case R.id.filter_switch:
                    clearAll();
                    mTagFilter = isChecked;
                    break;

                case R.id.sound_switch:
                    mSoundPlay = isChecked;
                    break;

                case R.id.mask_switch:
                    mMask = isChecked;
                    break;

                case R.id.toggle_switch:
                    if (isChecked) {
                        if (mReader.RF_SetToggle(SDConsts.RFToggle.ON) == SDConsts.RFResult.SUCCESS)
                            mToggle = true;
                    }
                    else {
                        if (mReader.RF_SetToggle(SDConsts.RFToggle.OFF) == SDConsts.RFResult.SUCCESS)
                            mToggle = false;
                    }
                    break;

                case R.id.pc_switch:
                    mIgnorePC = isChecked;
                    break;
            }
        }
    };

    private void startStopwatch() {
        if (D) {
//            Log.d(TAG, "startStopwatch");
//            tvLog.setText("startStopwatch");
        }

        if (mStopwatchSvc != null && !mStopwatchSvc.isRunning())
            mStopwatchSvc.start();

        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void pauseStopwatch() {
        if (D) {
//            Log.d(TAG, "pauseStopwatch");
//            tvLog.setText("pauseStopwatch");
        }

        if (mStopwatchSvc != null && mStopwatchSvc.isRunning())
            mStopwatchSvc.pause();

        updateCountText();

        updateTimerText();

        updateSpeedCountText();

        updateAvrSpeedCountText();

        mProgressBar.setVisibility(View.INVISIBLE);
    }

    private void stopStopwatch() {
        if (D) {
//            Log.d(TAG, "stopStopwatch");
//            tvLog.setText("stopStopwatch");
        }

        if (mStopwatchSvc != null && mStopwatchSvc.isRunning())
            mStopwatchSvc.pause();

        if (mStopwatchSvc != null)
            mStopwatchSvc.reset();

        updateTimerText();

        updateAvrSpeedCountText();

        mProgressBar.setVisibility(View.INVISIBLE);
    }

    private void updateCountText() {
        if (D) {
//            Log.d(TAG, "updateCountText");
//            tvLog.setText("updateCountText");
        }
        String text = Integer.toString(mAdapter.getCount());
        mCountText.setText(text);
    }

    private void updateTimerText() {
        if (D) {
//            Log.d(TAG, "updateTimerText");
//            tvLog.setText("updateTimerText");
        }
        if (mStopwatchSvc != null)
            mTimerText.setText(mStopwatchSvc.getFormattedElapsedTime());
    }

    private void updateSpeedCountText() {
        if (D) {
//            Log.d(TAG, "updateSpeedCountText");
//            tvLog.setText("updateSpeedCountText");
        }
        String speedStr = "";
        double value = 0;
        double totalCount = 0;
        double sec = 0;
        if (mStopwatchSvc != null) {
            sec = ((double)((int)(mStopwatchSvc.getElapsedTime() / 100))) / 10;

            if (!mTagFilter)
                totalCount = mAdapter.getTotalCount();
            else {
                totalCount = mAdapter.getTotalCount();
                for (int i = 0 ; i < mAdapter.getCount(); i++)
                    totalCount += mAdapter.getItemDupCount(i);
            }
            if (totalCount > 0 && sec - mOldSec >= 1) {
                value = (double)((int)(((totalCount - mOldTotalCount) / (sec - mOldSec)) * 10)) / 10;

                mOldTotalCount = totalCount;

                mOldSec = sec;
                Activity activity = MainActivity.this;
                if (activity != null)
                    speedStr = value + activity.getString(R.string.speed_postfix_str);
                mSpeedCountText.setText(speedStr);
            }
        }
    }

    private void updateAvrSpeedCountText() {
        if (D) {
//            Log.d(TAG, "updateAvrSpeedCountText");
//            tvLog.setText("updateAvrSpeedCountText");
        }
        String speedStr = "";
        double value = 0;
        int totalCount = 0;
        double sec = 0;
        if (mStopwatchSvc != null) {
            sec = ((double)((int)(mStopwatchSvc.getElapsedTime() / 100))) / 10;

            if (!mTagFilter)
                totalCount = mAdapter.getTotalCount();
            else {
                totalCount = mAdapter.getTotalCount();
                for (int i = 0 ; i < mAdapter.getCount(); i++)
                    totalCount += mAdapter.getItemDupCount(i);
            }
            if (totalCount > 0 && sec >= 1)
                value = (double)((int)(((double)totalCount / sec) * 10)) / 10;

            Activity activity = MainActivity.this;
            if (activity != null)
                speedStr = value + activity.getString(R.string.speed_postfix_str);
            mAvrSpeedCountTest.setText(speedStr);
        }
    }

    private void enableControl(boolean b) {
        if (b)
            mRfidList.setOnItemClickListener(listItemClickListener);
        else
            mRfidList.setOnItemClickListener(null);
        mTurboSwitch.setEnabled(b);
        mRssiSwitch.setEnabled(b);
        mFilterSwitch.setEnabled(b);
        mSoundSwitch.setEnabled(b);
        mMaskSwitch.setEnabled(b);
        mToggleSwitch.setEnabled(b);
        mPCSwitch.setEnabled(b);
        mFileSwitch.setEnabled(b);
        mSessionSpin.setEnabled(b);
        mSelFlagSpin.setEnabled(b);
        mBackButton.setVisibility(b ? View.VISIBLE : View.INVISIBLE);

    }

    private ServiceConnection mStopwatchSvcConnection= new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            // TODO Auto-generated method stub
            if (D) {
//                Log.d(TAG, "onServiceConnected");
//                tvLog.setText("onServiceConnected");
            }

            mStopwatchSvc = ((StopwatchService.LocalBinder)arg1).getService(mUpdateStopwatchHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            // TODO Auto-generated method stub
            if (D) {
//                Log.d(TAG, "onServiceDisconnected");
//                tvLog.setText("onServiceDisconnected");
            }

            mStopwatchSvc = null;
        }
    };

    private void bindStopwatchSvc() {
        if (D) {
//            Log.d(TAG, "bindStopwatchSvc");
//            tvLog.setText("bindStopwatchSvc");
        }
        mContext.bindService(new Intent(mContext, StopwatchService.class), mStopwatchSvcConnection, Context.BIND_AUTO_CREATE);
    }

    private void unbindStopwatchSvc() {
        if (D) {
//            Log.d(TAG, "unbindStopwatchSvc");
//            tvLog.setText("unbindStopwatchSvc");
        }
        try {
            if (mStopwatchSvc != null)
                mContext.unbindService(mStopwatchSvcConnection);
        }
        catch (java.lang.IllegalArgumentException iae) {
            return;
        }
    }

    private static class UpdateStopwatchHandler extends Handler {
        private final WeakReference<MainActivity> mExecutor;
        public UpdateStopwatchHandler(MainActivity f) {
            mExecutor = new WeakReference<>(f);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity executor = mExecutor.get();
            if (executor != null) {
                executor.handleUpdateStopwatchHandler(msg);
            }
        }
    }

    public void handleUpdateStopwatchHandler(Message m) {
        if (D) {
//            Log.d(TAG, "mUpdateStopwatchHandler");
//            tvLog.setText("mUpdateStopwatchHandler");
        }
        if (m.what == StopwatchService.TICK_WHAT) {
            if (D) {
//                Log.d(TAG, "received stopwatch message");
//                tvLog.setText("received stopwatch message");
            }

            mTickCount++;

            updateCountText();

            updateSpeedCountText();

            if (mTickCount == 10) {
                updateAvrSpeedCountText();
                mTickCount = 0;
            }
            updateTimerText();

            mStopwatchSvc.update();

            mRfidList.setSelection(mRfidList.getAdapter().getCount() - 1);

        }
    }

    private static class MainHandler extends Handler {
        private final WeakReference<MainActivity> mExecutor;
        public MainHandler(MainActivity f) {
            mExecutor = new WeakReference<>(f);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity executor = mExecutor.get();
            if (executor != null) {
                executor.handleMainHandler(msg);
            }
        }
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
                    case SDConsts.SDCmdMsg.TRIGGER_PRESSED:
                        if (!mInventory) {
                            clearAll();
                            openFile();
                            //+++NTNS
                            int ret;
                            if (mLocate) {
                                //mCurrentPower = mReader.RF_GetRadioPowerState();
                                ret = mReader.RF_PerformInventoryForLocating(mLocateEPC);
                            } else
                                ret = mReader.RF_PerformInventory(mIsTurbo, mMask, mIgnorePC);
                            if (ret == SDConsts.RFResult.SUCCESS) {
                                startStopwatch();
                                mInventory = true;
                                enableControl(!mInventory);
                            } else if (ret == SDConsts.RFResult.MODE_ERROR)
                                Toast.makeText(mContext, "Start Inventory failed, Please check RFR900 MODE", Toast.LENGTH_SHORT).show();
                            else if (ret == SDConsts.RFResult.LOW_BATTERY)
                                Toast.makeText(mContext, "Start Inventory failed, LOW_BATTERY", Toast.LENGTH_SHORT).show();
                            else
                            if (D) {
//                                Log.d(TAG, "Start Inventory failed");
//                                tvLog.setText("Start Inventory failed");
                            }
                        }
                        break;

                    case SDConsts.SDCmdMsg.SLED_INVENTORY_STATE_CHANGED:
                        mInventory = false;
                        enableControl(!mInventory);
                        pauseStopwatch();
                        // In case of low battery on inventory, reason value is LOW_BATTERY
                        Toast.makeText(mContext, "Inventory Stopped reason : " + m.arg2, Toast.LENGTH_SHORT).show();

                        mAdapter.addItem(-1, "Inventory Stopped reason : " + m.arg2,  Integer.toString(m.arg2), !mIgnorePC, mTagFilter);
                        break;

                    case SDConsts.SDCmdMsg.TRIGGER_RELEASED:
                        if (mReader.RF_StopInventory() == SDConsts.SDResult.SUCCESS) {
                            mInventory = false;
                            enableControl(!mInventory);
                        }
                        pauseStopwatch();
                        break;

                    //You can receive this message every a minute. SDConsts.SDCmdMsg.SLED_BATTERY_STATE_CHANGED
                    case SDConsts.SDCmdMsg.SLED_BATTERY_STATE_CHANGED:
                        //Toast.makeText(mContext, "Battery state = " + m.arg2, Toast.LENGTH_SHORT).show();
                        if (D) {
//                            Log.d(TAG, "Battery state = " + m.arg2);
//                            tvLog.setText("Battery state = " + m.arg2);
                        }
                        mBatteryText.setText("" + m.arg2 + "%");
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

            case SDConsts.Msg.RFMsg:
                switch(m.arg1) {
                    case SDConsts.RFCmdMsg.INVENTORY:
                    case SDConsts.RFCmdMsg.READ:
                        if (m.arg2 == SDConsts.RFResult.SUCCESS) {
                            if (m.obj != null  && m.obj instanceof String) {
                                String data = (String) m.obj;
                                if (data != null)
                                    processReadData(data);
                            }
                        }
                        break;
                    case SDConsts.RFCmdMsg.LOCATE:
                        if (m.arg2 == SDConsts.RFResult.SUCCESS) {
                            if (m.obj != null  && m.obj instanceof Integer)
                                processLocateData((int) m.obj);
                        }
                        break;
                }
                break;
            case SDConsts.Msg.BTMsg:
                if (m.arg1 == SDConsts.BTCmdMsg.SLED_BT_CONNECTION_STATE_CHANGED) {
                    if (D) {
//                        Log.d(TAG, "SLED_BT_CONNECTION_STATE_CHANGED = " + m.arg2);
//                        tvLog.setText("SLED_BT_CONNECTION_STATE_CHANGED = " + m.arg2);
                    }
                    if (mReader.BT_GetConnectState() != SDConsts.BTConnectState.CONNECTED) {
                        if (mInventory) {
                            pauseStopwatch();
                            mInventory = false;
                        }
                        enableControl(false);
                    }
                    if (mOptionHandler != null)
                        mOptionHandler.obtainMessage(MainActivity.MSG_OPTION_CONNECT_STATE_CHANGED).sendToTarget();
                }
                else if  (m.arg1 == SDConsts.BTCmdMsg.SLED_BT_DISCONNECTED || m.arg1 == SDConsts.BTCmdMsg.SLED_BT_CONNECTION_LOST) {
                    if (mInventory) {
                        pauseStopwatch();
                        mInventory = false;
                    }
                    enableControl(false);
                    if (mOptionHandler != null)
                        mOptionHandler.obtainMessage(MainActivity.MSG_OPTION_CONNECT_STATE_CHANGED).sendToTarget();
                }
                break;
        }
    }

    private void processLocateData(int data) {
        startLocateTimer();
        mLocateValue = data;
        //mTagLocateProgress.setProgress(data);
        if (mSoundTask == null) {
            mSoundTask = new SoundTask();
            mSoundTask.execute();
        }
        else {
            if (mSoundTask.getStatus() == AsyncTask.Status.FINISHED) {
                mSoundTask.cancel(true);
                mSoundTask = null;
                mSoundTask = new SoundTask();
                mSoundTask.execute();
            }
        }
    }

    private void processReadData(String data) {
        //updateCountText();
        StringBuilder tagSb = new StringBuilder();
        tagSb.setLength(0);
        String info = "";
        String originalData = data;
        if (data.contains(";")) {
            if (D) {
//                Log.d(TAG, "full tag = " + data);
//                tvLog.setText("full tag = " + data);
            }
            //full tag example(with optional value)
            //1) RF_PerformInventory => "3000123456783333444455556666;rssi:-54.8"
            //2) RF_PerformInventoryWithLocating => "3000123456783333444455556666;loc:64"
            int infoTagPoint = data.indexOf(';');
            info = data.substring(infoTagPoint);
            int infoPoint = info.indexOf(':') + 1;
            info = info.substring(infoPoint);
            if (D) {
//                Log.d(TAG, "info tag = " + info);
//                tvLog.setText("info tag = " + info);
            }
            data = data.substring(4, infoTagPoint);
            tvRfidno.setText(data);

                if(ProductNo.contains(data)){
                    int index = ProductNo.indexOf(data);
//                    Toast.makeText(MainActivity.this,"Found", Toast.LENGTH_SHORT).show();
//                    Log.i(TAG, "Reader opene");
                    SQLiteDatabase mydatabase=openOrCreateDatabase("MyDB1.db",MODE_PRIVATE,null);
                    String sql = "UPDATE "+ Inventory +" SET Found = '1' WHERE ProductNo = '"+data+"'";
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
//                    Log.d(TAG, "not found");
                }





            if (D) {
//                Log.d(TAG, "data tag = " + data);
//                tvLog.setText("data tag = " + data);
            }
        }

        if (info != "") {
            Activity activity = MainActivity.this;
            String prefix = "";
            if (originalData.contains("rssi")) {
                if (activity != null)
                    prefix = activity.getString(R.string.rssi_str);
            }
            else if (originalData.contains("loc")){
                if (activity != null)
                    prefix = activity.getString(R.string.loc_str);
            }
            if (activity != null)
                info = prefix + info;
        }
//        mAdapter.addItem(-1, data, info, !mIgnorePC, mTagFilter);


        if (mFileManager != null && mFile)
            mFileManager.writeToFile(data);

        mRfidList.setSelection(mRfidList.getAdapter().getCount() - 1);
        if (!mInventory) {
            updateCountText();
            updateSpeedCountText();
            updateAvrSpeedCountText();
        }

        if (mSoundTask == null) {
            mSoundTask = new SoundTask();
            mSoundTask.execute();
        }
        else {
            if (mSoundTask.getStatus() == AsyncTask.Status.FINISHED) {
                mSoundTask.cancel(true);
                mSoundTask = null;
                mSoundTask = new MainActivity.SoundTask();
                mSoundTask.execute();
            }
        }
    }

    private void addCheckListener() {
        if (mTurboSwitch != null)
            mTurboSwitch.setOnCheckedChangeListener(sledcheckListener);

        if (mRssiSwitch != null)
            mRssiSwitch.setOnCheckedChangeListener(sledcheckListener);

        if (mFilterSwitch != null)
            mFilterSwitch.setOnCheckedChangeListener(sledcheckListener);

        if (mSoundSwitch != null)
            mSoundSwitch.setOnCheckedChangeListener(sledcheckListener);

        if (mMaskSwitch != null)
            mMaskSwitch.setOnCheckedChangeListener(sledcheckListener);

        if (mToggleSwitch != null)
            mToggleSwitch.setOnCheckedChangeListener(sledcheckListener);

        if (mPCSwitch != null)
            mPCSwitch.setOnCheckedChangeListener(sledcheckListener);

        if (mFileSwitch != null)
            mFileSwitch.setOnCheckedChangeListener(sledcheckListener);

        if (mSessionSpin != null)
            mSessionSpin.setOnItemSelectedListener(sessionListener);

        if (mSelFlagSpin != null)
            mSelFlagSpin.setOnItemSelectedListener(selFlagListener);
    }

    private void updateButtonState() {
        mPCSwitch.setChecked(mIgnorePC);

        mFileSwitch.setChecked(mFile);

        mFilterSwitch.setChecked(mTagFilter);

        mSoundSwitch.setChecked(mSoundPlay);

        mMaskSwitch.setChecked(mMask);

        mTurboSwitch.setChecked(mIsTurbo);


        if (mReader != null) {
            int toggle = mReader.RF_GetToggle();
            mToggle = toggle == SDConsts.RFToggle.ON;
            mToggleSwitch.setChecked(mToggle);

            int session = mReader.RF_GetSession();
            if (session != mSessionSpin.getSelectedItemPosition())
                mSessionSpin.setSelection(session);

            int flag = mReader.RF_GetSelectionFlag();
            if (flag != mSelFlagSpin.getSelectedItemPosition() + 1)
                mSelFlagSpin.setSelection(flag - 1);

            int rssi = mReader.RF_GetRssiTrackingState();
            mRssi = rssi == SDConsts.RFRssi.ON;
            mRssiSwitch.setChecked(mRssi);

            int battery = mReader.SD_GetBatteryStatus();
            if (!(battery < 0 || battery > 100))
                mBatteryText.setText("" + battery + "%");

        }
    }

    private void startLocateTimer() {
        stopLocateTimer();

        mLocateTimerTask = new TimerTask() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                locateTimeout();
            }
        };
        mClearLocateTimer = new Timer();
        mClearLocateTimer.schedule(mLocateTimerTask, 500);
    }

    private void stopLocateTimer() {
        if (mClearLocateTimer != null ) {
            mClearLocateTimer.cancel();
            mClearLocateTimer = null;
        }
    }

    private void locateTimeout() {
        mTagLocateProgress.setProgress(0);
    }




}
