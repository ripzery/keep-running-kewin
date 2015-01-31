package com.example.ripzery.projectx01.ar.detail;

import com.example.ripzery.projectx01.interface_model.Item;
import com.example.ripzery.projectx01.model.weapon.Gun;

import java.util.ArrayList;

/**
 * Created by Rawipol on 1/13/15 AD.
 */
public class Me {
    public static final float myMaxHP = 70;
    public static float myHP = myMaxHP;
    public static int distanceMultiplier = 1;

    public static ArrayList<Item> items = new ArrayList<Item>();
    public static ArrayList<Gun> guns = new ArrayList<Gun>();
    public static int chosenGun;
    public static int chosenItem;

    public static boolean selectGun = false;
    public static boolean selectItem = false;

    public static int bagMaxCapacity = 12;

    public static int weight = 70;
    public static int totalDuration = 0;
    public static float highestSpeed = 0;
    public static double averageSpeed = 0;
    public static float calories = 0;


}


