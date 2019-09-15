package de.ur.hikingspots;

import java.util.ArrayList;
import java.util.List;

public class Singleton {

    private static Singleton instance;

    public synchronized static Singleton get() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }

    private int sync = 0;

    private ArrayList<Spot> passSpotlist;

    public int setSpotList(ArrayList<Spot> largeData) {
        this.passSpotlist = largeData;
        return ++sync;
    }

    public ArrayList<Spot> getSpotlist(int request) {
        return (request == sync) ? passSpotlist : null;
    }
}