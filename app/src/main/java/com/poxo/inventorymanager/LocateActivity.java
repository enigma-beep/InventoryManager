package com.poxo.inventorymanager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.FragmentTransaction;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.poxo.inventorymanager.stopwatch.StopwatchService;

import java.lang.ref.WeakReference;
import java.time.LocalDate;
import java.time.Month;
import java.util.Timer;
import java.util.TimerTask;

import co.kr.bluebird.sled.BTReader;
import co.kr.bluebird.sled.SDConsts;

public class LocateActivity extends AppCompatActivity {

    TextView tv;

    private BTReader mReader;

    private Context mContext;

    private static final String TAG = LocateActivity.class.getSimpleName();

    private static final boolean D = Constants.MAIN_D;

    public static final int MSG_OPTION_CONNECT_STATE_CHANGED = 0;

    private AccessHandler mAccessHandler = new AccessHandler(this);

    int power= -1;

    private ProgressBar mTagLocateProgress;
    private int mLocateValue;

    String mEPC;
    private StopwatchService mStopwatchSvc;
    public final LocateActivity.UpdateConnectHandler mUpdateConnectHandler = new LocateActivity.UpdateConnectHandler(this);
    ImageView mConnectImage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locate);

        mContext = this;

        mEPC = getIntent().getStringExtra("mEPC");
        tv=findViewById(R.id.textViewtest);
        tv.setText(mEPC);
        mConnectImage=findViewById(R.id.connectlocate_BT);
        mReader = BTReader.getReader(mContext, mAccessHandler);

        mTagLocateProgress = findViewById(R.id.tag_locate_progress);
        mTagLocateProgress.setProgress(0);

        power=mReader.RF_SetRadioPowerState(30);
        if(power==0){
//            Toast.makeText(MainActivity.this,"RF_PowerState changed to 30",Toast.LENGTH_SHORT).show();
        }else{
//            Toast.makeText(MainActivity.this,"RF_PowerState change failed",Toast.LENGTH_SHORT).show();
        }

    }

    private static class AccessHandler extends Handler {
        private final WeakReference<LocateActivity> mExecutor;
        public AccessHandler(LocateActivity f) {
            mExecutor = new WeakReference<>(f);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void handleMessage(Message msg) {
            LocateActivity executor = mExecutor.get();
            if (executor != null) {
                executor.handleMessage(msg);
            }
        }
    }

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

                    case SDConsts.RFCmdMsg.LOCATE:
                        if (m.arg2 == SDConsts.RFResult.SUCCESS) {
//                            if (m.obj != null  && m.obj instanceof Integer)
                                processLocateData((int) m.obj);
                        }
                        break;

                }
                break;
            case SDConsts.Msg.SDMsg:
                switch(m.arg1){
                    case SDConsts.SDCmdMsg.TRIGGER_PRESSED:
                        int ret;
                        ret = mReader.RF_PerformInventoryForLocating(mEPC);
                        if (ret == SDConsts.RFResult.SUCCESS) {
//                            startStopwatch();
                        }
                        else {
                        }
                        break;
                    case SDConsts.SDCmdMsg.TRIGGER_RELEASED:
//                            pauseStopwatch();
////                        triggerFlag=0;
                        break;
                }

                if (m.arg1 == SDConsts.SDCmdMsg.SLED_HOTSWAP_STATE_CHANGED) {
                    if (m.arg2 == SDConsts.SDHotswapState.HOTSWAP_STATE)
                        Toast.makeText(mContext, "HOTSWAP STATE CHANGED = HOTSWAP_STATE", Toast.LENGTH_SHORT).show();
                    else if (m.arg2 == SDConsts.SDHotswapState.NORMAL_STATE)
                        Toast.makeText(mContext, "HOTSWAP STATE CHANGED = NORMAL_STATE", Toast.LENGTH_SHORT).show();
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

        }
    }

    private void pauseStopwatch() {
        mStopwatchSvc.pause();
    }

    private void startStopwatch() {
        mStopwatchSvc.start();
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

        updateConnectState();
        super.onStart();
    }

    private void processLocateData(int data) {
        mLocateValue = data;
        mTagLocateProgress.setProgress(data);

    }

    private void updateConnectState() {
        if (mReader.BT_GetConnectState() == SDConsts.BTConnectState.CONNECTED){
            mConnectImage.setImageResource(R.drawable.bluetoothconnected);


        }
        else {
            mConnectImage.setImageResource(R.drawable.bluetoothnotconnected);
        }
    }

    private static class UpdateConnectHandler extends Handler {
        private final WeakReference<LocateActivity> mExecutor;
        public UpdateConnectHandler(LocateActivity ac) {
            mExecutor = new WeakReference<>(ac);
        }

        @Override
        public void handleMessage(Message msg) {
            LocateActivity executor = mExecutor.get();
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
        else {

        }
    }


}