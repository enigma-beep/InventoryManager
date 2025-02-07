package com.poxo.inventorymanager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;


public class Splash extends AppCompatActivity {
    int viewCount=0;
    int vcc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }


        SQLiteDatabase mydatabase=openOrCreateDatabase("view.db",MODE_PRIVATE,null);
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS VC(ViewCount int(20));");
        mydatabase.execSQL("INSERT INTO VC VALUES('"+viewCount+"');");
        String vc = "SELECT ViewCount FROM VC";
        Cursor c = mydatabase.rawQuery(vc, null);
        c.moveToFirst();

        vcc=c.getInt(0);
        viewCount=vcc+1;
        mydatabase.execSQL("UPDATE  VC SET ViewCount='"+viewCount+"'");

        c.close();

//        this.deleteDatabase("MyDB1.db");
//        customAdapter.clearData();
//        customAdapter.notifyDataSetChanged();
//        tvTotal.setText("0");
//        tvFound.setText("0");
//        tvNotfound.setText("0");


        Thread timer=new Thread()
        {
            public void run() {
                try {
                    sleep(2500);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                finally
                {
                    if(viewCount==1) {
                        viewCount++;
                        Intent i = new Intent(Splash.this, FileChooserActivity.class);
                        finish();
                        startActivity(i);
                    }
                    else{
                        Intent i = new Intent(Splash.this, FileChooserActivity.class);
                        finish();
                        startActivity(i);
                    }
                }
            }
        };
        timer.start();


    }
}
