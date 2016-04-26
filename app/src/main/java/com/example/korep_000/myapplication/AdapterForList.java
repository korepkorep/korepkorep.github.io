package com.example.korep_000.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by korep_000 on 25.04.2016.
 */

public class AdapterForList extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<Person> objects;

    AdapterForList(Context context, ArrayList<Person> person_music) {
        ctx = context;
        objects = person_music;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    // кол-во элементов
    @Override
    public int getCount() {
        return objects.size();
    }

    // элемент по позиции
    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    // id по позиции
    @Override
    public long getItemId(int position) {
        return position;
    }
    //члеовек по позиции
    Person getPerson(int position) {
        return ((Person) getItem(position));
    }
    // пункт списка
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // используем созданные, но не используемые view
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.item, parent, false);
        }

        Person p = getPerson(position);

        // заполняем View в пункте списка данными из товаров: наименование, цена
        // и картинка
        ((TextView) view.findViewById(R.id.firstLine)).setText(p.name);
        ((TextView) view.findViewById(R.id.secondLine)).setText(p.style);
        ((TextView) view.findViewById(R.id.thirdLine)).setText(p.quant);
        try {
            ImageView i = (ImageView) view.findViewById(R.id.icon);
            Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(p.url).getContent());
            i.setImageBitmap(bitmap);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return view;
    }

}
