package com.example.ripzery.projectx01.ar.detail;

import com.example.ripzery.projectx01.ar.detail.weapon.Gun;
import com.example.ripzery.projectx01.interface_model.Item;

import java.util.ArrayList;

/**
 * Created by Rawipol on 1/13/15 AD.
 */
public class Me {
    public static final float myMaxHP = 100;
    public static float myHP = myMaxHP;

    public static ArrayList<Item> items = new ArrayList<Item>();
    public static ArrayList<Gun> guns = new ArrayList<Gun>();
    public static int chosenGun;

}
