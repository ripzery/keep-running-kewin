package com.example.ripzery.projectx01.util;

import android.util.Log;

import com.example.ripzery.projectx01.app.MultiplayerActivity;
import com.example.ripzery.projectx01.app.Singleton;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;

import java.util.List;

/**
 * Created by visit on 2/8/15 AD.
 */
public class MyUpdateRoomListener implements RoomStatusUpdateListener, RoomUpdateListener {

    public static final String TAG = "MyUpdateRoomListener";

    public MultiplayerActivity multiplayerActivity;

    public MyUpdateRoomListener(MultiplayerActivity multiplayerActivity) {
        this.multiplayerActivity = multiplayerActivity;
    }

    @Override
    public void onRoomConnecting(Room room) {
        Singleton.updateRoom(room);
    }

    @Override
    public void onRoomAutoMatching(Room room) {
        Singleton.updateRoom(room);
    }

    @Override
    public void onPeerInvitedToRoom(Room room, List<String> strings) {
        Singleton.updateRoom(room);
    }

    @Override
    public void onPeerDeclined(Room room, List<String> strings) {
        Singleton.updateRoom(room);
    }

    @Override
    public void onPeerJoined(Room room, List<String> strings) {
        Singleton.updateRoom(room);
    }

    @Override
    public void onPeerLeft(Room room, List<String> strings) {
        Singleton.updateRoom(room);
    }

    @Override
    public void onConnectedToRoom(Room room) {
        Log.d(TAG, "onConnectedToRoom.");

        // get room ID, participants and my ID:
        Singleton.mRoomId = room.getRoomId();
        multiplayerActivity.mParticipants = room.getParticipants();
        Singleton.mParticipants = room.getParticipants();
        Singleton.myId = room.getParticipantId(Games.Players.getCurrentPlayerId(Singleton.mGoogleApiClient));

        // print out the list of participants (for debug purposes)
        Log.d(TAG, "Room ID: " + Singleton.mRoomId);
        Log.d(TAG, "My ID " + Singleton.myId);
        Log.d(TAG, "<< CONNECTED TO ROOM>>");
    }

    @Override
    public void onDisconnectedFromRoom(Room room) {
        Singleton.mRoomId = null;
        multiplayerActivity.showGameError();
    }


    @Override
    public void onPeersConnected(Room room, List<String> strings) {
        Singleton.updateRoom(room);
    }

    @Override
    public void onPeersDisconnected(Room room, List<String> strings) {
        Singleton.updateRoom(room);
    }

    @Override
    public void onP2PConnected(String s) {

    }

    @Override
    public void onP2PDisconnected(String s) {

    }

    @Override
    public void onRoomCreated(int statusCode, Room room) {
        Log.d(TAG, "onRoomCreated(" + statusCode + ", " + room + ")");
        if (statusCode != GamesStatusCodes.STATUS_OK) {
            Log.e(TAG, "*** Error: onRoomCreated, status " + statusCode);
            multiplayerActivity.showGameError();
            return;
        }

        // show the waiting room UI
        multiplayerActivity.showWaitingRoom(room);
    }

    @Override
    public void onJoinedRoom(int statusCode, Room room) {
        Log.d(TAG, "onJoinedRoom(" + statusCode + ", " + room + ")");
        if (statusCode != GamesStatusCodes.STATUS_OK) {
            Log.e(TAG, "*** Error: onRoomConnected, status " + statusCode);
            multiplayerActivity.showGameError();
            return;
        }

        // show the waiting room UI
        multiplayerActivity.showWaitingRoom(room);
    }

    @Override
    public void onLeftRoom(int statusCode, String s) {
        // we have left the room; return to main screen.
        Log.d(TAG, "onLeftRoom, code " + statusCode);
        multiplayerActivity.switchToMainScreen();
    }

    @Override
    public void onRoomConnected(int statusCode, Room room) {
        Log.d(TAG, "onRoomConnected(" + statusCode + ", " + room + ")");
        if (statusCode != GamesStatusCodes.STATUS_OK) {
            Log.e(TAG, "*** Error: onRoomConnected, status " + statusCode);
            multiplayerActivity.showGameError();
            return;
        }
        Singleton.updateRoom(room);
    }

}
