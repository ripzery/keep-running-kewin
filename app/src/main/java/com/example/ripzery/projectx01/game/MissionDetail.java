package com.example.ripzery.projectx01.game;

import com.example.ripzery.projectx01.R;
import com.example.ripzery.projectx01.util.MissionData;

import java.util.ArrayList;

/**
 * Created by oakraw90 on 11/4/2014.
 */
public class MissionDetail {
    private ArrayList<MissionData> missionDatas = new ArrayList<MissionData>();

    public MissionDetail() {
        missionDatas.add(new MissionData("Tutorial", "", R.drawable.single_player_ic, 1000));
        missionDatas.add(new MissionData("Mission 1", "", R.drawable.single_player_ic, 1000));
        missionDatas.add(new MissionData("Mission 2", "", R.drawable.single_player_ic, 1100));
        missionDatas.add(new MissionData("Mission 3", "", R.drawable.single_player_ic, 1100));
        missionDatas.add(new MissionData("Mission 4", "", R.drawable.single_player_ic, 1210));
        missionDatas.add(new MissionData("Mission 5", "", R.drawable.single_player_ic, 1210));
        missionDatas.add(new MissionData("Mission 6", "", R.drawable.single_player_ic, 1320));
        missionDatas.add(new MissionData("Mission 7", "", R.drawable.single_player_ic, 1320));
        missionDatas.add(new MissionData("Mission 8", "", R.drawable.single_player_ic, 1500));
    }

    public ArrayList<MissionData> getMissionDatas() {
        return missionDatas;
    }
}
