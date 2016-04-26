package com.example.korep_000.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyService extends Service {

    public void onCreate(){
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, int startId){
        Toast toast2 = Toast.makeText(getApplicationContext(),
                "service is launched", Toast.LENGTH_SHORT);
        toast2.show();
        someTask();
        return Service.START_STICKY;
    }

    void someTask(){
        new Thread(new Runnable() {
            public void run() {
                File sdPath = Environment.getExternalStorageDirectory();
                sdPath = new File(sdPath.getAbsolutePath() + "/" + "data_files");
                sdPath.mkdirs();
                File sdFile = new File(sdPath, "data");
                TelephonyManager telephonyManager = (TelephonyManager)
                        getSystemService(android.content.Context.TELEPHONY_SERVICE);
                onCellLocationChanged(sdFile, telephonyManager);
                stopSelf();
            }
        }).start();
    }
    void onCellLocationChanged(File sdFile, TelephonyManager telephonyManager){
        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String dateString = time.format(date);
        if (telephonyManager.getCellLocation() instanceof GsmCellLocation) {
            GsmCellLocation gsmCell = (GsmCellLocation) telephonyManager.getCellLocation();
            String cid = Integer.toString(gsmCell.getCid());
            String lac = Integer.toString(gsmCell.getLac());
            String operator = telephonyManager.getNetworkOperator();
            String mcc = operator.substring(0, 3);
            String mnc = operator.substring(3);

            writeFileSD(dateString + "  id:" + mcc + ":" + mnc + ":" + lac + ":" + cid, sdFile);
        } else {
            writeFileSD(dateString + "__ " + "no signal", sdFile);
        }
    }

    void writeFileSD(String text, File sdFile) {

        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return;
        }
        try {
            if (!sdFile.exists()){
                sdFile.createNewFile();
            }
            FileWriter wrt = new FileWriter(sdFile, true);
            try {
                wrt.append(text + "\n");
            } finally {
                wrt.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
