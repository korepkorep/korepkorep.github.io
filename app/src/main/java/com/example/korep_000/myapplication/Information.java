package com.example.korep_000.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by korep_000 on 24.04.2016.
 */
public class Information extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        Intent intent = getIntent();

        String name = intent.getStringExtra("name");
        String genre = intent.getStringExtra("genre");
        String alb = intent.getStringExtra("quant");
        String description = intent.getStringExtra("description");
        String link = intent.getStringExtra("link");
        String big_image = intent.getStringExtra("big_im");

        setTitle(name);
        ((TextView) findViewById(R.id.genre)).setText(genre);
        ((TextView) findViewById(R.id.alb)).setText(alb);
        ((TextView) findViewById(R.id.link)).setText(link);
        ((TextView) findViewById(R.id.description)).setText(description);
        try {
            ImageView i = (ImageView) findViewById(R.id.big_icon);
            Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(big_image).getContent());
            i.setImageBitmap(bitmap);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
