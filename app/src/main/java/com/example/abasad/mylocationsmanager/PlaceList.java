package com.example.abasad.mylocationsmanager;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;


 public  class PlaceList {

    public  List<Place> list;

    public PlaceList(){
        list = new ArrayList<Place>();
    }

    public void add(Place p){
        list.add(p);
    }

    public void remove(Place p){
        list.remove(p);


    }

}