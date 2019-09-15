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

    private ArrayList<Spot> largeData;

    public int setLargeData(ArrayList<Spot> largeData) {
        this.largeData = largeData;
        return ++sync;
    }

    public ArrayList<Spot> getLargeData(int request) {
        return (request == sync) ? largeData : null;
    }
}