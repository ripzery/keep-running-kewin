package com.example.ripzery.projectx01.util;

/**
 * Created by oakraw90 on 11/4/2014.
 */
public class MissionData {
    private String name;
    private String description;
    private int thumbnail;

    public MissionData(String name, String description, int thumbnail) {
        this.name = name;
        this.description = description;
        this.thumbnail = thumbnail;
    }

    public String getName() {
        return name;
    }

    public int getThumbnail() {
        return thumbnail;
    }
}
