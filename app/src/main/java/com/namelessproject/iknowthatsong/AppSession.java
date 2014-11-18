package com.namelessproject.iknowthatsong;

import android.app.Application;

import java.util.ArrayList;
import java.util.HashMap;

public class AppSession extends Application {
    private ArrayList<HashMap<String, String>> listOfSong = new ArrayList<HashMap<String, String>>();

    public void setListOfSong(ArrayList<HashMap<String, String>> listOfSong) {
        this.listOfSong = listOfSong;
    }

    public ArrayList<HashMap<String, String>> getListOfSong() {
        return this.listOfSong;
    }
}