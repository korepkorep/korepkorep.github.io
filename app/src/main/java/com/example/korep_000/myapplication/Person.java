package com.example.korep_000.myapplication;

/**
 * Created by korep_000 on 25.04.2016.
 */
public class Person {
    int id;
    String name;
    String quant;//albums and tracks
    String url;//for small image
    String style;//genre
    String description;
    String link;
    String big_image;


    Person(int _id, String _describe, String _quant, String _url, String _style,
           String _description, String _link, String _big_image) {
        id = _id;
        name = _describe;
        quant = _quant;
        url = _url;
        style = _style;
        description = _description;
        link = _link;
        big_image = _big_image;
    }
}