package com.example.ripzery.projectx01.app;

import com.example.ripzery.projectx01.interface_model.Monster;

import java.util.ArrayList;

/**
 * Created by visit on 1/15/15 AD.
 */
public class Singleton {
    private static Singleton mSing = new Singleton();
    private static ArrayList<Monster> allMonsters;

    private Singleton() {
    }

    /* Static 'instance' method */
    public static Singleton getInstance() {
        return mSing;
    }

    /* Other methods protected by singleton-ness */
    protected static void setAllMonsters(ArrayList<Monster> listMonsters) {
        allMonsters = listMonsters;
    }

    public static ArrayList<Monster> getAllMonsters() {
        return allMonsters;
    }
}
