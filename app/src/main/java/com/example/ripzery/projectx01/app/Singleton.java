package com.example.ripzery.projectx01.app;

import com.example.ripzery.projectx01.interface_model.Monster;
import com.example.ripzery.projectx01.util.MyRealTimeMessageReceived;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by visit on 1/15/15 AD.
 */
public class Singleton {
    public static ArrayList<Participant> mParticipants = null;
    public static GoogleApiClient mGoogleApiClient;
    public static String myId = null;
    public static String mRoomId;
    public static MyRealTimeMessageReceived myRealTimeMessageReceived;
    private static Singleton mSing = new Singleton();
    private static ArrayList<Monster> allMonsters;
    private static ArrayList<LatLng> allPlayerPositions;

    private Singleton() {
    }

    /* Static 'instance' method */
    public static Singleton getInstance() {
        return mSing;
    }

    public static ArrayList<Monster> getAllMonsters() {
        return allMonsters;
    }

    /* Other methods protected by singleton-ness */
    protected static void setAllMonsters(ArrayList<Monster> listMonsters) {
        allMonsters = listMonsters;
    }

    public static ArrayList<LatLng> getAllPlayerPositions() {
        return allPlayerPositions;
    }

    protected static void setAllPlayerPositions(ArrayList<LatLng> allPositions) {
        allPlayerPositions = allPositions;
    }

    public static void updateRoom(Room room) {
        if (room != null) {
            mParticipants = room.getParticipants();
        }
    }

    public static Participant getParticipantFromId(String id) {
        for (int i = 0; i < Singleton.mParticipants.size(); i++) {
            if (Singleton.mParticipants.get(i).getParticipantId().equals(id)) {
                return Singleton.mParticipants.get(i);
            }
        }
        return null;
    }

}
