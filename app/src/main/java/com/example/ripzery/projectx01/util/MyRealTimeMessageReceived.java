package com.example.ripzery.projectx01.util;

import android.util.Log;

import com.example.ripzery.projectx01.app.MapsMultiplayerActivity;
import com.example.ripzery.projectx01.app.MultiplayerMapsActivity;
import com.example.ripzery.projectx01.app.Singleton;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;

/**
 * Created by visit on 2/8/15 AD.
 */
public class MyRealTimeMessageReceived implements RealTimeMessageReceivedListener {

    private static final String TAG = "MyRealtimeMessage";
    private MapsMultiplayerActivity mapsMultiplayerActivity;
    private MultiplayerMapsActivity multiplayerMapsActivity;
    private String time = "Time : ";

    public static int byteArrayToInt(byte[] b) {
        return b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }

    @Override
    public void onRealTimeMessageReceived(RealTimeMessage realTimeMessage) {
        byte[] buf = realTimeMessage.getMessageData();
        String sender = realTimeMessage.getSenderParticipantId();
        byte[] distanceByte = new byte[4];
        char header = (char) buf[0];
        for (int i = 0; i < distanceByte.length; i++)
            distanceByte[i] = buf[i + 1];

        byte[] damage = new byte[4];
        for (int i = 0; i < damage.length; i++) {
            damage[i] = buf[i + 5];
        }

        String distance = "Distance : " + byteArrayToInt(distanceByte);
        String damaged = "Damaged : " + byteArrayToInt(damage);
        Log.d(TAG, "Message received: " + (char) buf[0] + "/" + byteArrayToInt(distanceByte));

        // Final result
        if (header == 'F') {
            byte[] hour = new byte[4];
            byte[] min = new byte[4];
            byte[] sec = new byte[4];
            for (int i = 0; i < hour.length; i++) {
                hour[i] = buf[i + 9];
            }
            for (int i = 0; i < min.length; i++) {
                min[i] = buf[i + 13];
            }
            for (int i = 0; i < sec.length; i++) {
                sec[i] = buf[i + 17];
            }

            time = "Time : " + byteArrayToInt(hour) + " H " + byteArrayToInt(min) + " M " + byteArrayToInt(sec) + " S ";
        }

        byte[] killed = new byte[4];
        for (int i = 0; i < damage.length; i++) {
            killed[i] = buf[i + 21];
        }

        String kill = "Killed : " + byteArrayToInt(killed);


        //TODO : Who is sender ?
        Log.d(TAG, " All participants : " + Singleton.mParticipants.size());
        Log.d(TAG, " Player 0 : " + Singleton.mParticipants.get(0).getDisplayName());
        Log.d(TAG, " Player 1 : " + Singleton.mParticipants.get(1).getDisplayName());
        Log.d(TAG, " Player 2 : " + Singleton.mParticipants.get(2).getDisplayName());

        int otherPlayerIndex = 2; // use for set text at the correct place

        for (int i = 0; i < Singleton.mParticipants.size(); i++) {

            //find sender
            if (Singleton.mParticipants.get(i).getParticipantId().equals(sender)) {
                multiplayerMapsActivity.getFragmentMultiplayerStatus().getTextView("p" + otherPlayerIndex, 1).setText(distance);
                multiplayerMapsActivity.getFragmentMultiplayerStatus().getTextView("p" + otherPlayerIndex, 0).setText(damaged);
                multiplayerMapsActivity.getFragmentMultiplayerStatus().getTextView("p" + otherPlayerIndex, 3).setText(kill);
                if (header == 'F') {
                    multiplayerMapsActivity.getFragmentMultiplayerStatus().getTextView("p" + otherPlayerIndex, 2).setText(time);
                }
            }

            if (!Singleton.mParticipants.get(i).getParticipantId().equals(Singleton.myId)) {
                otherPlayerIndex++;
            }
        }
    }

    public void setMapsMultiplayerActivity(MapsMultiplayerActivity mapsMultiplayerActivity) {
        this.mapsMultiplayerActivity = mapsMultiplayerActivity;
    }

    public void setMultiplayerMapsActivity(MultiplayerMapsActivity multiplayerMapsActivity) {
        this.multiplayerMapsActivity = multiplayerMapsActivity;
    }
}
