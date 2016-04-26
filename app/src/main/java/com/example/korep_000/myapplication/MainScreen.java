package com.example.korep_000.myapplication;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.view.View.OnClickListener;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.widget.AdapterView.OnItemClickListener;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;

public class MainScreen extends AppCompatActivity {

    ArrayList<Person> person_music = new ArrayList<Person>();
    AdapterForList adapterForList;
    JSONArray obj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adapterForList = new AdapterForList(this, person_music);
        ListView lvMain = (ListView) findViewById(R.id.lvMain);
        lvMain.setAdapter(adapterForList);
        fillData();
        lvMain.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Person pr = adapterForList.getPerson(position);
                Intent intent = new Intent(MainScreen.this, Information.class);
                intent.putExtra("name", pr.name);
                intent.putExtra("genre", pr.style);
                intent.putExtra("quant", pr.quant);
                intent.putExtra("description", pr.description);
                intent.putExtra("link", pr.link);
                intent.putExtra("big_im", pr.big_image);
                startActivity(intent);
            }
        });
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    protected void fillData() {
        try {
            File dir = getFilesDir();
            File f = new File(dir + "/text/", "data_yand");
            if(f.exists()){
                Toast toast = Toast.makeText(getApplicationContext(),
                        "ок", Toast.LENGTH_SHORT);
                toast.show();
                if(isNetworkAvailable()){
                    String firstLine = Uri.parse
                            ("http://cache-default04g.cdn.yandex.net/download.cdn.yandex.net/mobilization-2016/artists.json").getLastPathSegment();
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            openFileInput("data_yand")));
                    String line = br.readLine();
                    if(!firstLine.equals(line)){
                        doDownload();
                    }
                    br.close();
                }
            } else{
                Toast toast = Toast.makeText(getApplicationContext(),
                        "файл не найден", Toast.LENGTH_SHORT);
                toast.show();
                if(isNetworkAvailable()){
                    doDownload();
                } else{
                    Toast toast1 = Toast.makeText(getApplicationContext(),
                            "NO Internet connection", Toast.LENGTH_SHORT);
                    toast1.show();
                }
            }
            File secondInputFile = new File(getFilesDir() + "/text/", "data_yand");
            InputStream secondInputStream = new BufferedInputStream(new FileInputStream(secondInputFile));
            BufferedReader br = new BufferedReader(new InputStreamReader(secondInputStream));
            String str = "";
            StringBuffer buffer = new StringBuffer();
            str = br.readLine();
            while((str = br.readLine()) != null){
                buffer.append(str);
            }
            br.close();
            String text = buffer.toString();
            try{
                JSONArray obj = new JSONArray(text);
                for(int i = 0; i < obj.length(); ++i){
                    JSONObject title = (JSONObject)obj.get(i);
                    int id = -1;
                    if (title.has("id")){
                        id = (Integer) title.get("id");
                    }
                    String name = "";
                    if (title.has("name")){
                        name = title.get("name").toString();
                    }
                    String small = "";
                    String big = "";
                    if (title.has("cover")){
                        JSONObject cover = (JSONObject)title.get("cover");
                        if (cover.has("small")){
                            small = cover.get("small").toString();
                        }
                        if (cover.has("big")){
                            big = cover.get("big").toString();
                        }
                    }
                    StringBuffer buffer1 = new StringBuffer();
                    if (title.has("genres")){
                        JSONArray genre = (JSONArray)title.get("genres");
                        for(int j = 0; j < genre.length(); ++j){
                            if (j == genre.length() - 1) {
                                buffer1.append(genre.get(j).toString());
                            } else{
                                buffer1.append(genre.get(j).toString() + ", ");
                            }
                        }
                    } else {
                        buffer1.append("");
                    }
                    String genre = buffer1.toString();
                    String album = "";
                    String tracks = "";
                    if (title.has("tracks")){
                        tracks = title.get("tracks").toString();
                    }
                    if (title.has("albums")){
                        album = title.get("albums").toString();
                    }
                    String link = "";
                    if (title.has("link")){
                        link = title.get("link").toString();
                    }
                    String description = "";
                    if (title.has("description")){
                        description = title.get("description").toString();

                    }
                    if (title.has("link")){
                        link = title.get("link").toString();
                    }
                    person_music.add(new Person(id, name, "albums" + album + ", " + "tracks" + tracks,
                            small, genre, description, link, big));
                }
            } catch (JSONException e){
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void writeF(String text, String firstLine){
        File myDir = getFilesDir();
        try {
            File secondFile = new File(myDir + "/text/", "data_yand");
            if (secondFile.getParentFile().mkdirs()) {
                secondFile.createNewFile();
                FileOutputStream fos = new FileOutputStream(secondFile);
                fos.write((firstLine + "\n" + text).getBytes());
                fos.flush();
                fos.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }

    protected void doDownload() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    String urlString = "http://cache-default05g.cdn.yandex.net/download.cdn.yandex.net/mobilization-2016/artists.json";
                    String text = readUrl(urlString);
                    String firstLine = Uri.parse(urlString).getLastPathSegment();
                    writeF(text, firstLine);
                } catch(java.lang.Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

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