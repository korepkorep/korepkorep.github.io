package com.example.korep_000.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements OnTouchListener {

    private MyReceiver receiver = new MyReceiver();
    ImageView imageView;
    TextView text;
    private float starting_x=0;
    private float starting_y=0;
    private float scroll_x=0;
    private float scroll_y=0;
    String station_now = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button myButton = (Button) findViewById(R.id.button);
        myButton.setOnClickListener(station);
        this.imageView = (ImageView)this.findViewById(R.id.imageView);
        imageView.setOnTouchListener(this);
        text = (TextView) findViewById(R.id.textView);
        //checkLocation();
    }

    public static final int IDM_BOOl_GROUP = 400;
    public static final int IDM_BOOL_YES = 401;
    public static final int IDM_BOOL_NO = 402;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        Toast.makeText(getApplicationContext(),
                "Menu has been created", Toast.LENGTH_LONG).show();
        /*menu.add(IDM_BOOl_GROUP, IDM_BOOL_YES, Menu.NONE, "да");
        menu.add(IDM_BOOl_GROUP, IDM_BOOL_NO, Menu.NONE, "нет");
        menu.setGroupCheckable(IDM_BOOl_GROUP, false, true);*/
        //return super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_location:
                Toast.makeText(getApplicationContext(),
                        "You selected location", Toast.LENGTH_LONG).show();
                return true;

        }
        return super.onOptionsItemSelected(item);
        /*int id = item.getItemId();
        switch (id) {
            case IDM_BOOL_YES:
                if(!item.isChecked()){
                    item.setChecked(!item.isChecked());
                    return true;
                } else {
                    return false;
                }
            case IDM_BOOL_NO:
                if(item.isChecked()){
                    item.setChecked(!item.isChecked());
                    return true;
                } else {
                    return false;
                }
            default:
                return false;
        }*/
    }

    OnClickListener station = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    station_now, Toast.LENGTH_SHORT);
            toast.show();
            if(station_now != ""){
                onCellLocationChanged(station_now);
                station_now = "";
            }
        }
    };

    public void registerBroadcastReceiver(View view) {
        this.registerReceiver(receiver, new IntentFilter(
                "android.intent.action.TIME_TICK"));
        Toast.makeText(getApplicationContext(), "Приёмник включен",
                Toast.LENGTH_SHORT).show();
    }
    public void unregisterBroadcastReceiver(View view) {
        this.unregisterReceiver(receiver);

        Toast.makeText(getApplicationContext(), "Приёмник выключён", Toast.LENGTH_SHORT)
                .show();
    }

    protected void checkLocation(){
        TelephonyManager telephonyManager = (TelephonyManager)
                getSystemService(android.content.Context.TELEPHONY_SERVICE);
        if (telephonyManager.getCellLocation() instanceof GsmCellLocation) {
            GsmCellLocation gsmCell = (GsmCellLocation) telephonyManager.getCellLocation();
            String cid = Integer.toString(gsmCell.getCid());
            String lac = Integer.toString(gsmCell.getLac());
            String operator = telephonyManager.getNetworkOperator();
            String mcc = operator.substring(0, 3);
            String mnc = operator.substring(3);
            readFileSD("id:" + mcc +
                    ":" + mnc + ":" + lac + ":" + cid);
        }
    }

    void readFileSD(String s) {
        // проверяем доступность SD
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "SD - карта не доступна", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        File sdPath = Environment.getExternalStorageDirectory();
        sdPath = new File(sdPath.getAbsolutePath() + "/" + "data_files");
        sdPath.mkdirs();
        File sdFile = new File(sdPath, "data");
        try {
            BufferedReader br = new BufferedReader(new FileReader(sdFile));
            String str = "";
            String word1 = "id:";
            String word2 = "x:";
            Map <String, String> map_id_coord = new HashMap <String, String>();
            while ((str = br.readLine()) != null) {
                int index_word1 = str.indexOf(word1);
                int index_word2 = str.indexOf(word2);
                int j = index_word1;
                String key_id = "";
                String value_coord = "";
                while(j != -1 && j != str.length() && str.charAt(j) != ' ') {
                    key_id += str.charAt(j);
                    j += 1;
                }
                j = index_word2;
                while (j != -1 && j != str.length() && str.charAt(j) != ' '){
                    value_coord += str.charAt(j);
                    j += 1;
                }
                if(key_id != "" && value_coord != ""){
                    if(!map_id_coord.containsKey(key_id)){
                        map_id_coord.put(key_id, value_coord);
                    }
                }
            }
            if(map_id_coord.containsKey(s)){
                for (Map.Entry entry : map_id_coord.entrySet()) {
                    if(entry.getKey().toString().equals(s)){
                       // entry.getValue();//координаты. на этом месте нужно что-то нарисовать
                        Toast toast2 = Toast.makeText(getApplicationContext(),
                                "yes", Toast.LENGTH_SHORT);
                        toast2.show();
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private long startTime = 0;
    private long endTime = 0;
    public boolean onTouch(View v, MotionEvent event) {
        String st = "";
        text.setText("");


        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                starting_x = event.getX();
                starting_y = event.getY();
                station_now = "";
                break;
            case MotionEvent.ACTION_UP:
                endTime = event.getEventTime();
                break;
            case MotionEvent.ACTION_MOVE:
                station_now = "";
                float x = event.getX();
                float y = event.getY();
                scroll_x = x - starting_x;
                scroll_y = y - starting_y;
                starting_x = starting_x + scroll_x;
                starting_y = starting_y + scroll_y;
                endTime = 0;
                break;
        }
        File sdPath = Environment.getExternalStorageDirectory();
        sdPath = new File(sdPath.getAbsolutePath() + "/" + "data_files");
        sdPath.mkdirs();
        File sdFile = new File(sdPath, "data");
        TelephonyManager telephonyManager = (TelephonyManager)
                getSystemService(android.content.Context.TELEPHONY_SERVICE);
        if (endTime - startTime >= 5000) {
            if ((1920 - (starting_x + scroll_x)) * (1920 - (starting_x + scroll_x))
                    + (858 - (starting_y + scroll_y)) * (858 - (starting_y + scroll_y)) <= 50 * 50) {
                station_now = "Prospekt Mira";
                text.setText(station_now);
                station_now = station_now + " x:"
                        + Integer.toString(1920) + "y:"
                        + Integer.toString(858);
            } else if ((2278 - (starting_x + scroll_x)) * (2278 - (starting_x + scroll_x))
                    + (1119 - (starting_y + scroll_y)) * (1119 - (starting_y + scroll_y)) <= 50 * 50) {
                station_now = "Komsomolskaya";
                text.setText(station_now);
                station_now = station_now + " x:"
                        + Integer.toString(2278) + "y:"
                        + Integer.toString(1119);
            } else if ((2411 - (starting_x + scroll_x)) * (2411 - (starting_x + scroll_x))
                    + (1592 - (starting_y + scroll_y)) * (1592 - (starting_y + scroll_y)) <= 50 * 50) {
                station_now = "Kurskaya";
                text.setText(station_now);
                station_now = station_now + " x:"
                        + Integer.toString(2411) + "y:"
                        + Integer.toString(1592);
            } else if ((2279 - (starting_x + scroll_x)) * (2279 - (starting_x + scroll_x))
                    + (1962 - (starting_y + scroll_y)) * (1962 - (starting_y + scroll_y)) <= 50 * 50) {
                station_now = "Taganskaya";
                text.setText(station_now);
                station_now = station_now + " x:"
                        + Integer.toString(2279) + "y:"
                        + Integer.toString(1962);
            } else if ((1937 - (starting_x + scroll_x)) * (1937 - (starting_x + scroll_x))
                    + (2214 - (starting_y + scroll_y)) * (2214 - (starting_y + scroll_y)) <= 50 * 50) {
                station_now = "Paveletskaya";
                text.setText(station_now);
                station_now = station_now  + " x:"
                        + Integer.toString(1937) + "y:"
                        + Integer.toString(2214);
            } else if ((1688 - (starting_x + scroll_x)) * (1688 - (starting_x + scroll_x))
                    + (2258 - (starting_y + scroll_y)) * (2258 - (starting_y + scroll_y)) <= 50 * 50) {
                station_now = "Dobryninskaya";
                text.setText(station_now);
                station_now = station_now +  " x:"
                        + Integer.toString(1688) + "y:"
                        + Integer.toString(2258);
            } else if ((1433 - (starting_x + scroll_x)) * (1433 - (starting_x + scroll_x))
                    + (2210 - (starting_y + scroll_y)) * (2210 - (starting_y + scroll_y)) <= 50 * 50) {
                station_now = "Oktyabrskaya";
                text.setText(station_now);
                station_now = station_now + " x:"
                        + Integer.toString(1433) + "y:"
                        + Integer.toString(2210);
            } else if ((1064 - (starting_x + scroll_x)) * (1064 - (starting_x + scroll_x))
                    + (1880 - (starting_y + scroll_y)) * (1880 - (starting_y + scroll_y)) <= 50 * 50) {
                station_now = "Park Kultury";
                text.setText(station_now);
                station_now = station_now + " x:"
                        + Integer.toString(1064) + "y:"
                        + Integer.toString(1880);
            } else if ((981 - (starting_x + scroll_x)) * (981 - (starting_x + scroll_x))
                    + (1612 - (starting_y + scroll_y)) * (1612 - (starting_y + scroll_y)) <= 50 * 50) {
                station_now = "Kievskaya";
                text.setText(station_now);
                station_now = station_now + " x:"
                        + Integer.toString(981) + "y:"
                        + Integer.toString(1612);
            } else if ((1004 - (starting_x + scroll_x)) * (1004 - (starting_x + scroll_x))
                    + (1358 - (starting_y + scroll_y)) * (1358 - (starting_y + scroll_y)) <= 50 * 50) {
                station_now = "Krasnopresnenskaya";
                text.setText(station_now);
                station_now = station_now + " x:"
                        + Integer.toString(1004) + "y:"
                        + Integer.toString(1358);
            } else if ((1155 - (starting_x + scroll_x)) * (1155 - (starting_x + scroll_x))
                    + (1072 - (starting_y + scroll_y)) * (1072 - (starting_y + scroll_y)) <= 50 * 50) {
                station_now = "Belorusskaya";
                text.setText(station_now);
                station_now = station_now + " x:"
                        + Integer.toString(1155) + "y:"
                        + Integer.toString(1072);
            } else if ((1341 - (starting_x + scroll_x)) * (1341 - (starting_x + scroll_x))
                    + (908 - (starting_y + scroll_y)) * (908 - (starting_y + scroll_y)) <= 50 * 50) {
                station_now = "Novoslobodskaya";
                text.setText(station_now);
                station_now = station_now + " x:"
                        + Integer.toString(1341) + "y:"
                        + Integer.toString(908);
            }
        }
        return true;
    }

    void onCellLocationChanged(String station){
        File sdPath = Environment.getExternalStorageDirectory();
        sdPath = new File(sdPath.getAbsolutePath() + "/" + "data_files");
        sdPath.mkdirs();
        File sdFile = new File(sdPath, "data");
        TelephonyManager telephonyManager = (TelephonyManager)
                getSystemService(android.content.Context.TELEPHONY_SERVICE);
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

            writeFileSD(station + " " + dateString + "  id:" + mcc +
                    ":" + mnc + ":" + lac + ":" + cid + " ", sdFile);
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
}